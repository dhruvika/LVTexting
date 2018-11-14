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

    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Context context = this;
        sharedPref = context.getSharedPreferences("prefs", Context.MODE_PRIVATE);
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

        ViewGroup settingsContainer = findViewById( R.id.SETTINGS_CONTAINER );
        List<TextView> textViews = getViewsFromGroup(settingsContainer);

        for( int i = 0; i < textViews.size(); i++ ) {
            textViews.get(i).setTextSize(TypedValue.COMPLEX_UNIT_SP, currentSize);
        }
    }

    public List<TextView> getViewsFromGroup(ViewGroup viewGroup) {
        /*
        Recursive method that finds all textViews in given viewGroup and returns them as a list
         */

        List<TextView> viewList = new ArrayList<>();

        for( int i = 0; i < viewGroup.getChildCount(); i++ ) {
            if (viewGroup.getChildAt(i) instanceof TextView) {
                TextView textView = (TextView) viewGroup.getChildAt(i);
                viewList.add(textView);
            }

            if (viewGroup.getChildAt(i) instanceof ViewGroup) {
                ViewGroup childViewGroup = (ViewGroup) viewGroup.getChildAt(i);
                viewList.addAll(getViewsFromGroup(childViewGroup));
            }
        }

        return viewList;
    }
}