package ngo.teog.swift.gui.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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
import androidx.lifecycle.ViewModelProvider;
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
import ngo.teog.swift.gui.reportInfo.ReportInfoActivity;
import ngo.teog.swift.gui.userInfo.UserInfoActivity;
import ngo.teog.swift.gui.userProfile.UserProfileActivity;
import ngo.teog.swift.helpers.Defaults;
import ngo.teog.swift.helpers.ResourceKeys;
import ngo.teog.swift.helpers.data.AppModule;
import ngo.teog.swift.helpers.data.DaggerAppComponent;
import ngo.teog.swift.helpers.data.HospitalDatabase;
import ngo.teog.swift.helpers.data.RoomModule;
import ngo.teog.swift.helpers.data.ViewModelFactory;

/**
 * Creates a tab layout holding the three main tabs. Provides some general functionality and implements the main menu.
 * @author nitelow
 */
public class MainActivity extends BaseActivity {

    private static final int SCANNER_TAB = 0;
    private static final int TODO_TAB = 1;
    private static final int CALENDAR_TAB = 2;

    private static final int APP_LINK_TYPE_SEGMENT = 0;
    private static final int APP_LINK_COUNTRY_SEGMENT = 1;
    private static final int APP_LINK_HOSPITAL_SEGMENT = 2;
    //device and user share same segment
    private static final int APP_LINK_DEVICE_SEGMENT = 3;
    private static final int APP_LINK_USER_SEGMENT = 3;
    private static final int APP_LINK_REPORT_SEGMENT = 4;

    private ViewPager mViewPager;
    private DemoCollectionPagerAdapter mDemoCollectionPagerAdapter;

    @Inject
    HospitalDatabase database;

    @Inject
    ViewModelFactory viewModelFactory;

    MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DaggerAppComponent.builder()
                .appModule(new AppModule(this.getApplication()))
                .roomModule(new RoomModule(this.getApplication()))
                .build()
                .inject(this);

        SharedPreferences preferences = this.getSharedPreferences(Defaults.PREF_FILE_KEY, Context.MODE_PRIVATE);
        int id = preferences.getInt(Defaults.ID_PREFERENCE, -1);

        viewModel = new ViewModelProvider(this, viewModelFactory).get(MainViewModel.class);
        viewModel.init(id);

        handleIntent(this.getIntent());

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

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        String appLinkAction = intent.getAction();
        Uri appLinkData = intent.getData();

        if(Intent.ACTION_VIEW.equals(appLinkAction) && appLinkData != null) {
            SharedPreferences preferences = this.getSharedPreferences(Defaults.PREF_FILE_KEY, Context.MODE_PRIVATE);
            String userCountry = preferences.getString(Defaults.COUNTRY_PREFERENCE, null);

            viewModel.getUserHospital().observe(this, userHospital -> {
                if(userHospital != null) {
                    try {
                        int userHospitalId = userHospital.getId();

                        List<String> pathSegments = appLinkData.getPathSegments();

                        //Scheme: /<type>/<country>/<hospital ID>/<device/user ID>/[<report ID>]

                        String type = pathSegments.get(APP_LINK_TYPE_SEGMENT);
                        String country = pathSegments.get(APP_LINK_COUNTRY_SEGMENT);
                        int hospital = Integer.parseInt(pathSegments.get(APP_LINK_HOSPITAL_SEGMENT));

                        if(country.equals(userCountry)) {
                            if (hospital == userHospitalId) {
                                Intent openIntent;

                                if (ResourceKeys.DEVICE.equals(type)) {
                                    int deviceNumber = Integer.parseInt(pathSegments.get(APP_LINK_DEVICE_SEGMENT));

                                    openIntent = new Intent(MainActivity.this, DeviceInfoActivity.class);
                                    openIntent.putExtra(ResourceKeys.DEVICE_ID, deviceNumber);
                                } else if (ResourceKeys.USER.equals(type)) {
                                    int userNumber = Integer.parseInt(pathSegments.get(APP_LINK_USER_SEGMENT));

                                    openIntent = new Intent(MainActivity.this, UserInfoActivity.class);
                                    openIntent.putExtra(ResourceKeys.USER_ID, userNumber);
                                } else if (ResourceKeys.REPORT.equals(type)) {
                                    int deviceNumber = Integer.parseInt(pathSegments.get(APP_LINK_DEVICE_SEGMENT));
                                    int reportNumber = Integer.parseInt(pathSegments.get(APP_LINK_REPORT_SEGMENT));

                                    openIntent = new Intent(MainActivity.this, ReportInfoActivity.class);
                                    openIntent.putExtra(ResourceKeys.DEVICE_ID, deviceNumber);
                                    openIntent.putExtra(ResourceKeys.REPORT_ID, reportNumber);
                                } else {
                                    throw new Exception("invalid item type");
                                }

                                startActivity(openIntent);
                            } else {
                                Toast.makeText(this.getApplicationContext(), "wrong country", Toast.LENGTH_SHORT).show();//TODO extract string
                            }
                        } else {
                            Toast.makeText(this.getApplicationContext(), "wrong hospital", Toast.LENGTH_SHORT).show();//TODO extract string
                        }
                    } catch(Exception e) {
                        Log.e(this.getClass().getName(), e.toString(), e);
                        Toast.makeText(this.getApplicationContext(), getString(R.string.invalid_item_link), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public class DemoCollectionPagerAdapter extends FragmentPagerAdapter {
        public DemoCollectionPagerAdapter(FragmentManager fm) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @Override
        @NonNull
        public Fragment getItem(int i) {
            switch(i) {
                case SCANNER_TAB:
                    return new BarcodeFragment();
                case CALENDAR_TAB:
                    return new CalendarFragment();
                case TODO_TAB:
                default:
                    //necessary, because method is annotated with @NonNull
                    return new TodoFragment();
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return getResources().getStringArray(R.array.main_tab_names)[position];
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
        if(mViewPager.getCurrentItem() == SCANNER_TAB) {
            BarcodeFragment barcodeFragment = (BarcodeFragment)mDemoCollectionPagerAdapter.getItem(SCANNER_TAB);

            return barcodeFragment.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.profileItem) {
            startUserProfileActivity();
        } else if(id == R.id.hospitalItem) {
            startHospitalActivity();
        } else if(id == R.id.logoutItem) {
            logout();
        } else if(id == R.id.aboutItem) {
            startAboutActivity();
        } else if(id == R.id.info) {
            showInfo(R.string.mainactivity);
        } else {
            return super.onOptionsItemSelected(item);
        }

        return true;
    }

    public void startAboutActivity() {
        Intent intent = new Intent(MainActivity.this, AboutActivity.class);
        startActivity(intent);
    }

    /**
     * Logs out the user. Wipes the database and shared preferences and deletes all downloaded device images.
     */
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
}


