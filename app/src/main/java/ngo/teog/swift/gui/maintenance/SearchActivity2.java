package ngo.teog.swift.gui.maintenance;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;

import ngo.teog.swift.R;
import ngo.teog.swift.gui.BaseActivity;
import ngo.teog.swift.helpers.Defaults;

public class SearchActivity2 extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search2);

        Intent intent = getIntent();

        String searchObject = intent.getStringExtra(Defaults.SEARCH_OBJECT);

        Button localButton = findViewById(R.id.localButton);
        Button otherButton = findViewById(R.id.otherButton);

        localButton.setOnClickListener(view -> {
            Intent intent1 = new Intent(SearchActivity2.this, SearchActivity3.class);
            intent1.putExtra(Defaults.SCOPE, Defaults.SCOPE_LOCAL);
            intent1.putExtra(Defaults.SEARCH_OBJECT, searchObject);

            startActivity(intent1);
        });

        otherButton.setOnClickListener(view -> {
            Intent intent12 = new Intent(SearchActivity2.this, SearchActivity3.class);
            intent12.putExtra(Defaults.SCOPE, Defaults.SCOPE_GLOBAL);
            intent12.putExtra(Defaults.SEARCH_OBJECT, searchObject);

            startActivity(intent12);
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
        return super.onOptionsItemSelected(item, R.string.maintenance_info);
    }
}
