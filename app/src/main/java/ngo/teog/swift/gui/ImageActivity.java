package ngo.teog.swift.gui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import ngo.teog.swift.R;

public class ImageActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_image_dialog);

        Intent intent = this.getIntent();
        File image = (File)intent.getSerializableExtra("IMAGE");

        try {
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(image));

            ImageView imageView = findViewById(R.id.imageView);
            imageView.setImageBitmap(b);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
