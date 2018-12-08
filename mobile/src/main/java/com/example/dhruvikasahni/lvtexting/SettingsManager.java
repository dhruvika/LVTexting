package com.example.dhruvikasahni.lvtexting;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.Log;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class SettingsManager {

    private static SharedPreferences getSharedPreferences(Context context) {
        /*
        Get shared preferences for this app
         */
        SharedPreferences sharedPref = context.getSharedPreferences("prefs", Context.MODE_PRIVATE);
        return sharedPref;
    }

    private static final String FONT_SIZE = "FONT_SIZE";

    public static void applySettingsToTheme(Context context) {
        /*
        Apply settings changes to the context's theme
         */
        SharedPreferences sharedPref = getSharedPreferences(context);

        // Apply font size
        int currentFontSize = sharedPref.getInt(FONT_SIZE,5);
        switch (currentFontSize) {
            case 1:
                context.getTheme().applyStyle(R.style.FontSize_S1, true);
                break;
            case 2:
                context.getTheme().applyStyle(R.style.FontSize_S2, true);
                break;
            case 3:
                context.getTheme().applyStyle(R.style.FontSize_S3, true);
                break;
            case 4:
                context.getTheme().applyStyle(R.style.FontSize_S4, true);
                break;
            case 5:
                context.getTheme().applyStyle(R.style.FontSize_S5, true);
                break;
            case 6:
                context.getTheme().applyStyle(R.style.FontSize_S6, true);
                break;
            case 7:
                context.getTheme().applyStyle(R.style.FontSize_S7, true);
                break;
            case 8:
                context.getTheme().applyStyle(R.style.FontSize_S8, true);
                break;
            case 9:
                context.getTheme().applyStyle(R.style.FontSize_S9, true);
                break;
            default:
                context.getTheme().applyStyle(R.style.FontSize_S5, true);
                break;
        }
    }

    public static void applyThemeToView(Context context, ViewGroup viewContainer) {
        /*
        Apply theme changes to a given viewGroup. Only use this for preference screen
         */
        Resources.Theme theme = context.getTheme();
        List<TextView> textViews = getViewsFromGroup(viewContainer);

        int[] attributes = new int[] { R.attr.font_size };
        TypedArray array = theme.obtainStyledAttributes(attributes);

        final int fontSize = array.getDimensionPixelSize(0, 20);
        array.recycle();

        for( int i = 0; i < textViews.size(); i++ ) {
            textViews.get(i).setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSize);
        }
    }
    private static List<TextView> getViewsFromGroup(ViewGroup viewGroup) {
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

    public static void changeFontSize(Context context, int delta) {
        /*
        Change font size by delta
        */
        SharedPreferences sharedPref = getSharedPreferences(context);
        int MAX_FONT_SIZE = 9;
        int MIN_FONT_SIZE = 1;

        // Calculate new size
        int currentSize = sharedPref.getInt(FONT_SIZE, 5);
        int newSize = currentSize + delta;
        if (newSize > MAX_FONT_SIZE) { newSize = MAX_FONT_SIZE; }
        if (newSize < MIN_FONT_SIZE) { newSize = MIN_FONT_SIZE; }

        // Set preference (Try using commit instead of apply)
        sharedPref.edit().putInt(FONT_SIZE, newSize).apply();
        // Log.d("MyDebug", Integer.toString(newSize));
    }
}