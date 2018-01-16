package ngo.teog.hstest;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.InputStream;

import ngo.teog.hstest.helpers.HospitalDevice;

public class DeviceInfoActivity extends AppCompatActivity {

    private HospitalDevice device;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_info);

        Intent intent = this.getIntent();
        device = (HospitalDevice)intent.getSerializableExtra("device");

        ImageView imageView = findViewById(R.id.imageView);

        TextView statusView = findViewById(R.id.statusView);
        if(device.isWorking()) {
            statusView.setText("working");
            statusView.setBackgroundColor(Color.GREEN);
        } else {
            statusView.setText("not working");
            statusView.setBackgroundColor(Color.RED);
        }

        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        TextView assetNumberView = findViewById(R.id.assetNumberView);
        assetNumberView.setText(device.getAssetNumber());

        TextView nameView = findViewById(R.id.nameValueView);
        nameView.setText(device.getType());

        TextView serialNumberView = findViewById(R.id.serialNumberView);
        serialNumberView.setText(device.getSerialNumber());

        new DownloadImageTask(imageView, progressBar).execute("https://teog.virlep.de/graphics/" + device.getID() + ".jpg", null);
    }

    public void startReportActivity(View view) {
        Intent intent = new Intent(getApplicationContext(), NewReportActivity.class);
        intent.putExtra("DEVICE", device);

        startActivity(intent);
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        private ImageView imageView;
        private ProgressBar progressBar;

        public DownloadImageTask(ImageView imageView, ProgressBar progressBar) {
            this.imageView = imageView;
            this.progressBar = progressBar;
        }

        @Override
        protected void onPreExecute() {
            imageView.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if(result != null) {
                imageView.setImageBitmap(result);
            }
            progressBar.setVisibility(View.INVISIBLE);
            imageView.setVisibility(View.VISIBLE);
        }
    }
}
