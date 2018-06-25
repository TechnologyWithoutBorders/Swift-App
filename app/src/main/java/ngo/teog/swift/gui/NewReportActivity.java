package ngo.teog.swift.gui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.android.volley.RequestQueue;

import java.util.ArrayList;

import ngo.teog.swift.R;
import ngo.teog.swift.communication.RequestFactory;
import ngo.teog.swift.communication.VolleyManager;
import ngo.teog.swift.helpers.Defaults;
import ngo.teog.swift.helpers.HospitalDevice;
import ngo.teog.swift.helpers.Report;


public class NewReportActivity extends AppCompatActivity {

    private ViewFlipper flipper;
    private LinearLayout first;
    private LinearLayout second;
    private ProgressBar progressBar;
    private Button nextButton;
    private Button tagButton;

    private MySimpleArrayAdapter tagAdapter;
    private ListView tagList;
    private ArrayList<String> tagNames = new ArrayList<String>();

    private HospitalDevice device;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_report);

        Intent intent = this.getIntent();
        device = (HospitalDevice)intent.getSerializableExtra("DEVICE");

        first = findViewById(R.id.first);
        second = findViewById(R.id.second);
        nextButton = findViewById(R.id.nextButton);
        progressBar = findViewById(R.id.progressBar);
        flipper = findViewById(R.id.flipper);

        Animation in = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        Animation out = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right);

        flipper.setInAnimation(in);
        flipper.setOutAnimation(out);

        Spinner spinner = findViewById(R.id.typeSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.report_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        tagButton = findViewById(R.id.tagButton);

        tagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog tagDialog = new Dialog(NewReportActivity.this);
                tagDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                View dialogView = getLayoutInflater().inflate(R.layout.tag_dialog, null);

                LinearLayout root = dialogView.findViewById(R.id.linearLayout);
                final String[] errorTags = getResources().getStringArray(R.array.error_tags);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                for(int i = 0; i < errorTags.length; i++) {
                    final int index = i;
                    Button tagButton = new Button(dialogView.getContext());
                    tagButton.setText(errorTags[i]);
                    tagButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            addTag(errorTags[index]);
                            tagDialog.cancel();
                        }
                    });
                    root.addView(tagButton, params);
                }

                tagDialog.setContentView(dialogView);
                tagDialog.show();
            }
        });

        tagList = findViewById(R.id.tagList);
        tagAdapter = new MySimpleArrayAdapter(this, new ArrayList<String>());
        tagList.setAdapter(tagAdapter);
    }

    private void addTag(String tag) {
        //TODO Liste
        tagNames.add(tag);
        tagAdapter.notifyDataSetChanged();
    }

    public void next(View view) {
        if(flipper.getCurrentView() == first) {
            this.setTitle("Attach Pictures");
            nextButton.setText("Create");
            flipper.showNext();
        } else {
            nextButton.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);

            SharedPreferences preferences = getSharedPreferences(Defaults.PREF_FILE_KEY, Context.MODE_PRIVATE);
            int author = preferences.getInt(Defaults.ID_PREFERENCE, -1);

            String title = "bla";

            /*Report report = new Report(-1, author, device.getID(), title);

            RequestQueue queue = VolleyManager.getInstance(this).getRequestQueue();

            RequestFactory factory = new RequestFactory();
            RequestFactory.DefaultRequest request = factory.createReportCreationRequest(this, progressBar, nextButton, report);

            queue.add(request);*/
        }
    }

    private class MySimpleArrayAdapter extends ArrayAdapter<String> {
        private final Context context;

        private MySimpleArrayAdapter(Context context, ArrayList<String> values) {
            super(context, -1, values);
            this.context = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.row_maintenance, parent, false);
            }

            TextView nameView = convertView.findViewById(R.id.tagName);

            String tagName = this.getItem(position);

            nameView.setText(tagName);

            return convertView;
        }
    }
}
