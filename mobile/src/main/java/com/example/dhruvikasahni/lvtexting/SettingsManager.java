package com.example.dhruvikasahni.lvtexting;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class SettingsManager {

    public static SharedPreferences getSharedPreferences(Context context) {
        /*
        Get shared preferences for this app
         */

        SharedPreferences sharedPref = context.getSharedPreferences("prefs", Context.MODE_PRIVATE);
        return sharedPref;
    }

    public static void applySettingsToTheme(Context context) {
        /*
        Apply settings changes to the context's theme
         */

        /*
        SharedPreferences sharedPref = getSharedPreferences(context);
        int currentSize = sharedPref.getInt("FONT_SIZE",20);
         */
        context.getTheme().applyStyle(R.style.FontStyle_Medium, true);
    }
}
