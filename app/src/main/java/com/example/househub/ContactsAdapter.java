package com.example.househub;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.househub.Model.Contacts;
import com.example.househub.Model.Messages;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactsViewHolder>{

    private Context context;
    private List<Contacts> contactsList;
    private FirebaseAuth mAuth;
    private DatabaseReference ContactsRef;

    public ContactsAdapter(Context context, List<Contacts> contactsList){
        this.context = context;
        this.contactsList = contactsList;
    }

    public class ContactsViewHolder extends RecyclerView.ViewHolder{

        public TextView contactsNameText, contactsPhoneText, contactsAddressText;
        public CircleImageView contactProfileImage;

        public ContactsViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            contactsNameText = (TextView) itemView.findViewById(R.id.contacts_name);
            contactsPhoneText = (TextView) itemView.findViewById(R.id.contacts_phone_number);
            contactsAddressText = (TextView) itemView.findViewById(R.id.address_contacts);
            contactProfileImage = (CircleImageView) itemView.findViewById(R.id.contacts_recycler_image);
        }
    }

    @NonNull
    @NotNull
    @Override
    public ContactsAdapter.ContactsViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contacts_display_layout, parent, false);

        return new ContactsAdapter.ContactsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ContactsAdapter.ContactsViewHolder holder, int position) {
        Contacts contacts = contactsList.get(position);

        String cid = contacts.getCid();
        String name = contacts.getName();
        String phone = contacts.getPhone();
        String address = contacts.getAddress();
        String image = contacts.getImage();

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profileIntent = new Intent(context, ContactsProfileActivity.class);
                profileIntent.putExtra("visitContactId", cid);
                profileIntent.putExtra("contactName", name);
                context.startActivity(profileIntent);
            }
        });


        holder.contactsNameText.setText(name);
        holder.contactsPhoneText.setText(phone);
        holder.contactsAddressText.setText(address);
        if(image != null) {
            Glide.with(context).load(image).placeholder(R.drawable.profile_image).into(holder.contactProfileImage);
        }
    }

    @Override
    public int getItemCount() {
        return contactsList.size();
    }
}

