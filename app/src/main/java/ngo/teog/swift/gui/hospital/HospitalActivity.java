package ngo.teog.swift.gui.hospital;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import javax.inject.Inject;

import ngo.teog.swift.R;
import ngo.teog.swift.gui.BaseActivity;
import ngo.teog.swift.gui.deviceInfo.DeviceInfoActivity;
import ngo.teog.swift.helpers.Defaults;
import ngo.teog.swift.helpers.data.AppModule;
import ngo.teog.swift.helpers.data.DaggerAppComponent;
import ngo.teog.swift.helpers.data.HospitalDevice;
import ngo.teog.swift.helpers.data.RoomModule;
import ngo.teog.swift.helpers.data.User;
import ngo.teog.swift.helpers.data.ViewModelFactory;

public class HospitalActivity extends BaseActivity {

    @Inject
    ViewModelFactory viewModelFactory;

    private HospitalViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital);

        LinearLayout contentView = findViewById(R.id.contentView);
        final ExpandableListView hospitalListView = findViewById(R.id.hospitalList);

        hospitalListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View view, int groupPosition, int childPosition, long id) {
                switch(groupPosition) {
                    case 0:
                        Intent intent = new Intent(HospitalActivity.this, DeviceInfoActivity.class);
                        intent.putExtra("user", (User)hospitalListView.getExpandableListAdapter().getChild(groupPosition, childPosition));
                        startActivity(intent);
                        break;
                    case 1:
                        Intent intent2 = new Intent(HospitalActivity.this, DeviceInfoActivity.class);
                        intent2.putExtra("device", (HospitalDevice)hospitalListView.getExpandableListAdapter().getChild(groupPosition, childPosition));
                        startActivity(intent2);
                        break;
                }

                return false;
            }
        });

        TextView nameView = findViewById(R.id.nameView);
        TextView locationView = findViewById(R.id.locationView);

        DaggerAppComponent.builder()
                .appModule(new AppModule(getApplication()))
                .roomModule(new RoomModule(getApplication()))
                .build()
                .inject(this);

        SharedPreferences preferences = this.getSharedPreferences(Defaults.PREF_FILE_KEY, Context.MODE_PRIVATE);
        int id = preferences.getInt(Defaults.ID_PREFERENCE, -1);

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(HospitalViewModel.class);
        viewModel.init(id);
        viewModel.getHospital().observe(this, hospital -> {
            if(hospital != null) {
                nameView.setText(hospital.getName());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_my_hospital, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch(item.getItemId()) {
            case R.id.info:
                showInfo(R.string.hospital_activity);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
