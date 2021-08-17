package com.example.househub;

import android.widget.ArrayAdapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class SettingsAdapter extends ArrayAdapter <String> {
    private Context context;
    private String[] items;

    public SettingsAdapter(Context context, String[] items) {
        super(context, R.layout.iconrowlist, R.id.ictextid, items);
        this.context = context;
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.iconrowlist, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.ictextid);
        TextView subTextView = (TextView) rowView.findViewById(R.id.icsubtextid);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.list_image);
        textView.setText(items[position]);

        // Change icon and subtext based on name
        String s = items[position];

        if (s.equals("Profile Settings")) {
            imageView.setImageResource(R.drawable.ic_username);
            subTextView.setText("");
        } else if (s.equals("Family Settings")) {
            imageView.setImageResource(R.drawable.ic_family);
            subTextView.setText("");
        } else if (s.equals("Leave Family")) {
            imageView.setImageResource(R.drawable.ic_door);
            subTextView.setText("");
        }
        return rowView;
    }
}
