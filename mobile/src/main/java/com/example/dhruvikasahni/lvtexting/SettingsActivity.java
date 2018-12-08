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
        applySettingsToTheme();
        setContentView(R.layout.activity_settings);
    }

    public void applySettingsToTheme() {
        SettingsManager.applySettingsToTheme(this);
    }

    public void onChangePref(View v) {

        // Get preference
        int buttonID = v.getId();
        switch (buttonID) {
            case R.id.font_size_i:
                SettingsManager.changeFontSize(this, 1);
                break;
            case R.id.font_size_d:
                SettingsManager.changeFontSize(this, -1);
                break;
            case R.id.line_space_i:
                SettingsManager.changeFontSize(this, 1);
                break;
            case R.id.line_space_d:
                SettingsManager.changeFontSize(this, -1);
                break;
        }
        applySettingsToTheme();
    }
}

