package com.example.dhruvikasahni.lvtexting;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;

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
        SettingsManager.applyThemeToView(this, (ViewGroup) findViewById(R.id.Settings_Container));
    }
}

