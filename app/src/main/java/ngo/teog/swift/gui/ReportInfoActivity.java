package ngo.teog.swift.gui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.TextView;

import ngo.teog.swift.R;
import ngo.teog.swift.helpers.Report;

public class ReportInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_info);

        Intent intent = this.getIntent();
        Report report = (Report)intent.getSerializableExtra("REPORT");

        TextView assetView = findViewById(R.id.assetNumberView);
        assetView.setText(Integer.toString(report.getID()));

        TextView nameView = findViewById(R.id.nameView);

        TextView typeView = findViewById(R.id.typeView);

        TextView dateNameView = findViewById(R.id.dateNameView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_device_info, menu);
        return true;
    }
}
