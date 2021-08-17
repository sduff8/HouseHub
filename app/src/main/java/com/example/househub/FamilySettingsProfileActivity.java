package com.example.househub;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class FamilySettingsProfileActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private EditText familyNameSet;
    private Button updateFamilyButton;
    private TextView familyInviteId;
    private ImageView familyImage;

    private String currentUserID, familyNameId;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseRef;
    private StorageReference FamilyImagesRef;

    private static final int GalleryPick = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_settings_profile);

        familyNameSet = findViewById(R.id.family_name_set);
        updateFamilyButton = findViewById(R.id.create_family_button);
        familyInviteId = findViewById(R.id.family_invite_id);
        familyImage = findViewById(R.id.family_image);

        familyNameId = GlobalVars.getFamilyNameId();

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        databaseRef = FirebaseDatabase.getInstance().getReference();

        FamilyImagesRef = FirebaseStorage.getInstance().getReference().child("Family Images");

        mToolbar = findViewById(R.id.family_settings_profile_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Family Settings");

        familyInviteId.setText(familyNameId);

        RetrieveFamilyProfile();

        updateFamilyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateFamilyInformation();
            }
        });

        familyImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GalleryPick);
            }
        });
    }

    //Handles selecting photo from gallery, saving photo to firebase storage, and displaying to layout
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GalleryPick && resultCode == RESULT_OK && data!=null){
                Uri ImageUri = data.getData();


                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("Uploading Family Image...");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                final StorageReference filePath = FamilyImagesRef.child(currentUserID + ".jpg");

                UploadTask uploadTask= (UploadTask) filePath.putFile(ImageUri).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull @NotNull UploadTask.TaskSnapshot snapshot) {
                        double progressPercent = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                        progressDialog.setMessage("Percentage: " + (int) progressPercent + "%");
                    }
                });
                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        // Continue with the task to get the download URL
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            Uri downloadUri = task.getResult();
                            Toast.makeText(FamilySettingsProfileActivity.this, "Successfully uploaded", Toast.LENGTH_SHORT).show();
                            if (downloadUri != null) {

                                String downloadUrl = downloadUri.toString(); //YOU WILL GET THE DOWNLOAD URL HERE !!!!
                                databaseRef.child("Families").child(familyNameId).child("image").setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if(!task.isSuccessful()){
                                            String error=task.getException().toString();
                                            Toast.makeText(FamilySettingsProfileActivity.this,"Error : "+error,Toast.LENGTH_LONG).show();
                                        }else{

                                        }
                                    }
                                });
                            }

                        } else {
                            // Handle failures
                            // ...
                            Toast.makeText(FamilySettingsProfileActivity.this,"Error",Toast.LENGTH_LONG).show();
                            //loadingBar.dismiss();
                        }
                    }
                });
        }
    }

    private void RetrieveFamilyProfile() {
        databaseRef.child("Families").child(familyNameId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.exists() && (snapshot.hasChild("name"))){
                    String retrieveName = snapshot.child("name").getValue().toString();
                    familyNameSet.setText(retrieveName);
                }
                if (snapshot.exists() && (snapshot.hasChild("image"))){
                    String retrieveImage = snapshot.child("image").getValue().toString();
                    Glide.with(getApplicationContext()).load(retrieveImage).into(familyImage);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void UpdateFamilyInformation() {
        String setFullName = String.valueOf(familyNameSet.getText());

        if (setFullName.equals("")){
            Toast.makeText(FamilySettingsProfileActivity.this, "Please Enter Family Name", Toast.LENGTH_SHORT).show();
        }
        else{
            HashMap<String, Object> profileMap = new HashMap<>();
            profileMap.put("fid", familyNameId);
            profileMap.put("name", setFullName);
            databaseRef.child("Families").child(familyNameId).updateChildren(profileMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(FamilySettingsProfileActivity.this, "Family Updated Successfully", Toast.LENGTH_SHORT).show();
                        sendUserToMainActivity();
                    }
                    else{
                        String message = task.getException().toString();
                        Toast.makeText(FamilySettingsProfileActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) // Press Back Icon
        {
            Intent intent = new Intent(FamilySettingsProfileActivity.this, SettingsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void sendUserToMainActivity() {
        Intent mainIntent = new Intent(FamilySettingsProfileActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}