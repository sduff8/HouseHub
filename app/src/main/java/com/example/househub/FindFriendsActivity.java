package com.example.househub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.example.househub.Model.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindFriendsActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private RecyclerView mRecyclerList;
    private DatabaseReference UsersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);

        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        mRecyclerList = findViewById(R.id.find_friends_recycler_list);
        mRecyclerList.setLayoutManager(new LinearLayoutManager(this));

        mToolbar = findViewById(R.id.find_friends_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Add Family Members");
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>().setQuery(UsersRef, User.class).build();

        FirebaseRecyclerAdapter<User, FindFriendsViewHolder> adapter = new FirebaseRecyclerAdapter<User, FindFriendsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull @NotNull FindFriendsActivity.FindFriendsViewHolder holder, int position, @NonNull @NotNull User model) {
                holder.username.setText(model.getName());
                Picasso.get().load(model.getImage()).placeholder(R.drawable.profile_image).into(holder.profileImage);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String visitUserId = getRef(position).getKey();

                        Intent profileIntent = new Intent(FindFriendsActivity.this, ProfileActivity.class);
                        profileIntent.putExtra("visitUserId", visitUserId);
                        startActivity(profileIntent);
                    }
                });
            }

            @NonNull
            @NotNull
            @Override
            public FindFriendsViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_layout, parent, false);
                FindFriendsViewHolder viewHolder = new FindFriendsViewHolder(view);
                return viewHolder;
            }
        };
        mRecyclerList.setAdapter(adapter);

        adapter.startListening();
    }

    public static class FindFriendsViewHolder extends RecyclerView.ViewHolder{

        TextView username;
        CircleImageView profileImage;

        public FindFriendsViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username_add_friends);
            profileImage = itemView.findViewById(R.id.users_profile_image_add_friends);
        }
    }
}