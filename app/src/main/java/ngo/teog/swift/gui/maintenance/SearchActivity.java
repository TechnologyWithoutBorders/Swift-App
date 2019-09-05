package ngo.teog.swift.gui.maintenance;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import ngo.teog.swift.R;
import ngo.teog.swift.gui.BaseActivity;
import ngo.teog.swift.helpers.Defaults;

public class SearchActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search1);

        Button hospitalButton = findViewById(R.id.hospitalButton);
        Button deviceButton = findViewById(R.id.deviceButton);
        Button userButton = findViewById(R.id.userButton);

        hospitalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SearchActivity.this, SearchActivity3.class);
                intent.putExtra(Defaults.SCOPE, Defaults.SCOPE_GLOBAL);
                intent.putExtra(Defaults.SEARCH_OBJECT, Defaults.HOSPITAL_KEY);

                startActivity(intent);
            }
        });

        deviceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SearchActivity.this, SearchActivity2.class);
                intent.putExtra(Defaults.SEARCH_OBJECT, Defaults.DEVICE_KEY);

                startActivity(intent);
            }
        });

        userButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SearchActivity.this, SearchActivity2.class);
                intent.putExtra(Defaults.SEARCH_OBJECT, Defaults.USER_KEY);

                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_maintenance, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch(item.getItemId()) {
            case R.id.info:
                showInfo(R.string.maintenance_info);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
