package com.example.househub;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactsProfileActivity extends AppCompatActivity {

    private Button updateContact;
    private EditText username, phone, address;
    private CircleImageView contactImage;
    private Toolbar mToolbar;

    private String currentUserID, visitContactId, contactName, familyNameId;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseRef;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    private static final int ContactPick = 1;
    private StorageReference ContactImagesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_profile);

        visitContactId = getIntent().getExtras().get("visitContactId").toString();
        contactName = getIntent().getExtras().get("contactName").toString();

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        databaseRef = FirebaseDatabase.getInstance().getReference();
        familyNameId = GlobalVars.getFamilyNameId();

        updateContact = findViewById(R.id.update_contact_button);
        username = findViewById(R.id.contact_name_set);
        phone = findViewById(R.id.contact_phone_set);
        address = findViewById(R.id.contact_address_set);
        contactImage = findViewById(R.id.contact_image_set);
        ContactImagesRef = FirebaseStorage.getInstance().getReference().child("Contact Images");
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        mToolbar = findViewById(R.id.contact_profile_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(contactName);

        updateContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateContactInformation();
            }
        });

        RetrieveUserProfile();

        contactImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, ContactPick);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == ContactPick && resultCode == RESULT_OK && data!=null){
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

                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("Updating Image...");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                final StorageReference filePath = storageReference.child("Contact Images/" + visitContactId + ".jpg");

                UploadTask uploadTask= (UploadTask) filePath.putFile(resultUri).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
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
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            Uri downloadUri = task.getResult();
                            Toast.makeText(ContactsProfileActivity.this, "Successfully uploaded", Toast.LENGTH_SHORT).show();
                            if (downloadUri != null) {

                                String downloadUrl = downloadUri.toString();
                                databaseRef.child("Contacts").child(familyNameId).child(visitContactId).child("image").setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        //loadingBar.dismiss();
                                        if(!task.isSuccessful()){
                                            String error=task.getException().toString();
                                            Toast.makeText(ContactsProfileActivity.this,"Error : "+error,Toast.LENGTH_LONG).show();
                                        }else{

                                        }
                                    }
                                });
                            }

                        } else {
                            Toast.makeText(ContactsProfileActivity.this,"Error",Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }
    }

    private void RetrieveUserProfile() {
        databaseRef.child("Contacts").child(familyNameId).child(visitContactId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.exists() && (snapshot.hasChild("name"))){
                    String retrieveName = snapshot.child("name").getValue().toString();
                    username.setText(retrieveName);
                }
                if (snapshot.exists() && (snapshot.hasChild("phone"))){
                    String retrievePhone = snapshot.child("phone").getValue().toString();
                    phone.setText(retrievePhone);
                }
                if (snapshot.exists() && (snapshot.hasChild("name"))){
                    String retrieveAddress = snapshot.child("address").getValue().toString();
                    address.setText(retrieveAddress);
                }
                if (snapshot.exists() && (snapshot.hasChild("image"))){
                    String retrieveImage = snapshot.child("image").getValue().toString();
                    Glide.with(getApplicationContext()).load(retrieveImage).into(contactImage);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void UpdateContactInformation() {
        String setFullName = String.valueOf(username.getText());
        String setPhone = String.valueOf(phone.getText());
        String setAddress = String.valueOf(address.getText());

        if (setFullName.equals("")){
            Toast.makeText(ContactsProfileActivity.this, "Please Enter Name", Toast.LENGTH_SHORT).show();
        }
        else{
            HashMap<String, Object> profileMap = new HashMap<>();
            profileMap.put("name", setFullName);
            profileMap.put("phone", setPhone);
            profileMap.put("address", setAddress);
            databaseRef.child("Contacts").child(familyNameId).child(visitContactId).updateChildren(profileMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(ContactsProfileActivity.this, "Contact Updated Successfully", Toast.LENGTH_SHORT).show();
                        sendUserToMainActivity();
                    }
                    else{
                        String message = task.getException().toString();
                        Toast.makeText(ContactsProfileActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) // Press Back Icon
        {
            Intent intent = new Intent(ContactsProfileActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void sendUserToMainActivity() {
        Intent mainIntent = new Intent(ContactsProfileActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}