package com.example.househub;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;

public class FamilySettingsActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private EditText familyNameSet, joinFamilyText;
    private Button createFamilyButton, joinFamilyButton;

    private String currentUserID;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_settings);

        familyNameSet = findViewById(R.id.family_name_create);
        createFamilyButton = findViewById(R.id.create_family_button);
        joinFamilyText = findViewById(R.id.join_family_id);
        joinFamilyButton = findViewById(R.id.join_family_button);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        databaseRef = FirebaseDatabase.getInstance().getReference();

        mToolbar = findViewById(R.id.family_settings_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Family Settings");

        createFamilyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewFamily();
            }
        });

        joinFamilyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                joinFamily();
            }
        });
    }

    private void joinFamily() {
        String familyName, userID, familyKey;
        familyName = String.valueOf(joinFamilyText.getText());
        userID = currentUserID;

        if (familyName.equals("")){
            Toast.makeText(FamilySettingsActivity.this, "Please Enter Family Name", Toast.LENGTH_SHORT).show();
        }
        else{
            databaseRef.child("Users").child(userID).child("family").setValue(familyName).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(FamilySettingsActivity.this, "Family has been joined", Toast.LENGTH_SHORT).show();
                        sendUserToMainActivity();
                    }
                    else{
                        String message = task.getException().toString();
                        Toast.makeText(FamilySettingsActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void createNewFamily() {
        String familyName;
        familyName = String.valueOf(familyNameSet.getText());

        final String randomKey = UUID.randomUUID().toString();
        final String familyKey = randomKey.substring(0, 8);

        if (familyName.equals("")){
            Toast.makeText(FamilySettingsActivity.this, "Please Enter Family Name", Toast.LENGTH_SHORT).show();
        }
        else{
            HashMap<String, Object> profileMap = new HashMap<>();
            profileMap.put("fid", familyKey);
            profileMap.put("name", familyName);
            databaseRef.child("Families").child(familyKey).setValue(profileMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<Void> task) {
                    if(task.isSuccessful()){
                        databaseRef.child("Users").child(currentUserID).child("family").setValue(familyKey);
                        Toast.makeText(FamilySettingsActivity.this, "Family was Created", Toast.LENGTH_SHORT).show();
                        sendUserToMainActivity();
                    }
                    else{
                        String message = task.getException().toString();
                        Toast.makeText(FamilySettingsActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
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
}