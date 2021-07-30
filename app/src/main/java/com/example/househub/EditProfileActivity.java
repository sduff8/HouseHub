 package com.example.househub;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {

    private Button editProfile;
    private EditText username;
    private CircleImageView userProfileImage;
    private Toolbar mToolbar;

    private String currentUserID;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseRef;

    private static final int GalleryPick = 1;
    private StorageReference UserProfileImagesRef;
    //private ProgressDialog loadingBar;

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
        UserProfileImagesRef = FirebaseStorage.getInstance().getReference().child("Profile Images");

        mToolbar = findViewById(R.id.edit_profile_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Edit Profile");

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateProfileInformation();
            }
        });

        RetrieveUserProfile();

        userProfileImage.setOnClickListener(new View.OnClickListener() {
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

            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if(resultCode == RESULT_OK){
                Uri resultUri = result.getUri();

                //loadingBar.setTitle("Set Profile Image");
                //loadingBar.setMessage("Profile image in updating...");
                //loadingBar.setCancelOnTouchOutside(false);
                //loadingBar.show();

                final StorageReference filePath = UserProfileImagesRef.child(currentUserID + ".jpg");

                UploadTask uploadTask=filePath.putFile(resultUri);
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
                            Uri downloadUri = task.getResult();
                            Toast.makeText(EditProfileActivity.this, "Successfully uploaded", Toast.LENGTH_SHORT).show();
                            if (downloadUri != null) {

                                String downloadUrl = downloadUri.toString(); //YOU WILL GET THE DOWNLOAD URL HERE !!!!
                                databaseRef.child("Users").child(currentUserID).child("image").setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        //loadingBar.dismiss();
                                        if(!task.isSuccessful()){
                                            String error=task.getException().toString();
                                            Toast.makeText(EditProfileActivity.this,"Error : "+error,Toast.LENGTH_LONG).show();
                                        }else{

                                        }
                                    }
                                });
                            }

                        } else {
                            // Handle failures
                            // ...
                            Toast.makeText(EditProfileActivity.this,"Error",Toast.LENGTH_LONG).show();
                            //loadingBar.dismiss();
                        }
                    }
                });
            }
        }
    }

    private void RetrieveUserProfile() {
        databaseRef.child("Users").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.exists() && (snapshot.hasChild("name"))){
                    String retrieveName = snapshot.child("name").getValue().toString();
                    username.setText(retrieveName);
                }
                if (snapshot.exists() && (snapshot.hasChild("image"))){
                    String retrieveImage = snapshot.child("image").getValue().toString();
                    Picasso.get().load(retrieveImage).into(userProfileImage);
                }
                //else{
                    //Toast.makeText(EditProfileActivity.this, "Please Set Name", Toast.LENGTH_SHORT).show();
                //}
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
            HashMap<String, Object> profileMap = new HashMap<>();
            profileMap.put("uid", currentUserID);
            profileMap.put("name", setFullName);
            databaseRef.child("Users").child(currentUserID).updateChildren(profileMap).addOnCompleteListener(new OnCompleteListener<Void>() {
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