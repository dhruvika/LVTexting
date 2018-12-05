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

    public static void onFontChange(Context context, ViewGroup viewContainer) {
        /*
        Apply settings changes to the contents of a viewGroup
         */

        SharedPreferences sharedPref = getSharedPreferences(context);
        int currentSize = sharedPref.getInt("FONT_SIZE",20);

        List<TextView> textViews = getViewsFromGroup(viewContainer);

        for( int i = 0; i < textViews.size(); i++ ) {
            textViews.get(i).setTextSize(TypedValue.COMPLEX_UNIT_SP, currentSize);
        }
    }

    public static List<TextView> getViewsFromGroup(ViewGroup viewGroup) {
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
