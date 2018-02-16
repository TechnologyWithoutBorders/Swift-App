package ngo.teog.swift;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ngo.teog.swift.helpers.Defaults;
import ngo.teog.swift.helpers.Report;

public class UserProfileActivity extends AppCompatActivity {

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

        TextView nameView = findViewById(R.id.nameView);

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
            nameView.setText(this.getItem(position).getDevice().getAssetNumber());

            TextView dateView = (TextView) rowView.findViewById(R.id.dateView);

            DateFormat format = new SimpleDateFormat("yyyy-mm-dd");

            Date date = this.getItem(position).getDate();

            dateView.setText(format.format(this.getItem(position).getDate()));

            return rowView;
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
