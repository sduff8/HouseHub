package com.example.househub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {

    private Button editProfile;
    private EditText username;
    private CircleImageView userProfileImage;

    private String currentUserID;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        databaseRef = FirebaseDatabase.getInstance().getReference();

        editProfile = findViewById(R.id.update_profile_button);
        username = findViewById(R.id.name_set);
        userProfileImage = findViewById(R.id.profile_image_set);

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateProfileInformation();
            }
        });

        RetrieveUserProfile();
    }

    private void RetrieveUserProfile() {
        databaseRef.child("Users").child(currentUserID).child("Profile").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.exists() && (snapshot.hasChild("name"))){
                    String retrieveName = snapshot.child("name").getValue().toString();
                    username.setText(retrieveName);
                }
                //if (snapshot.exists() && (snapshot.hasChild("image"))){
                    //String retrieveImage = snapshot.child("image").getValue().toString();
                //}
                else{
                    Toast.makeText(EditProfileActivity.this, "Please Set Name", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void UpdateProfileInformation() {
        String setFullName = String.valueOf(username.getText());

        if (setFullName.equals("")){
            Toast.makeText(EditProfileActivity.this, "Please Enter Name", Toast.LENGTH_SHORT).show();
        }
        else{
            HashMap<String, String> profileMap = new HashMap<>();
            profileMap.put("uid", currentUserID);
            profileMap.put("name", setFullName);
            databaseRef.child("Users").child(currentUserID).child("Profile").setValue(profileMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(EditProfileActivity.this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
                        sendUserToMainActivity();
                    }
                    else{
                        String message = task.getException().toString();
                        Toast.makeText(EditProfileActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void sendUserToMainActivity() {
        Intent mainIntent = new Intent(EditProfileActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

}