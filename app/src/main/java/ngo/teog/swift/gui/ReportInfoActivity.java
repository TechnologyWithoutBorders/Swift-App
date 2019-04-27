package ngo.teog.swift.gui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import ngo.teog.swift.R;
import ngo.teog.swift.helpers.Defaults;
import ngo.teog.swift.helpers.data.Report;
import ngo.teog.swift.helpers.data.ReportInfo;

public class ReportInfoActivity extends BaseActivity {

    private ReportInfo report;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_info);

        Intent intent = this.getIntent();
        report = (ReportInfo)intent.getSerializableExtra("REPORT");

        TextView dateView = findViewById(R.id.dateView);
        dateView.setText(Defaults.DATETIME_FORMAT.format(report.getReport().getCreated()));

        TextView authorView = findViewById(R.id.authorView);
        authorView.setText(report.getAuthors().get(0).getName());

        TextView stateChangeView = findViewById(R.id.stateChangeView);
        stateChangeView.setText(getResources().getStringArray(R.array.device_states)[report.getReport().getPreviousState()] + " -> " + getResources().getStringArray(R.array.device_states)[report.getReport().getCurrentState()]);

        TextView descriptionView = findViewById(R.id.descriptionView);
        descriptionView.setText(report.getReport().getDescription());
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
            case R.id.info:
                showInfo(R.string.reportInfoActivity);
                return true;
            case R.id.share:
                Intent intent = new Intent(Intent.ACTION_SEND);

                intent.putExtra(Intent.EXTRA_TEXT,"I want to show you this report: http://teog.virlep.de/report/" + Integer.toString(report.getReport().getId()));
                intent.setType("text/plain");
                startActivity(Intent.createChooser(intent, "Share report link"));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}


