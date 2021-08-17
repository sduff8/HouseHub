package com.example.househub;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class SignupActivity extends AppCompatActivity {

    private TextInputEditText password, email;
    private Button buttonSignup;
    private TextView signupText;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseRef, UsersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        email = findViewById(R.id.signupEmailText);
        password = findViewById(R.id.signupPasswordText);
        buttonSignup = findViewById(R.id.buttonSignup);
        signupText = findViewById(R.id.textViewSignup);
        progressBar = findViewById(R.id.signupProgress);

        mAuth = FirebaseAuth.getInstance();
        databaseRef = FirebaseDatabase.getInstance().getReference();
        UsersRef = databaseRef.child("Users");

        signupText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewAccount();
            }
        });

    }

    private void createNewAccount() {
        String newEmail, newPassword;
        newEmail = String.valueOf(email.getText());
        newPassword = String.valueOf(password.getText());

        if (newEmail.equals("") || newPassword.equals("")) {
            Toast.makeText(SignupActivity.this, "Please Enter Email & Password", Toast.LENGTH_SHORT).show();
        }
        else{
            progressBar.setVisibility(View.VISIBLE);
            mAuth.createUserWithEmailAndPassword(newEmail, newPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        String currentUserID = mAuth.getUid();
                        databaseRef.child("Users").child(currentUserID).setValue("");
                        mAuth.signOut();
                        sendUserToLoginActivity();
                        Toast.makeText(SignupActivity.this, "Account Created", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                    else{
                        String message = task.getException().toString();
                        Toast.makeText(SignupActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                }
            });
        }
    }

    private void sendUserToMainActivity() {
        Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    private void sendUserToLoginActivity() {
        Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }

    public void setUserFirebaseMessagingTokenAndSignup() {
        FirebaseMessaging.getInstance ().getToken ()
                .addOnCompleteListener ( task -> {
                    if (!task.isSuccessful ()) {
                        //Could not get FirebaseMessagingToken
                        return;
                    }
                    if (null != task.getResult ()) {
                        //Got FirebaseMessagingToken
                        String firebaseMessagingToken = Objects.requireNonNull ( task.getResult () );
                        String currentUserID = mAuth.getCurrentUser().getUid();
                        UsersRef.child(currentUserID).child("device_token").setValue(firebaseMessagingToken).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    sendUserToMainActivity();
                                    Toast.makeText(SignupActivity.this, "Account Created", Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                }
                                else{
                                    String message = task.getException().toString();
                                    Toast.makeText(SignupActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                }
                            }
                        });
                    }
                } );
    }

}
/*
public class SignupActivity extends AppCompatActivity {

    TextInputEditText textInputEditTextUsername, textInputEditTextPassword, textInputEditTextPhoneNumber, textInputEditTextEmail;
    Button buttonSignup;
    TextView textViewSignup;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        textInputEditTextUsername = findViewById(R.id.signupUsernameText);
        textInputEditTextPassword = findViewById(R.id.signupPasswordText);
        textInputEditTextPhoneNumber = findViewById(R.id.signupPhoneText);
        textInputEditTextEmail = findViewById(R.id.signupEmailText);
        buttonSignup = findViewById(R.id.buttonSignup);
        textViewSignup = findViewById(R.id.textViewSignup);
        progressBar = findViewById(R.id.signupProgress);

        textViewSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username, password, phoneNumber, email;
                username = String.valueOf(textInputEditTextUsername.getText());
                password = String.valueOf(textInputEditTextPassword.getText());
                phoneNumber = String.valueOf(textInputEditTextPhoneNumber.getText());
                email = String.valueOf(textInputEditTextEmail.getText());

                if (!username.equals("") && !password.equals("") && !phoneNumber.equals("") && !email.equals("")) {
                    progressBar.setVisibility(View.VISIBLE);
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(() -> {
                        String[] field = new String[4];
                        field[0] = "username";
                        field[1] = "password";
                        field[2] = "phone_number";
                        field[3] = "email";
                        String[] data = new String[4];
                        data[0] = username;
                        data[1] = password;
                        data[2] = phoneNumber;
                        data[3] = email;
                        PutData putData = new PutData("http://192.168.2.19/househub/signup.php", "POST", field, data);
                        if (putData.startPut()) {
                            if (putData.onComplete()) {
                                progressBar.setVisibility(View.GONE);
                                String result = putData.getResult();
                                String confirmResult = "\t\tSign Up Success";
                                if (result.equals(confirmResult)) {
                                    Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();

                                }
                            }
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "All fields required", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
*/