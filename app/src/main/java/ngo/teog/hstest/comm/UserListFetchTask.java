package ngo.teog.hstest.comm;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

import ngo.teog.hstest.DeviceInfoActivity;
import ngo.teog.hstest.helpers.User;
import ngo.teog.hstest.helpers.UserFilter;

/**
 * Tasks werden zukünftig auf Volley-Requests umgestellt.
 * Created by Julian on 17.11.2017.
 */

@Deprecated
public class UserListFetchTask extends AsyncTask<UserFilter, Integer, ArrayList<User>> {

    private Context context;
    private View disable;
    private View enable;
    private ArrayAdapter<User> adapter;

    public UserListFetchTask(Context context, View disable, View enable, ArrayAdapter<User> adapter) {
        this.context = context;
        this.disable = disable;
        this.enable = enable;
        this.adapter = adapter;
    }

    @Override
    protected void onPreExecute() {
        disable.setVisibility(View.INVISIBLE);
        enable.setVisibility(View.VISIBLE);

        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if(activeNetworkInfo == null || !activeNetworkInfo.isConnectedOrConnecting()) {
            this.cancel(true);
        }
    }

    @Override
    protected ArrayList<User> doInBackground(UserFilter... filters) {
        String filterString = "";

        for(UserFilter filter : filters) {
            if(filter != null) {
                filterString += "&" + filter.getType() + "=" + filter.getValue();
            }
        }

        int resCode;
        InputStream in;
        try {
            URL url = new URL("https://teog.virlep.de/devices.php?action=fetch" + filterString);
            URLConnection urlConn = url.openConnection();

            HttpsURLConnection httpsConn = (HttpsURLConnection) urlConn;
            httpsConn.setAllowUserInteraction(false);
            httpsConn.setInstanceFollowRedirects(true);
            httpsConn.setRequestMethod("GET");
            httpsConn.connect();
            resCode = httpsConn.getResponseCode();

            if(resCode == HttpsURLConnection.HTTP_OK) {
                in = httpsConn.getInputStream();

                //ArrayList<User> result = XMLParser.getInstance().p(in);

                in.close();

                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<User> result) {
        if(adapter != null) {
            adapter.clear();
            adapter.addAll(result);
        } else {
            if(result.size() > 0) {
                Intent intent = new Intent(context, DeviceInfoActivity.class);//TODO generisch
                intent.putExtra("device", result.get(0));
                context.startActivity(intent);
            } else {
                Toast.makeText(context.getApplicationContext(), "device not found", Toast.LENGTH_SHORT).show();
            }
        }

        enable.setVisibility(View.INVISIBLE);
        disable.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onCancelled() {
        Toast.makeText(context.getApplicationContext(), "no internet connection", Toast.LENGTH_SHORT).show();

        if(adapter != null) {
            adapter.clear();
            adapter.add(null);
        }

        enable.setVisibility(View.INVISIBLE);
        disable.setVisibility(View.VISIBLE);
    }
}
