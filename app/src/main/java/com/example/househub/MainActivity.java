package com.example.househub;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    private String currentUserID;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseRef;

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

        if (item.getItemId() == R.id.settings){
            sendUserToSettingsActivity();
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
            VerifyUserProfileAndFamily();
            //CheckUserFamily();
        }
    }

//    private void CheckUserFamily(){
//        String currentUserID = mAuth.getCurrentUser().getUid();
//
//        databaseRef.child("Users").child(currentUserID).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
//                if (!(snapshot.exists() && (snapshot.hasChild("family")))){
//                    sendUserToFamilySettingsActivity();
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull @NotNull DatabaseError error) {
//            }
//        });
//    }

    private void VerifyUserProfileAndFamily() {
        String currentUserID = mAuth.getCurrentUser().getUid();

        databaseRef.child("Users").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (!(snapshot.exists() && (snapshot.hasChild("name")))){
                    sendUserToEditProfile();
                }
                if (!(snapshot.exists() && (snapshot.hasChild("family")))){
                    sendUserToFamilySettingsActivity();
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
            }
        });
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

    private void sendUserToSettingsActivity() {
        Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
        settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(settingsIntent);
        finish();
    }
}
