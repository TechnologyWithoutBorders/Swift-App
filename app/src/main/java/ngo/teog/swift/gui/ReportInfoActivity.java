package ngo.teog.swift.gui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import ngo.teog.swift.R;
import ngo.teog.swift.helpers.Report;

public class ReportInfoActivity extends BaseActivity {

    private Report report;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_info);

        Intent intent = this.getIntent();
        report = (Report)intent.getSerializableExtra("REPORT");

        TextView dateView = findViewById(R.id.dateView);
        dateView.setText(Report.reportFormat.format(report.getDateTime()));

        TextView authorView = findViewById(R.id.authorView);
        authorView.setText(report.getAuthorName());

        TextView stateChangeView = findViewById(R.id.stateChangeView);
        stateChangeView.setText(getResources().getStringArray(R.array.device_states)[report.getPreviousState()] + " -> " + getResources().getStringArray(R.array.device_states)[report.getCurrentState()]);

        TextView descriptionView = findViewById(R.id.descriptionView);
        descriptionView.setText(report.getDescription());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_report_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch(item.getItemId()) {
            case R.id.share:
                Intent intent = new Intent(Intent.ACTION_SEND);

                intent.putExtra(Intent.EXTRA_TEXT,"I want to show you this report: http://teog.virlep.de/report/" + Integer.toString(report.getID()));
                intent.setType("text/plain");
                startActivity(Intent.createChooser(intent, "Share report link"));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
