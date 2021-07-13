package com.example.househub;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

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