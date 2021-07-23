package com.example.househub;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private FirebaseAuth mAuth;
    private DatabaseReference DatabaseRef, UsersRef ,FamilyRef, FamilyMessageKeyRef;
    private String currentUserId, currentUsername, currentDate, currentTime, familyNameId;

    private ImageButton sendButton;
    private EditText sendMessageText;
    private ScrollView mScrollView;
    private TextView displayTextMessages;
    private View chatFragmentView;


    public ChatFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChatFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChatFragment newInstance(String param1, String param2) {
        ChatFragment fragment = new ChatFragment();
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
        chatFragmentView = inflater.inflate(R.layout.fragment_chat, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        DatabaseRef = FirebaseDatabase.getInstance().getReference();
        UsersRef = DatabaseRef.child("Users");
        familyNameId = GlobalVars.getFamilyNameId();
        FamilyRef = DatabaseRef.child("FamilyChat").child(familyNameId);

        //Initialize Fields
        sendButton = chatFragmentView.findViewById(R.id.sendButton);
        sendMessageText = chatFragmentView.findViewById(R.id.sendMessageEdit);
        displayTextMessages = chatFragmentView.findViewById(R.id.chat_text_display);
        mScrollView = chatFragmentView.findViewById(R.id.chat_scroll_view);

        mScrollView.post(new Runnable() {
            @Override
            public void run() {
                mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });

        getUserInfo();

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveMessageToDatabase();

                sendMessageText.setText("");

                mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });

        return chatFragmentView;
    }

    @Override
    public void onStart() {
        super.onStart();

        //displayTextMessages.setText("");

        FamilyRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {
                if(snapshot.exists()){
                    DisplayMessages(snapshot);
                }
            }

            @Override
            public void onChildChanged(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {
                if(snapshot.exists()){
                    DisplayMessages(snapshot);
                }
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

    private void SaveMessageToDatabase() {
            String message = sendMessageText.getText().toString();
            String messageKey = FamilyRef.push().getKey();

            if(!(TextUtils.isEmpty(message))){
                Calendar mCalendarDate = Calendar.getInstance();
                SimpleDateFormat currentDateFormat = new SimpleDateFormat("MMM dd, yyyy");
                currentDate = currentDateFormat.format(mCalendarDate.getTime());

                Calendar mCalendarTime = Calendar.getInstance();
                SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
                currentTime = currentTimeFormat.format(mCalendarTime.getTime());

                HashMap<String, Object> chatMessageKey = new HashMap<>();
                FamilyRef.updateChildren(chatMessageKey);

                FamilyMessageKeyRef = FamilyRef.child(messageKey);

                HashMap<String, Object> messageInfoMap = new HashMap<>();
                    messageInfoMap.put("name", currentUsername);
                    messageInfoMap.put("message", message);
                    messageInfoMap.put("date", currentDate);
                    messageInfoMap.put("time", currentTime);

                FamilyMessageKeyRef.updateChildren(messageInfoMap);
            }
    }

    private void getUserInfo() {
        UsersRef.child(currentUserId).child("Profile").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    currentUsername = snapshot.child("name").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void DisplayMessages(DataSnapshot dataSnapshot) {
        Iterator iterator = dataSnapshot.getChildren().iterator();

        while(iterator.hasNext()) {
            String chatDate = (String) ((DataSnapshot)iterator.next()).getValue();
            String chatMessage = (String) ((DataSnapshot)iterator.next()).getValue();
            String chatName = (String) ((DataSnapshot)iterator.next()).getValue();
            String chatTime = (String) ((DataSnapshot)iterator.next()).getValue();

            displayTextMessages.append(chatName + " :\n" + chatMessage + " :\n" + chatTime + "     " + chatDate + "\n\n\n");

            mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
        }
    }

    /*
    private DatabaseReference getFamilyRef(DatabaseReference DatabaseRef){
        DatabaseRef.child("Users").child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                String familyNameId = snapshot.child("Family").getValue().toString();
                FamilyRef = DatabaseRef.child("FamilyChat").child(familyNameId);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
        return FamilyRef;
    }
*/
}
    /*
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        DatabaseRef = FirebaseDatabase.getInstance().getReference();
        UsersRef = DatabaseRef.child("Users");

        getFamilyId();

        Log.d("MyDebugTag", "My variable is: " + familyNameId);

        //FamilyRef = DatabaseRef.child("FamilyChat").child(familyNameId);

        sendButton = view.findViewById(R.id.sendButton);
        sendMessageText = view.findViewById(R.id.sendMessageEdit);
        displayTextMessages = view.findViewById(R.id.chat_text_display);
        mScrollView = view.findViewById(R.id.chat_scroll_view);


        getUserInfo();

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveMessageToDatabase();

                sendMessageText.setText("");
            }
        });
    }
*/
