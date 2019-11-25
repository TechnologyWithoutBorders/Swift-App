package ngo.teog.swift.gui.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import ngo.teog.swift.R;
import ngo.teog.swift.gui.AboutActivity;
import ngo.teog.swift.gui.BaseActivity;
import ngo.teog.swift.gui.deviceCreation.NewDeviceActivity;
import ngo.teog.swift.gui.deviceInfo.DeviceInfoActivity;
import ngo.teog.swift.gui.hospital.HospitalActivity;
import ngo.teog.swift.gui.login.LoginActivity;
import ngo.teog.swift.gui.maintenance.SearchActivity;
import ngo.teog.swift.gui.reportInfo.ReportInfoActivity;
import ngo.teog.swift.gui.userInfo.UserInfoActivity;
import ngo.teog.swift.gui.userProfile.UserProfileActivity;
import ngo.teog.swift.helpers.Defaults;
import ngo.teog.swift.helpers.ResourceKeys;
import ngo.teog.swift.helpers.data.AppModule;
import ngo.teog.swift.helpers.data.DaggerAppComponent;
import ngo.teog.swift.helpers.data.HospitalDatabase;
import ngo.teog.swift.helpers.data.RoomModule;

public class MainActivity extends BaseActivity {

    private ViewPager mViewPager;
    private DemoCollectionPagerAdapter mDemoCollectionPagerAdapter;

    @Inject
    HospitalDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = this.getIntent();

        String appLinkAction = intent.getAction();
        Uri appLinkData = intent.getData();

        if(Intent.ACTION_VIEW.equals(appLinkAction) && appLinkData != null) {
            //TODO im Beispiel wird protected void onNewIntent(Intent intent) überschrieben

            try {
                List<String> pathSegments = appLinkData.getPathSegments();

                //Scheme: /<type>/<country>/<hospital>/<device/user>/[<report>]

                String type = pathSegments.get(0);
                int hospital = Integer.parseInt(pathSegments.get(2));

                Intent openIntent = null;

                if(ResourceKeys.DEVICE.equals(type)) {
                    int deviceNumber = Integer.parseInt(pathSegments.get(3));

                    openIntent = new Intent(MainActivity.this, DeviceInfoActivity.class);
                    openIntent.putExtra(ResourceKeys.DEVICE_ID, deviceNumber);
                } else if(ResourceKeys.USER.equals(type)) {
                    int userNumber = Integer.parseInt(pathSegments.get(3));

                    openIntent = new Intent(MainActivity.this, UserInfoActivity.class);
                    openIntent.putExtra(ResourceKeys.USER_ID, userNumber);
                } else if(ResourceKeys.REPORT.equals(type)) {
                    int deviceNumber = Integer.parseInt(pathSegments.get(3));
                    int reportNumber = Integer.parseInt(pathSegments.get(4));

                    openIntent = new Intent(MainActivity.this, ReportInfoActivity.class);
                    openIntent.putExtra(ResourceKeys.DEVICE_ID, deviceNumber);
                    openIntent.putExtra(ResourceKeys.REPORT_ID, reportNumber);
                }

                startActivity(openIntent);

            } catch(Exception e) {
                Toast.makeText(this.getApplicationContext(), "invalid item link", Toast.LENGTH_SHORT).show();
            }
        }

        TabLayout tabLayout = findViewById(R.id.tab_layout);

        mDemoCollectionPagerAdapter =
                new DemoCollectionPagerAdapter(
                        getSupportFragmentManager());
        mViewPager = findViewById(R.id.pager);
        mViewPager.setAdapter(mDemoCollectionPagerAdapter);
        mViewPager.setCurrentItem(1);

        for(int i = 0; i < 3; i++) {
            tabLayout.addTab(
                    tabLayout.newTab()
                            .setText("Tab " + (i + 1)));
        }

        DaggerAppComponent.builder()
                .appModule(new AppModule(getApplication()))
                .roomModule(new RoomModule(getApplication()))
                .build()
                .inject(this);
    }

    public class DemoCollectionPagerAdapter extends FragmentPagerAdapter {
        private final String[] PAGE_NAMES = {"Scanner", "Todo", "Calendar"};

        public DemoCollectionPagerAdapter(FragmentManager fm) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @Override
        @NonNull
        public Fragment getItem(int i) {
            switch(i) {
                case 0:
                    return new BarcodeFragment();
                case 1:
                    return new TodoFragment();
                case 2:
                    return new CalendarFragment();
            }

            //necessary, because method is annotated with @NonNull
            return new TodoFragment();
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return PAGE_NAMES[position];
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(mViewPager.getCurrentItem() == 0) {
            return ((BarcodeFragment)mDemoCollectionPagerAdapter.getItem(0)).onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.profileItem:
                startUserProfileActivity();
                return true;
            case R.id.hospitalItem:
                startHospitalActivity();
                return true;
            case R.id.logoutItem:
                logout();
                return true;
            case R.id.aboutItem:
                startAboutActivity();
                return true;
            case R.id.maintenance:
                startMaintenanceActivity();
                return true;
            case R.id.info:
                showInfo(R.string.mainactivity);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void startAboutActivity() {
        Intent intent = new Intent(MainActivity.this, AboutActivity.class);
        startActivity(intent);
    }

    public void logout() {
        ExecutorService executor = Executors.newFixedThreadPool(1);

        executor.execute(() -> {
            database.clearAllTables();
            executor.shutdown();
        });

        //delete shared preferences
        SharedPreferences preferences = getSharedPreferences(Defaults.PREF_FILE_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();

        //delete files (images)
        File imageDir = new File(getFilesDir(), Defaults.DEVICE_IMAGE_PATH);

        if(imageDir.exists()) {
            for(File file : imageDir.listFiles()) {
                file.delete();
            }
        }

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);

        this.finish();
    }

    public void startUserProfileActivity() {
        Intent intent = new Intent(MainActivity.this, UserProfileActivity.class);
        startActivity(intent);
    }

    public void startHospitalActivity() {
        Intent intent = new Intent(MainActivity.this, HospitalActivity.class);
        startActivity(intent);
    }

    public void startNewDeviceActivity(View view) {
        Intent intent = new Intent(MainActivity.this, NewDeviceActivity.class);
        startActivity(intent);
    }

    public void startMaintenanceActivity() {
        Intent intent = new Intent(MainActivity.this, SearchActivity.class);
        startActivity(intent);
    }
}


