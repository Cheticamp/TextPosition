package com.example.textposition;

// See https://stackoverflow.com/questions/68452314/how-to-left-align-to-a-centered-text-in-android/68456182#68456182

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Guideline;

import static android.view.Gravity.CENTER_HORIZONTAL;

public class MainActivity extends AppCompatActivity {
    private TextView mTextViewTop;
    private TextView mTextViewLeft;
    private TextView mTextViewRight;
    private Guideline mGuidelineStart;
    private Guideline mGuidelineEnd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextViewTop = findViewById(R.id.timeTextView);

        // These are the views to align.
        mTextViewLeft = findViewById(R.id.timeTextView2);
        mTextViewRight = findViewById(R.id.timeTextView3);

        // Get the vertical start and end guidelines which are set to something arbitrary to start.
        // The left bottom TextView is constrained to the guidelineStart and the right bottom
        // TextView is constrained to guidelineEnd.
        mGuidelineStart = findViewById(R.id.guidelineStart);
        mGuidelineEnd = findViewById(R.id.guidelineEnd);

        // This is our ConstraintLayout. We will wait for everything to layout so we can get
        // good measurements.
        final ConstraintLayout layout = findViewById(R.id.layout);
        layout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                // Remove the listener so we don't loop endlessly.
                layout.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                // Get tight bounds for the text within the TextViews taking padding and
                // gravity into account. Bounds are relative to the subject view.
                Rect boundsTop = getTextBounds(mTextViewTop);
                Rect boundsLeft = getTextBounds(mTextViewLeft);
                Rect boundsRight = getTextBounds(mTextViewRight);

                // Get the starting offset for the top TextView including any horizontal translation.
                float viewX = mTextViewTop.getX();

                // Set guidelines to the start and end of the text.
                float guidelineStartOffset = viewX + boundsTop.left;
                float guidelineEndOffset = viewX + boundsTop.right;
                mGuidelineStart.setGuidelineBegin((int) guidelineStartOffset);
                mGuidelineEnd.setGuidelineBegin((int) guidelineEndOffset + LINE_WIDTH);

                // Tweak placements
                // boundsLeft.left is how far into the TextView the text appears.
                mTextViewLeft.setTranslationX(-boundsLeft.left - LINE_WIDTH);

                // Shift by distance from end of text to end of view.
                mTextViewRight.setTranslationX(mTextViewRight.getWidth() - boundsRight.right);

                // Draw the outlines of the TextViews.
                drawTextBounds(mTextViewTop, boundsTop);
                drawTextBounds(mTextViewLeft, boundsLeft);
                drawTextBounds(mTextViewRight, boundsRight);
            }
        });
    }

    private void adjustBoundsForGravity(TextView textView, Rect bounds) {
        if ((textView.getGravity() & Gravity.HORIZONTAL_GRAVITY_MASK) == CENTER_HORIZONTAL) {
            Rect gravityOut = new Rect();
            Paint paint = textView.getPaint();
            int textWidth = (int) paint.measureText(textView.getText().toString());
            int textHeight = textView.getLayout().getHeight();
            int paddingStart = textView.getTotalPaddingStart();
            int paddingEnd = textView.getTotalPaddingEnd();
            Rect textViewBounds = new Rect(0, 0, textView.getWidth() - paddingStart - paddingEnd, textView.getHeight());
            Gravity.apply(CENTER_HORIZONTAL, textWidth, textHeight, textViewBounds, gravityOut);
            bounds.left += gravityOut.left;
            bounds.right += gravityOut.left;
        }
    }

    private Rect getTextBounds(TextView textView) {
        // bounds will store the rectangle that will tightly circumscribe the text.
        Rect bounds = new Rect();

        // Get the bounds for the text. Top and bottom are measured from the baseline. Left
        // and right are measured from 0.
        String s = (String) textView.getText();
        Paint textPaint = textView.getPaint();
        textPaint.getTextBounds(s, 0, s.length(), bounds);
        int baseline = textView.getBaseline();
        bounds.top += baseline;
        bounds.bottom += baseline;
        int startPadding = textView.getPaddingStart();
        bounds.left += startPadding;
        bounds.right += startPadding;
        adjustBoundsForGravity(textView, bounds);
        return bounds;
    }

    private void drawTextBounds(TextView textView, Rect bounds) {
        Bitmap bitmap = Bitmap.createBitmap(textView.getWidth(),
                textView.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint rectPaint = new Paint();
        rectPaint.setColor(Color.YELLOW);
        rectPaint.setStyle(Paint.Style.STROKE);
        rectPaint.setStrokeWidth(LINE_WIDTH);
        canvas.drawRect(bounds, rectPaint);
        textView.setForeground(new BitmapDrawable(getResources(), bitmap));
    }

    private static final int LINE_WIDTH = 1;
}
