package ngo.teog.swift;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.SpannableString;
import android.text.util.Linkify;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import ngo.teog.swift.helpers.Defaults;

public class MainActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private DemoCollectionPagerAdapter mDemoCollectionPagerAdapter;

    private BarcodeFragment codeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TabLayout tabLayout = findViewById(R.id.tab_layout);

        mDemoCollectionPagerAdapter =
                new DemoCollectionPagerAdapter(
                        getSupportFragmentManager());
        mViewPager = findViewById(R.id.pager);
        mViewPager.setAdapter(mDemoCollectionPagerAdapter);
        mViewPager.setCurrentItem(1);

        try {
            ActionBar actionBar = getSupportActionBar();

            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setLogo(R.mipmap.ic_launcher_round);
            actionBar.setDisplayUseLogoEnabled(true);
        } catch(NullPointerException e) {
            //ignore
        }

        for(int i = 0; i < 3; i++) {
            tabLayout.addTab(
                    tabLayout.newTab()
                            .setText("Tab " + (i + 1)));
        }

        Intent intent = this.getIntent();
        if(intent.hasExtra("NEWS")) {
            String news = intent.getStringExtra("NEWS");

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View view = this.getLayoutInflater().inflate(R.layout.dialog_news, null);
            TextView tv = view.findViewById(R.id.newsView);
            tv.setText(news);
            builder.setView(view);
            builder.setCancelable(true);

            AlertDialog alert1 = builder.create();
            alert1.show();
        }
    }

    public class DemoCollectionPagerAdapter extends FragmentPagerAdapter {
        public DemoCollectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            switch(i) {
                case 0:
                    if(codeFragment == null) {
                        codeFragment = new BarcodeFragment();
                    }

                    return codeFragment;
                case 1:
                    return new TodoFragment();
                case 2:
                    return new SearchFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String[] names = {"Scanner", "Todo", "Search"};

            return names[position];
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
            case R.id.logoutItem:
                logout();
                return true;
            case R.id.aboutItem:
                startAboutActivity();
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
        SharedPreferences preferences = getSharedPreferences(Defaults.PREF_FILE_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(getString(R.string.id_pref));
        editor.remove(getString(R.string.pw_pref));
        editor.commit();

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);

        this.finish();
    }

    public void startUserProfileActivity() {
        Intent intent = new Intent(MainActivity.this, UserProfileActivity.class);
        startActivity(intent);
    }

    public void startNewDeviceActivity(View view) {
        Intent intent = new Intent(MainActivity.this, NewDeviceActivity.class);
        startActivity(intent);
    }
}
