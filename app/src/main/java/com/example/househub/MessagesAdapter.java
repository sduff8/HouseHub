package com.example.househub;

import android.graphics.Color;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.househub.Model.Messages;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageViewHolder> {

    private List<Messages> userMessagesList;
    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef;

    public MessagesAdapter(List<Messages> userMessagesList){
        this.userMessagesList = userMessagesList;
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder{

        public TextView senderMessageText, receiverMessageText, senderMessageDate, receiverMessageDate, receiverMessageName;
        public CircleImageView receiverProfileImage;

        public MessageViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            senderMessageText = (TextView) itemView.findViewById(R.id.sender_message_text);
            senderMessageDate = (TextView) itemView.findViewById(R.id.sender_message_date);
            receiverMessageText = (TextView) itemView.findViewById(R.id.received_message_text);
            receiverProfileImage = (CircleImageView) itemView.findViewById(R.id.message_profile_image);
            receiverMessageDate = (TextView) itemView.findViewById(R.id.received_message_date);
            receiverMessageName = (TextView) itemView.findViewById(R.id.received_message_username);

        }
    }

    @NonNull
    @NotNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_message_layout, parent, false);

        mAuth = FirebaseAuth.getInstance();

        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MessagesAdapter.MessageViewHolder holder, int position) {
        String messageSenderID = mAuth.getCurrentUser().getUid();
        Messages messages = userMessagesList.get(position);

        String date = messages.getDate();
        String message = messages.getMessage();
        String name = messages.getName();
        String time = messages.getTime();
        String type = messages.getType();
        String uid = messages.getUid();

        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.hasChild("image")){
                    String receiverImage = snapshot.child("image").getValue().toString();

                    Picasso.get().load(receiverImage).placeholder(R.drawable.profile_image).into(holder.receiverProfileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        if(type.equals("text")){
            holder.receiverMessageText.setVisibility(View.INVISIBLE);
            holder.receiverProfileImage.setVisibility(View.INVISIBLE);
            holder.receiverMessageDate.setVisibility(View.INVISIBLE);
            holder.receiverMessageName.setVisibility(View.INVISIBLE);
            holder.senderMessageText.setVisibility(View.INVISIBLE);
            holder.senderMessageDate.setVisibility(View.INVISIBLE);

            if (uid.equals(messageSenderID)){
                holder.senderMessageText.setVisibility(View.VISIBLE);
                holder.senderMessageDate.setVisibility(View.VISIBLE);

                holder.senderMessageText.setBackgroundResource(R.drawable.background_right);
                holder.senderMessageText.setTextColor(Color.BLACK);
                holder.senderMessageText.setText(message);

                holder.senderMessageDate.setTextColor(Color.BLACK);
                holder.senderMessageDate.setText(date);
            }
            else{
                holder.receiverProfileImage.setVisibility(View.VISIBLE);
                holder.receiverMessageText.setVisibility(View.VISIBLE);
                holder.receiverMessageDate.setVisibility(View.VISIBLE);
                holder.receiverMessageName.setVisibility(View.VISIBLE);

                holder.receiverMessageText.setBackgroundResource(R.drawable.background_left);
                holder.receiverMessageText.setTextColor(Color.BLACK);
                holder.receiverMessageText.setText(message);

                holder.receiverMessageName.setTextColor(Color.BLACK);
                holder.receiverMessageName.setText(name);

                holder.receiverMessageDate.setText(date);
            }
        }
    }

    @Override
    public int getItemCount() {
        return userMessagesList.size();
    }
}
