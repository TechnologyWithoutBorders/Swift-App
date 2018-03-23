package ngo.teog.swift;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import ngo.teog.swift.helpers.HospitalDevice;

public class NewReportActivity extends AppCompatActivity {

    private ViewFlipper flipper;
    private LinearLayout first;
    private LinearLayout second;
    private Button nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_report);

        first = findViewById(R.id.first);
        second = findViewById(R.id.second);
        nextButton = findViewById(R.id.nextButton);
        flipper = findViewById(R.id.flipper);

        Animation in = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        Animation out = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right);

        flipper.setInAnimation(in);
        flipper.setOutAnimation(out);
    }

    public void next(View view) {
        if(flipper.getCurrentView() == first) {
            this.setTitle("Attach Pictures");
            nextButton.setText("Create");
            flipper.showNext();
        } else {
            //TODO Create
        }
    }
}
