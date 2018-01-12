package ngo.teog.hstest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import ngo.teog.hstest.helpers.Report;

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
        nameView.setText(report.getDevice().getAssetNumber());

        TextView typeView = findViewById(R.id.typeView);
        typeView.setText(report.getDevice().getType());

        TextView dateNameView = findViewById(R.id.dateNameView);
        dateNameView.setText(report.getDate() + " " + report.getAuthor().getFullName());
    }
}
