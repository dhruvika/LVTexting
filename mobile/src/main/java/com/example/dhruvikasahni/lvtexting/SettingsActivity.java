package com.example.dhruvikasahni.lvtexting;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        onFontChange();
    }

    public void onFontChange() {
        SettingsManager.onFontChange(this, (ViewGroup) findViewById(R.id.Settings_Container));
    }
    public void onChangeFontSize(View v) {

        SharedPreferences sharedPref = SettingsManager.getSharedPreferences(this);

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

        int currentSize = sharedPref.getInt(FONT_SIZE, 20);

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
}

