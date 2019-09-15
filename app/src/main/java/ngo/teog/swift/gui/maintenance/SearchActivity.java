package ngo.teog.swift.gui.maintenance;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import ngo.teog.swift.R;
import ngo.teog.swift.gui.BaseActivity;
import ngo.teog.swift.helpers.Defaults;
import ngo.teog.swift.helpers.ResourceKeys;

public class SearchActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_search1);

        setContentView(R.layout.coming_soon_dummy);

        TextView infoView = findViewById(R.id.infoView);
        infoView.setText("please use the 'my hospital' menu to browse your hospital");

        /*Button hospitalButton = findViewById(R.id.hospitalButton);
        Button deviceButton = findViewById(R.id.deviceButton);
        Button userButton = findViewById(R.id.userButton);

        hospitalButton.setOnClickListener(view -> {
            Intent intent = new Intent(SearchActivity.this, SearchActivity3.class);
            intent.putExtra(Defaults.SCOPE, Defaults.SCOPE_GLOBAL);
            intent.putExtra(Defaults.SEARCH_OBJECT, ResourceKeys.HOSPITAL);

            startActivity(intent);
        });

        deviceButton.setOnClickListener(view -> {
            Intent intent = new Intent(SearchActivity.this, SearchActivity2.class);
            intent.putExtra(Defaults.SEARCH_OBJECT, ResourceKeys.DEVICE);

            startActivity(intent);
        });

        userButton.setOnClickListener(view -> {
            Intent intent = new Intent(SearchActivity.this, SearchActivity2.class);
            intent.putExtra(Defaults.SEARCH_OBJECT, ResourceKeys.USER);

            startActivity(intent);
        });*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_maintenance, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item, R.string.maintenance_info);
    }
}
