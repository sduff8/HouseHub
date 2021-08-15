package com.example.househub;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.househub.Model.Contacts;
import com.example.househub.Model.Messages;
import com.example.househub.Model.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ContactsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContactsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private FirebaseAuth mAuth;
    private DatabaseReference DatabaseRef, UsersRef, ContactsRef, ContactsKeyRef;
    private String currentUserId, currentUsername, familyNameId;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    private Toolbar mToolbar;
    private Button addContactsButton;
    private RecyclerView mRecyclerView;
    private View view;
    private Uri imageUri;

    private final List<Contacts> contactsList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private ContactsAdapter contactsAdapter;


    public ContactsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ContactsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ContactsFragment newInstance(String param1, String param2) {
        ContactsFragment fragment = new ContactsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_contacts, container, false);

        mToolbar = view.findViewById(R.id.contacts_fragment_toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Contacts");

        addContactsButton = view.findViewById(R.id.add_contacts_button);
        mRecyclerView = view.findViewById(R.id.contacts_recycler_view);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        DatabaseRef = FirebaseDatabase.getInstance().getReference();
        UsersRef = DatabaseRef.child("Users");
        familyNameId = GlobalVars.getFamilyNameId();
        ContactsRef = DatabaseRef.child("Contacts").child(familyNameId);
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        contactsAdapter = new ContactsAdapter(getContext(), contactsList);
        mRecyclerView = view.findViewById(R.id.contacts_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(contactsAdapter);

        addContactsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addContact();
            }
        });

        getContacts();

        return view;
    }

    private void getContacts() {
        Query query = ContactsRef.orderByChild("name");

        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {
                Contacts contacts = snapshot.getValue(Contacts.class);

                contactsList.add(contacts);

                contactsAdapter.notifyDataSetChanged();

                //mRecyclerView.smoothScrollToPosition(Objects.requireNonNull(mRecyclerView.getAdapter()).getItemCount());
            }

            @Override
            public void onChildChanged(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {


            }

            @Override
            public void onChildRemoved(@NonNull @NotNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void addContact() {
        final String contactsKey = ContactsRef.push().getKey();

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertDialog);
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.contacts_add_dialog, null))

                .setPositiveButton("Add Contact", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Dialog f = (Dialog) dialog;


                        EditText contactNameField = f.findViewById(R.id.contact_name_dialog);
                        EditText contactPhoneField = f.findViewById(R.id.contact_phone_dialog);
                        EditText contactAddressField = f.findViewById(R.id.contact_address_dialog);

                        String contactName = contactNameField.getText().toString();
                        String contactPhone = contactPhoneField.getText().toString();
                        String contactAddress = contactAddressField.getText().toString();

                /*
                contactsImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectImage();
                        imageUpdateCheck = true;
                        contactsImage.setImageURI(imageUri);
                    }
                });
                 */

                        if (TextUtils.isEmpty(contactName) || TextUtils.isEmpty(contactPhone) || TextUtils.isEmpty(contactAddress)) {
                            Toast.makeText(getActivity(), "Please Enter All Contact Information", Toast.LENGTH_SHORT);
                        } else {
                            ContactsKeyRef = ContactsRef.child(contactsKey);

                            HashMap<String, Object> contactsInfoMap = new HashMap<>();
                            contactsInfoMap.put("name", contactName);
                            contactsInfoMap.put("phone", contactPhone);
                            contactsInfoMap.put("address", contactAddress);
                            contactsInfoMap.put("cid", contactsKey);

                            ContactsKeyRef.updateChildren(contactsInfoMap);
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        builder.create().show();
    }

    /*@Override
    public void onStart() {
        super.onStart();

        Query query = ContactsRef.orderByChild("name");

        FirebaseRecyclerOptions<Contacts> options = new FirebaseRecyclerOptions.Builder<Contacts>().setQuery(query, Contacts.class).build();

        FirebaseRecyclerAdapter<Contacts, ContactsFragment.ContactsViewHolder> adapter = new FirebaseRecyclerAdapter<Contacts, ContactsFragment.ContactsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull @NotNull ContactsFragment.ContactsViewHolder holder, int position, @NonNull @NotNull Contacts model) {
                holder.contactsNameText.setText(model.getName());
                holder.contactsPhoneText.setText(model.getPhone());
                holder.contactsAddressText.setText(model.getAddress());

                //Glide.with(getContext()).load(contactImage).placeholder(R.drawable.profile_image).into(holder.contactProfileImage);
                Glide.with(ContactsFragment.this).load(model.getImage()).placeholder(R.drawable.profile_image).into(holder.contactProfileImage);


                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String visitContactId = getRef(position).getKey();
                        String contactName = model.getName();

                        Intent profileIntent = new Intent(getContext(), ContactsProfileActivity.class);
                        profileIntent.putExtra("visitContactId", visitContactId);
                        profileIntent.putExtra("contactName", contactName);
                        startActivity(profileIntent);
                    }
                });
            }

            @NonNull
            @NotNull
            @Override
            public ContactsFragment.ContactsViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contacts_display_layout, parent, false);
                ContactsFragment.ContactsViewHolder viewHolder = new ContactsFragment.ContactsViewHolder(view);
                return viewHolder;
            }
        };
        mRecyclerView.setAdapter(adapter);

        adapter.startListening();
    }

    public static class ContactsViewHolder extends RecyclerView.ViewHolder {

        public TextView contactsNameText, contactsPhoneText, contactsAddressText;
        public CircleImageView contactProfileImage;

        public ContactsViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            contactsNameText = itemView.findViewById(R.id.contacts_name);
            contactsPhoneText = itemView.findViewById(R.id.contacts_phone_number);
            contactsAddressText = itemView.findViewById(R.id.address_contacts);
            contactProfileImage = itemView.findViewById(R.id.contacts_recycler_image);
        }
    }
*/
}