package ngo.teog.swift.gui.main;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;

import java.util.Calendar;

import ngo.teog.swift.R;
import ngo.teog.swift.gui.BaseFragment;

public class CalendarFragment extends BaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_calendar, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        CalendarView maintenanceCalendar = view.findViewById(R.id.maintenanceCalendar);

        long today = Calendar.getInstance().getTimeInMillis();
        //maintenanceCalendar.setMinDate(today);
    }
}
