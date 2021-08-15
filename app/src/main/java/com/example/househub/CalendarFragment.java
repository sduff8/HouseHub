 package com.example.househub;
 import androidx.appcompat.app.AppCompatActivity;
 import android.os.Bundle;

 import androidx.annotation.NonNull;
 import androidx.fragment.app.Fragment;

 import android.view.View;
 import android.widget.Button;

 import com.example.househub.Calendar.EventDecorator;
 import com.example.househub.Calendar.OneDayDecorator;
 import com.example.househub.Model.Event;
 import com.prolificinteractive.materialcalendarview.CalendarDay;
 import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
 import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

 import java.text.DateFormat;
 import java.text.SimpleDateFormat;
 import java.util.Date;
 import butterknife.BindView;
 import butterknife.ButterKnife;
 import java.util.ArrayList;
 import android.graphics.Color;

 import java.io.BufferedReader;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.InputStreamReader;
 import java.io.StringReader;
 import java.net.HttpURLConnection;
 import java.net.URL;
 import android.widget.ListView;
 import android.net.ConnectivityManager;
 import android.net.NetworkInfo;
 import android.os.AsyncTask;
 import android.app.AlertDialog;
 import android.content.Context;
 import android.widget.AdapterView;
 import android.content.Intent;

 import android.view.Menu;
 import android.view.MenuItem;
 import android.view.LayoutInflater;
 import android.widget.EditText;
 import android.view.ViewGroup;
 import android.content.DialogInterface;
 import java.io.BufferedWriter;
 import java.io.OutputStreamWriter;
 import java.util.Calendar;

 import org.jetbrains.annotations.NotNull;
 import org.xmlpull.v1.XmlPullParser;
 import org.xmlpull.v1.XmlPullParserFactory;

 /**
 * A simple {@link Fragment} subclass.
 * Use the {@link CalendarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CalendarFragment extends Fragment implements OnDateSelectedListener {

     // TODO: Rename parameter arguments, choose names that match
     // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
     private static final String ARG_PARAM1 = "param1";
     private static final String ARG_PARAM2 = "param2";

     // TODO: Rename and change types of parameters
     private String mParam1;
     private String mParam2;

     private DateFormat FORMATTER = SimpleDateFormat.getDateInstance();
     private OneDayDecorator oneDayDecorator;

     @BindView(R.id.calendarView)
     MaterialCalendarView widget;

     ArrayList<CalendarDay> dates;
     EventDecorator events;
     static ArrayList<Event> xmlEvents;
     static ArrayList<Event> selectedEvents;

     CalendarDay selectedDate;
     String filename = new String("cal_data.xml");

     View calendarFragmentView;

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
         calendarFragmentView = inflater.inflate(R.layout.fragment_calendar, container, false);


         return calendarFragmentView;
     }

     @Override
     public void onDateSelected(@NonNull @NotNull MaterialCalendarView widget, @NonNull @NotNull CalendarDay date, boolean selected) {

     }
/*
     public boolean isNetworkAvailable() {
         ConnectivityManager cm = (ConnectivityManager)
                 getSystemService(Context.CONNECTIVITY_SERVICE);

         NetworkInfo networkInfo = cm.getActiveNetworkInfo();

         if (networkInfo != null && networkInfo.isConnected())
             return true;
         else
             return false;

     }



     @Override
     public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
         selectedDate = date;
         if(events.shouldDecorate(date)) {
             selectedEvents.clear();
             findEvents(date.getDate());
             String[] events = new String[selectedEvents.size()];
             for(int i = 0; i < selectedEvents.size(); i++) {
                 Event e = selectedEvents.get(i);
                 events[i] = e.getType();
                 System.out.println(e.getType());
             }

             ListView eventListView = (ListView)calendarFragmentView.findViewById(R.id.eventList);
             eventListView.setAdapter(new EventAdapter(getActivity(), events));
             eventListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                 @Override
                 public void onItemClick(AdapterView<?> parent, View view,
                                         int position, long id) {
                     Event e = selectedEvents.get(position);
                     Intent newsScreen = new Intent(CalendarActivity.this, EventActivity.class);
                     newsScreen.putExtra("data", e);
                     startActivity(newsScreen);
                 }
             });
         }
         else {
             ListView eventListView = (ListView) calendarFragmentView.findViewById(R.id.eventList);
             eventListView.setAdapter(null);
         }
     }

     public void findEvents(org.threeten.bp.LocalDate date) {
         DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
         String s = df.format(date);
         for(int i = 0; i < xmlEvents.size(); i++)  {
             Event e = xmlEvents.get(i);
             if(s.equals(e.getDate())) {
                 selectedEvents.add(e);
             }
         }
     }

     private class GetXML extends AsyncTask<String, Void, String> {
         String src = null;

         @Override
         protected String doInBackground(String... params) {
             try {
                 URL url = new URL("http://10.0.2.2/XML/events.xml");
                 HttpURLConnection con = (HttpURLConnection) url.openConnection();
                 src = readStream(con.getInputStream());
             } catch (Exception e) {
                 e.printStackTrace();
             }
             return src;
         }

         @Override
         protected void onPostExecute(String result) {
             xmlEvents = new ArrayList<Event>();
             xmlEvents.clear();

             parseXML(src);

             String old = readFileFromInternalStorage(filename); // local data
             StringBuffer b = new StringBuffer("<events>");
             b.append(old);
             b.append("</events>");

             System.out.println();

             parseXML(b.toString());

             int d, m, y;
             CalendarDay day;
             for(int i = 0; i < xmlEvents.size(); i++) {
                 Event e = xmlEvents.get(i);
                 String date = e.getDate();
                 String[] a = date.split("-");
                 y = Integer.parseInt(a[0]);
                 m = Integer.parseInt(a[1]) - 1; // months start at 0 in Java
                 d = Integer.parseInt(a[2]);
                 day = CalendarDay.from(y,m,d);
                 dates.add(day);
             }

             events = new EventDecorator(Color.BLACK, dates);
             widget.addDecorator(events);
         }

         @Override
         protected void onPreExecute() {
         }

         @Override
         protected void onProgressUpdate(Void... values) {
         }
     }

     private String readStream(InputStream in) {
         BufferedReader reader = null;
         String line = null;
         StringBuffer sb = new StringBuffer();
         try {
             reader = new BufferedReader(new InputStreamReader(in));
             while ((line = reader.readLine()) != null) {
                 sb.append(line);
             }
         } catch (IOException e) {
             e.printStackTrace();
         } finally {
             if (reader != null) {
                 try {
                     reader.close();
                 } catch (IOException e) {
                     e.printStackTrace();
                 }
             }
         }
         return sb.toString();
     }

     public static void parseXML(String src) {
         String date = new String();
         String type = new String();
         String description = new String();

         try {
             StringReader sr = new StringReader(src);
             XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
             factory.setNamespaceAware(true);
             XmlPullParser xpp = factory.newPullParser();
             xpp.setInput(sr);
             Event e;

             int eventType = xpp.getEventType();
             while (eventType != XmlPullParser.END_DOCUMENT) {
                 if(eventType == XmlPullParser.START_TAG) {
                     if(xpp.getName().equals("type")) {
                         eventType = xpp.next();
                         if(eventType == XmlPullParser.TEXT) {
                             type = xpp.getText();
                         }
                     }
                 }

                 if(eventType == XmlPullParser.START_TAG) {
                     if(xpp.getName().equals("date")) {
                         eventType = xpp.next();
                         if(eventType == XmlPullParser.TEXT) {
                             date = xpp.getText();
                         }
                     }
                 }

                 if(eventType == XmlPullParser.START_TAG) {
                     if(xpp.getName().equals("description")) {
                         eventType = xpp.next();
                         if(eventType == XmlPullParser.TEXT) {
                             description = xpp.getText();
                         }
                     }
                 }

                 if(eventType == XmlPullParser.END_TAG) {
                     if(xpp.getName().equals("event")) {
                         e = new Event(type, date, description);
                         xmlEvents.add(e);
                     }
                 }

                 eventType = xpp.next();
             }
         }
         catch (Exception e) {
             e.printStackTrace();
         }
     }

     @Override
     public boolean onCreateOptionsMenu(Menu menu) {
         // Inflate the menu; this adds items to the action bar     // if it is present.

         getMenuInflater().inflate(R.menu.main, menu);

         return true;
     }


     public boolean onOptionsItemSelected(MenuItem item) {
         int id = item.getItemId();

         final CalendarDay d;
         final String ss;

         if(id == R.id.add) {
             if(selectedDate == null) {
                 d = widget.getCurrentDate(); // first of month
             }
             else {
                 d = selectedDate;
                 selectedDate = null;
             }

            /*
            Toast.makeText(getBaseContext(), "Add Clicked " + d,
                    Toast.LENGTH_SHORT).show();
            */
/*
             SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

             int sd = d.getDay();
             int sm = d.getMonth();
             int sy = d.getYear();

             Calendar c = Calendar.getInstance();
             c.set(sy, sm, sd, 0, 0);
             Date sDate = c.getTime();
             ss = sdf.format(sDate);

             LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(getContext().LAYOUT_INFLATER_SERVICE);
             View layout = inflater.inflate(R.layout.event_add_dialog, (ViewGroup) calendarFragmentView.findViewById(R.id.layout_root));

             final EditText titleBox = (EditText) layout.findViewById(R.id.title);
             final EditText descriptionBox = (EditText) layout.findViewById(R.id.description);

             AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
             builder.setView(layout);
             builder.setTitle("Date: " + ss);

             builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                 @Override
                 public void onClick(DialogInterface dialog, int which) {
                     String title = titleBox.getText().toString();
                     String description = descriptionBox.getText().toString();
                     String src = "<event><type>" + title + "</type>" + "<date>" + ss + "</date>" + "<description>" + description + "</description></event>";
                     String old = readFileFromInternalStorage(filename);
                     StringBuffer b = new StringBuffer(old);
                     b.append(src);
                     writeFileToInternalStorage(filename, b.toString());

                     dates.add(d);
                     Event e = new Event(title, ss, description);
                     xmlEvents.add(e);
                     events = new EventDecorator(Color.BLACK, dates);
                     widget.addDecorator(events);

                     dialog.dismiss();
                 }
             });

             builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                 @Override
                 public void onClick(DialogInterface dialog, int which) {
                     dialog.dismiss();
                 }
             });

             builder.show();
         }
         return true;
     }

     public void writeFileToInternalStorage(String fileName, String data) {
         String eol = System.getProperty("line.separator");
         BufferedWriter writer = null;
         try {
             writer = new BufferedWriter(new OutputStreamWriter(getActivity().openFileOutput(fileName, Context.MODE_PRIVATE)));
             writer.write(data);
         } catch (Exception e) {
             e.printStackTrace();
         } finally {
             if (writer != null) {
                 try {
                     writer.close();
                 } catch (IOException e) {
                     e.printStackTrace();
                 }
             }
         }
     }

     public String readFileFromInternalStorage(String fileName) {
         String eol = System.getProperty("line.separator");
         StringBuffer buffer = new StringBuffer();
         BufferedReader input = null;
         try {
             input = new BufferedReader(new InputStreamReader(getActivity().openFileInput(fileName)));
             String line;
             while ((line = input.readLine()) != null) {
                 buffer.append(line + eol);
             }
         } catch (Exception e) {
             e.printStackTrace();
         } finally {
             if (input != null) {
                 try {
                     input.close();
                 } catch (IOException e) {
                     e.printStackTrace();
                 }
             }
         }
         return buffer.toString();
     }

     @Override
     public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
         mCalendarView = getView().findViewById(R.id.calendarView);
         mCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
             @Override
             public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                 String date = (month + 1) + "/" + dayOfMonth + "/" + year;
                 Toast.makeText(getActivity().getApplicationContext(), "Date Selected: mm/dd/yyyy " + date, Toast.LENGTH_SHORT).show();
             }
         });
     }
     */
}
