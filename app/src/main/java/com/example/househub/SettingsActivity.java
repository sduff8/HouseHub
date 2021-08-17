package com.example.househub;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApiNotAvailableException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class SettingsActivity extends AppCompatActivity {

    private ListView listView;
    private Toolbar toolbar;

    private FirebaseAuth mAuth;
    private String currentUserID;
    private DatabaseReference DatabaseRef;

    String[] items = new String[] { "Profile Settings", "Family Settings", "Leave Family" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        listView = (ListView) findViewById(R.id.settings_list_view);
        toolbar = (Toolbar) findViewById(R.id.settings_toolbar);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        DatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users");

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Settings");


        listView.setAdapter(new SettingsAdapter(this, items));
        listView.setTextFilterEnabled(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String selectedValue = (String) parent.getItemAtPosition(position);

                if(selectedValue.equals("Profile Settings")) {
                    sendUserToEditProfile();
                }
                else if(selectedValue.equals("Family Settings")) {
                    sendUserToFamilySettingsProfile();
                }
                else if(selectedValue.equals("Leave Family")) {
                    leaveFamily();
                }
            }
        });
    }

    private void leaveFamily() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Leave Family");
        builder.setMessage("Are you sure you want to leave?");

        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DatabaseRef.child(currentUserID).child("family").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        if (task.isSuccessful()){
                            sendUserToMainActivity();
                        }
                        else{
                            String message = task.getException().toString();
                            Toast.makeText(SettingsActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) // Press Back Icon
        {
            Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void sendUserToFamilySettingsProfile() {
        Intent familySettingsProfileIntent = new Intent(SettingsActivity.this, FamilySettingsProfileActivity.class);
        startActivity(familySettingsProfileIntent);
        finish();
    }

    private void sendUserToEditProfile() {
        Intent editProfileIntent = new Intent(SettingsActivity.this, EditProfileActivity.class);
        startActivity(editProfileIntent);
        finish();
    }

    private void sendUserToMainActivity() {
        Intent mainIntent = new Intent(SettingsActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }
}
