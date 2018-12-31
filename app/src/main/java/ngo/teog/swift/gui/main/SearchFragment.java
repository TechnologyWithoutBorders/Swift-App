package ngo.teog.swift.gui.main;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.RequestQueue;

import java.util.ArrayList;

import ngo.teog.swift.gui.BaseFragment;
import ngo.teog.swift.R;
import ngo.teog.swift.communication.RequestFactory;
import ngo.teog.swift.communication.VolleyManager;
import ngo.teog.swift.helpers.SearchObject;

public class SearchFragment extends BaseFragment {

    private static final int DEVICE = 0;
    private static final int USER = 1;

    private SearchArrayAdapter adapter;
    private ProgressBar progressBar;
    private EditText searchField;
    private Spinner searchSpinner;
    private Button searchButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_search, container, false);

        searchSpinner = rootView.findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(rootView.getContext(), R.array.search_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        searchSpinner.setAdapter(adapter);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ListView listView = view.findViewById(R.id.maintenanceList);
        ArrayList<SearchObject> values = new ArrayList<>();

        progressBar = view.findViewById(R.id.progressBar);

        adapter = new SearchArrayAdapter(getContext(), values);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                SearchObject item = (SearchObject)adapterView.getItemAtPosition(i);

                Intent intent = new Intent(getContext(), item.getInfoActivityClass());
                intent.putExtra(item.getExtraIdentifier(), item);
                startActivity(intent);
            }
        });

        searchButton = view.findViewById(R.id.button22);
        searchField = view.findViewById(R.id.editText);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search();
            }
        });
    }

    private void search() {
        if(searchField.getText().length() > 0) {
            String searchString = searchField.getText().toString();
            RequestQueue queue = VolleyManager.getInstance(getContext()).getRequestQueue();

            progressBar.setVisibility(View.VISIBLE);
            searchButton.setVisibility(View.INVISIBLE);

            switch(searchSpinner.getSelectedItemPosition()) {
                case DEVICE:
                    if(this.checkForInternetConnection()) {

                        RequestFactory.DeviceListRequest request = new RequestFactory().createDeviceSearchRequest(getContext(), progressBar, searchButton, searchString, adapter);

                        queue.add(request);
                    }

                    break;

                case USER:
                    if(this.checkForInternetConnection()) {
                        RequestFactory.UserListRequest request = new RequestFactory().createUserSearchRequest(getContext(), progressBar, searchButton, searchString, adapter);

                        queue.add(request);
                    }

                    break;
            }

            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(searchField.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
        } else {
            searchField.setError("invalid search value");
        }
    }

    private class SearchArrayAdapter extends ArrayAdapter<SearchObject> {
        private final Context context;

        public SearchArrayAdapter(Context context, ArrayList<SearchObject> values) {
            super(context, -1, values);
            this.context = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.row_search, parent, false);
            }

            TextView nameView = convertView.findViewById(R.id.nameView);
            TextView infoView = convertView.findViewById(R.id.infoView);

            SearchObject object = this.getItem(position);

            if(object != null) {
                nameView.setText(object.getName());
                infoView.setText(object.getInformation());
            } else {
                nameView.setText("no internet connection");
                nameView.setTextColor(Color.RED);
                infoView.setText(null);
            }

            return convertView;
        }
    }
}
