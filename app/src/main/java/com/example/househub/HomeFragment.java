package com.example.househub;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.househub.Model.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private Toolbar mToolbar;
    private TextView familyName;
    private ImageView familyPhoto;
    private RecyclerView mRecyclerview;

    private String familyNameId;

    private FirebaseAuth mAuth;
    private DatabaseReference DatabaseRef, UsersRef, FamilyRef;

    private Activity mActivity;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);

        mActivity = getActivity();
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mActivity = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        familyNameId = GlobalVars.getFamilyNameId();

        mToolbar = view.findViewById(R.id.home_fragment_toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Home");

        familyName = view.findViewById(R.id.family_name_home);
        familyPhoto = view.findViewById(R.id.family_image_home);
        mRecyclerview = view.findViewById(R.id.home_recyclerview);

        mAuth = FirebaseAuth.getInstance();
        DatabaseRef = FirebaseDatabase.getInstance().getReference();
        UsersRef = DatabaseRef.child("Users");
        FamilyRef = DatabaseRef.child("Families");

        //adapter = new HomeAdapter(getContext(), memberList);
        mRecyclerview = view.findViewById(R.id.home_recyclerview);
        mRecyclerview.setLayoutManager(new GridLayoutManager(getContext(), 3));
        //mRecyclerview.setAdapter(adapter);

        RetrieveFamilyInfo();
        getMembers();

        return view;
    }

//    @Override
//    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        RetrieveFamilyInfo();
//        getMembers();
//    }

    private void getMembers() {
        if (mActivity == null){
            return;
        }

        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>().setQuery(UsersRef, User.class).build();

        FirebaseRecyclerAdapter<User, FamilyMemberViewHolder> adapter = new FirebaseRecyclerAdapter<User, FamilyMemberViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull @NotNull FamilyMemberViewHolder holder, int position, @NonNull @NotNull User model) {
                String modelFamilyId = model.getFamily();

                Log.d("MyErrorMsg", "model.getFamily() = " + modelFamilyId);
                Log.d("MyErrorMsg", "familyNameId = " + familyNameId);

                if(Objects.equals(modelFamilyId, familyNameId)) {
                    holder.memberUsername.setText(model.getName());
                    Glide.with(getContext()).load(model.getImage()).placeholder(R.drawable.profile_image).into(holder.memberImage);
                }
            }

            @NonNull
            @NotNull
            @Override
            public FamilyMemberViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_home_layout, parent, false);
                FamilyMemberViewHolder viewHolder = new FamilyMemberViewHolder(view);
                return viewHolder;
            }
        };
        mRecyclerview.setAdapter(adapter);

        adapter.startListening();
    }

    public static class FamilyMemberViewHolder extends RecyclerView.ViewHolder{

        TextView memberUsername;
        CircleImageView memberImage;

        public FamilyMemberViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            memberUsername = itemView.findViewById(R.id.member_name);
            memberImage = itemView.findViewById(R.id.member_image);
        }
    }

    private void RetrieveFamilyInfo() {
        FamilyRef.child(familyNameId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (mActivity == null){
                    return;
                }
                if (snapshot.exists() && (snapshot.hasChild("name"))){
                    String retrieveName = snapshot.child("name").getValue().toString();
                    familyName.setText(retrieveName);
                }
                if (snapshot.exists() && (snapshot.hasChild("image"))){
                    String retrieveImage = snapshot.child("image").getValue().toString();
                    Glide.with(getContext()).load(retrieveImage).into(familyPhoto);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
}