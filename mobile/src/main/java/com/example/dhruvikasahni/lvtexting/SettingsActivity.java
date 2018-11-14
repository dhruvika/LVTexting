package com.example.dhruvikasahni.lvtexting;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

public class SettingsActivity extends AppCompatActivity {

    private SharedPreferences sharedPref;
    private TextView textPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Context context = this;
        sharedPref = context.getSharedPreferences("prefs", Context.MODE_PRIVATE);
        textPreview = ((SettingsActivity) context).findViewById(R.id.SETTINGS_PREVIEW);
        onFontChange();
    }

    public void onChangeFontSize(View v) {

        // Get preference
        int buttonID = v.getId();
        int change = 0;
        switch (buttonID) {
            case R.id.FONT_SIZE_I:
                change = 1;
                break;
            case R.id.FONT_SIZE_D:
                change = -1;
                break;
        }

        String FONT_SIZE = "FONT_SIZE";
        int MAX_FONT_SIZE = 50;
        int MIN_FONT_SIZE = 14;

        int currentSize = sharedPref.getInt(FONT_SIZE,20);

        // Change value
        int newSize = currentSize + change;
        if (newSize > MAX_FONT_SIZE) {
            newSize = MAX_FONT_SIZE;
        }
        if (newSize < MIN_FONT_SIZE) {
            newSize = MIN_FONT_SIZE;
        }

        // Set preference (Try using commit instead of apply)
        sharedPref.edit().putInt(FONT_SIZE, newSize).apply();
        onFontChange();
    }

    public void onFontChange() {
        // Set style from preference
        int currentSize = sharedPref.getInt("FONT_SIZE",20);
        textPreview.setTextSize(TypedValue.COMPLEX_UNIT_SP, currentSize);

        LinearLayout layoutContainer = findViewById( R.id.SETTINGS_LAYOUT );
        for( int i = 0; i < layoutContainer.getChildCount(); i++ ) {
            if (layoutContainer.getChildAt(i) instanceof TextView) {
                TextView textView = (TextView) layoutContainer.getChildAt(i);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, currentSize);
            }
        }
    }
}