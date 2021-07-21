package com.example.textposition;

// https://stackoverflow.com/questions/52428868/get-position-of-the-text-inside-a-textview

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Guideline;

public class MainActivity extends AppCompatActivity {
    private TextView tv;
    private Guideline guidelineStart;
    private Guideline guidelineEnd;
    private ConstraintLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // This is the top TextView
        tv = findViewById(R.id.timeTextView);

        // Get the vertical start and end guidelines which are set to 0% and 100% to start. The
        // left bottom TextView is constrained to the guidelineStart and the right bottom
        // TextView is constrained to guidelineEnd.
        guidelineStart = findViewById(R.id.guidelineStart);
        guidelineEnd = findViewById(R.id.guidelineEnd);

        // This is our ConstraintLayout. We will wait for everthing to layout so we can get
        // good measurements.
        layout = findViewById(R.id.layout);
        layout.post(() -> {
            int layoutWidth = layout.getWidth();

            // Get the starting and ending positions from the Layout of the top TextView.
            float textStart = tv.getLayout().getLineLeft(0);
            float textEnd = tv.getLayout().getLineRight(0);
            float viewX = tv.getX();
            // Now set the start and end guidelines to shift our bottom two TextViews.
            // We could also use guidelines with pixel offsets.
            guidelineStart.setGuidelinePercent((viewX + textStart) / layoutWidth);
            guidelineEnd.setGuidelinePercent((viewX + textEnd) / layoutWidth);
        });
    }
}
