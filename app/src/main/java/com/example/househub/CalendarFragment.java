package com.example.househub;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.househub.Model.Event;
import com.example.househub.Model.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.sql.Time;
import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CalendarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CalendarFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private CalendarView mCalendarView;
    private Toolbar toolbar;
    private RecyclerView recyclerView;

    private FirebaseAuth mAuth;
    private DatabaseReference DatabaseRef, EventsRef, EventsKeyRef;
    private String currentUserId, currentUsername, familyNameId;

    public CalendarFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CalendarFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CalendarFragment newInstance(String param1, String param2) {
        CalendarFragment fragment = new CalendarFragment();
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
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        toolbar = view.findViewById(R.id.calendar_fragment_toolbar);

        toolbar.setTitle("Calendar");
        toolbar.inflateMenu(R.menu.options_with_add_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();

                if(id == R.id.add){
                    addEvent();
                    return true;
                }
                if (id == R.id.settings){
                    sendUserToSettingsActivity();
                    return true;
                }

                if (id == R.id.logout){
                    mAuth.signOut();
                    sendUserToLoginActivity();
                    return true;
                }
                return false;
            }
        });
        recyclerView = view.findViewById(R.id.eventList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        DatabaseRef = FirebaseDatabase.getInstance().getReference();
        familyNameId = GlobalVars.getFamilyNameId();
        EventsRef = DatabaseRef.child("Events").child(familyNameId);

        mCalendarView = view.findViewById(R.id.calendarView);
        mCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                String date = (month + 1) + "/" + dayOfMonth + "/" + year;
                getEvents(date);
            }
        });


        return view;
    }

    private void addEvent() {
        final String eventKey = EventsRef.push().getKey();

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.event_add_dialog, null))

                .setPositiveButton("Add Event", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Dialog f = (Dialog) dialog;

                        EditText eventTitleField = f.findViewById(R.id.event_title_dialog);
                        EditText eventDescriptionField = f.findViewById(R.id.event_description_dialog);
                        EditText eventTimeField = f.findViewById(R.id.event_time_dialog);
                        DatePicker eventDateField = f.findViewById(R.id.event_date_dialog);

                        String eventName = eventTitleField.getText().toString();
                        String eventDescription = eventDescriptionField.getText().toString();
                        String eventTime = eventTimeField.getText().toString();
                        int day = eventDateField.getDayOfMonth();
                        int month = eventDateField.getMonth() + 1;
                        int year = eventDateField.getYear();
                        String date = month + "/" + day + "/" + year;

                        if (TextUtils.isEmpty(eventName) || TextUtils.isEmpty(eventDescription) || TextUtils.isEmpty(eventTime)) {
                            Toast.makeText(getActivity(), "Please Enter All Event Information", Toast.LENGTH_SHORT);
                        } else {
                            EventsKeyRef = EventsRef.child(eventKey);

                            HashMap<String, Object> eventInfoMap = new HashMap<>();
                            eventInfoMap.put("title", eventName);
                            eventInfoMap.put("description", eventDescription);
                            eventInfoMap.put("time", eventTime);
                            eventInfoMap.put("date", date);

                            EventsKeyRef.updateChildren(eventInfoMap);
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

    private void getEvents(String date) {

        FirebaseRecyclerOptions<Event> options = new FirebaseRecyclerOptions.Builder<Event>().setQuery(EventsRef.orderByChild("time"), Event.class).build();

        FirebaseRecyclerAdapter<Event, CalendarFragment.EventViewHolder> adapter = new FirebaseRecyclerAdapter<Event, CalendarFragment.EventViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull @NotNull CalendarFragment.EventViewHolder holder, int position, @NonNull @NotNull Event model) {
                String modelTitle = model.getTitle();
                String modelDescription = model.getDescription();
                String modelTime = model.getTime();
                String modelDate = model.getDate();

                if(Objects.equals(modelDate, date)) {
                    holder.eventTitle.setText(modelTitle);
                    holder.eventDescription.setText(modelDescription);
                    holder.eventTime.setText(modelTime);
                }
            }

            @NonNull
            @NotNull
            @Override
            public CalendarFragment.EventViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_event_layout, parent, false);
                CalendarFragment.EventViewHolder viewHolder = new CalendarFragment.EventViewHolder(view);
                return viewHolder;
            }
        };
        recyclerView.setAdapter(adapter);

        adapter.startListening();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder{

        TextView eventTitle;
        TextView eventDescription;
        TextView eventTime;

        public EventViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            eventTitle = itemView.findViewById(R.id.event_title);
            eventDescription = itemView.findViewById(R.id.event_description);
            eventTime = itemView.findViewById(R.id.event_time);
        }
    }

    private void sendUserToSettingsActivity() {
        Intent settingsIntent = new Intent(getActivity(), SettingsActivity.class);
        settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(settingsIntent);
        requireActivity().finish();
    }

    private void sendUserToLoginActivity() {
        Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
        startActivity(loginIntent);
        requireActivity().finish();
    }
}