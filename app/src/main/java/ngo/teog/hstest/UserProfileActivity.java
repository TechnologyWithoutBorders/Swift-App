package ngo.teog.hstest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;

import ngo.teog.hstest.helpers.Defaults;
import ngo.teog.hstest.helpers.HospitalDevice;
import ngo.teog.hstest.helpers.Report;
import ngo.teog.hstest.helpers.User;

public class UserProfileActivity extends BaseActivity {

    private MySimpleArrayAdapter adapter;

    private TextView telephoneView;
    private TextView mailView;
    private TextView hospitalView;
    private TextView positionView;
    private TextView qualificationsView;

    private ProgressBar progressBar;
    private LinearLayout rootLayout;

    private ImageView imageView;

    private ListView reportList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        SharedPreferences preferences = getSharedPreferences(Defaults.PREF_FILE_KEY, Context.MODE_PRIVATE);

        String userName = preferences.getString(getString(R.string.name_pref), null);
        TextView nameView = findViewById(R.id.nameView);
        nameView.setText(userName);

        telephoneView = findViewById(R.id.phoneView);
        mailView = findViewById(R.id.mailView);
        hospitalView = findViewById(R.id.locationView);
        positionView = findViewById(R.id.positionView);
        qualificationsView = findViewById(R.id.qualificationsView);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        rootLayout = findViewById(R.id.rootLayout);

        reportList = (ListView)findViewById(R.id.reportList);
        ArrayList<Report> values = new ArrayList<Report>();

        adapter = new MySimpleArrayAdapter(this, values);
        reportList.setAdapter(adapter);

        reportList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), ReportInfoActivity.class);
                intent.putExtra("REPORT", (Report)adapterView.getItemAtPosition(i));
                startActivity(intent);
            }
        });

        imageView = findViewById(R.id.imageView);

        new FetchTask().execute(preferences.getString(getString(R.string.name_pref), null), null);
        new DownloadImageTask().execute("https://teog.virlep.de/users/0.jpg", null);
    }

    @Override
    public void onInternetStatusChanged() {
        SharedPreferences preferences = getSharedPreferences(Defaults.PREF_FILE_KEY, Context.MODE_PRIVATE);

        new FetchTask().execute(preferences.getString(getString(R.string.name_pref), null), null);
        new DownloadImageTask().execute("https://teog.virlep.de/users/0.jpg", null);
    }

    private class MySimpleArrayAdapter extends ArrayAdapter<Report> {
        private final Context context;

        public MySimpleArrayAdapter(Context context, ArrayList<Report> values) {
            super(context, -1, values);
            this.context = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.row_maintenance, parent, false);
            TextView nameView = (TextView) rowView.findViewById(R.id.nameView);
            nameView.setText(this.getItem(position).getDevice().getName());

            TextView dateView = (TextView) rowView.findViewById(R.id.dateView);

            DateFormat format = new SimpleDateFormat("yyyy-mm-dd");

            Date date = this.getItem(position).getDate();

            dateView.setText(format.format(this.getItem(position).getDate()));

            return rowView;
        }
    }

    private class FetchTask extends AsyncTask<String, Integer, Combi> {

        private XmlPullParser parser = Xml.newPullParser();

        @Override
        protected void onPreExecute() {
            rootLayout.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        }

        protected Combi doInBackground(String... userIDs) {
            int resCode;
            InputStream in;
            try {
                URL url = new URL("https://teog.virlep.de/users.php?username=" + userIDs[0]);
                URLConnection urlConn = url.openConnection();

                HttpsURLConnection httpsConn = (HttpsURLConnection) urlConn;
                httpsConn.setAllowUserInteraction(false);
                httpsConn.setInstanceFollowRedirects(true);
                httpsConn.setRequestMethod("GET");
                httpsConn.connect();
                resCode = httpsConn.getResponseCode();

                if(resCode == HttpsURLConnection.HTTP_OK) {
                    in = httpsConn.getInputStream();

                    parser.setInput(in, null);

                    int event = parser.getEventType();
                    String userName = null;
                    String telephoneNumber = null;
                    String eMail = null;
                    String hospital = null;
                    String position = null;
                    String qualifications = null;

                    User user = null;

                    int id = -1;
                    HospitalDevice device = null;
                    ArrayList<Report> reports = new ArrayList<Report>();

                    while(event != XmlPullParser.END_DOCUMENT)  {
                        String name = parser.getName();
                        switch (event){
                            case XmlPullParser.START_TAG:
                                if(name.equals("username")) {
                                    userName = parser.nextText();
                                }

                                if(name.equals("telephone")) {
                                    telephoneNumber = parser.nextText();
                                }

                                if(name.equals("email")) {
                                    eMail = parser.nextText();
                                }

                                if(name.equals("hospital")) {
                                    hospital = parser.nextText();
                                }

                                if(name.equals("position")) {
                                    position = parser.nextText();
                                }

                                if(name.equals("qualifications")) {
                                    qualifications = parser.nextText();

                                    user = new User(userName, telephoneNumber, eMail, hospital, position, qualifications);
                                }

                                if(name.equals("reportNumber")) {
                                    id = Integer.parseInt(parser.nextText());
                                }

                                if(name.equals("deviceNumber")) {
                                    String dummy = parser.nextText();
                                }

                                if(name.equals("deviceName")) {
                                    device = new HospitalDevice(-1, parser.nextText(), null, null, null, null, null, false, new Date());
                                }

                                if(name.equals("date")) {
                                    String dateString = parser.nextText();
                                    Date date = new Date();

                                    reports.add(new Report(id, user, device, date));
                                }
                                break;
                            case XmlPullParser.END_TAG:
                                break;
                        }

                        event = parser.next();
                    }

                    in.close();

                    return new Combi(reports, user);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Combi result) {
            User user = result.getUser();
            ArrayList<Report> reports = result.getReports();

            adapter.clear();
            adapter.addAll(reports);

            telephoneView.setText(user.getTelephone());
            mailView.setText(user.getEMail());
            hospitalView.setText(user.getHospital());
            positionView.setText(user.getPosition());
            qualificationsView.setText(user.getQualifications());

            progressBar.setVisibility(View.INVISIBLE);
            rootLayout.setVisibility(View.VISIBLE);
        }
    }

    private class Combi {
        private ArrayList<Report> reports;
        private User user;

        public Combi(ArrayList<Report> reports, User user) {
            this.reports = reports;
            this.user = user;
        }

        public ArrayList<Report> getReports() {
            return reports;
        }

        public User getUser() {
            return user;
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected void onPreExecute() {
            imageView.setVisibility(View.INVISIBLE);
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if(result != null) {
                imageView.setImageBitmap(result);
            }
            imageView.setVisibility(View.VISIBLE);
        }
    }
}
