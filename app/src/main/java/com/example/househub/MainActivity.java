package com.example.househub;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private Toolbar mToolbar;

    private String currentUserID;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseRef, FamilyRef;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        currentUserID = mAuth.getCurrentUser().getUid();
        databaseRef = FirebaseDatabase.getInstance().getReference();

        //Objects.requireNonNull(getSupportActionBar()).hide();
        //getWindow().addFlags((WindowManager.LayoutParams.FLAG_FULLSCREEN));

        //Get Family Table Reference
        databaseRef.child("Users").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                String familyId = snapshot.child("Family").getValue().toString();
                GlobalVars.setFamilyNameId(familyId);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        mToolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("HouseHub");

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, new HomeFragment()).commit();
        bottomNavigationView.setSelectedItemId(R.id.homeFragment);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment fragment = null;
            switch (item.getItemId()){

                case R.id.homeFragment:
                    fragment = new HomeFragment();
                    break;

                case R.id.chatFragment:
                    fragment = new ChatFragment();
                    //FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    //Bundle bundle = new Bundle();
                    //bundle.putString("familyNameId", familyNameId);
                    //fragment.setArguments(bundle);
                    break;

                case R.id.galleryFragment:
                    fragment = new GalleryFragment();
                    break;

                case R.id.calendarFragment:
                    fragment = new CalendarFragment();
                    break;

                case R.id.contactsFragment:
                    fragment = new ContactsFragment();
                    break;
            }
            assert fragment != null;
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, fragment).commit();

            return true;
        });

        //CheckUserFamily();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.options_menu,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.add_friends){
            sendUserToFindFriendsActivity();
        }

        if (item.getItemId() == R.id.family_settings){
            sendUserToFamilySettingsActivity();
        }

        if (item.getItemId() == R.id.update_profile){
            sendUserToEditProfile();
        }

        if (item.getItemId() == R.id.settings){

        }

        if (item.getItemId() == R.id.logout){
            mAuth.signOut();
            sendUserToLoginActivity();
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (currentUser == null) {
            sendUserToLoginActivity();
        }
        else{
            VerifyUser();
        }
    }

    private void CheckUserFamily(){
        String currentUserID = mAuth.getCurrentUser().getUid();

        databaseRef.child("Users").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.child("family").exists()){
                    enableBottomBar(true);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                enableBottomBar(false);
            }
        });
    }

    private void VerifyUser() {
        String currentUserID = mAuth.getCurrentUser().getUid();

        databaseRef.child("Users").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.exists() && (snapshot.hasChild("name"))){
                    Toast.makeText(MainActivity.this, "Welcome", Toast.LENGTH_SHORT).show();
                }
                else{
                    sendUserToEditProfile();
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
            }
        });
    }

    private void enableBottomBar(boolean enable){
        for (int i = 0; i < bottomNavigationView.getMenu().size(); i++) {
            bottomNavigationView.getMenu().getItem(i).setEnabled(enable);
        }
    }

    private void sendUserToEditProfile() {
        Intent editProfileIntent = new Intent(MainActivity.this, EditProfileActivity.class);
        startActivity(editProfileIntent);
    }

    private void sendUserToLoginActivity() {
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }

    private void sendUserToFamilySettingsActivity() {
        Intent familySettingsIntent = new Intent(MainActivity.this, FamilySettingsActivity.class);
        familySettingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(familySettingsIntent);
        finish();
    }

    private void sendUserToFindFriendsActivity() {
        Intent addFriendsIntent = new Intent(MainActivity.this, FindFriendsActivity.class);
        addFriendsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(addFriendsIntent);
        finish();
    }

    /*private void RequestNewGroup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.AlertDialog);
        builder.setTitle("Enter Family Name: ");

        final EditText groupNameField = new EditText(MainActivity.this);
        groupNameField.setHint("ex. The Duffs");
        builder.setView(groupNameField);

        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String groupName = groupNameField.getText().toString();

                if (TextUtils.isEmpty(groupName)){
                    Toast.makeText(MainActivity.this, "Please Enter Family Name", Toast.LENGTH_SHORT);
                }
                else{
                    CreateNewGroup(groupName);
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void CreateNewGroup(String groupName) {
        databaseRef.child("Groups").child(groupName).setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(MainActivity.this, groupName + "was created successfully", Toast.LENGTH_SHORT);
                }
            }
        });
        */
}
