package ngo.teog.swift.gui.main;

import android.os.Bundle;
import android.widget.CalendarView;

import java.util.Calendar;

import ngo.teog.swift.R;
import ngo.teog.swift.gui.BaseActivity;

public class CalendarActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        CalendarView maintenanceCalendar = findViewById(R.id.maintenanceCalendar);

        long today = Calendar.getInstance().getTimeInMillis();
        //maintenanceCalendar.setMinDate(today);
    }
}
