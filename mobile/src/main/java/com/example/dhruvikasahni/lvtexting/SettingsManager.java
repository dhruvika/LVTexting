package com.example.dhruvikasahni.lvtexting;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.Log;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.view.WindowManager;
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

    private static Boolean appliedToMain = true;
    private static Boolean appliedToConvo = true;

    private static final String FONT_SIZE = "FONT_SIZE";
    private static final String LINE_SPACING = "LINE_SPACING";
    private static final String CHAR_SPACING = "CHAR_SPACING";
    private static final String BRIGHTNESS = "BRIGHTNESS";
    private static final String SPEAKER_SPEED = "SPEAKER_SPEED";
    private static final String SCREEN_PADDING = "SCREEN_PADDING";

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

        // Apply line spacing
        int currentLineSpacing = sharedPref.getInt(LINE_SPACING,0);
        switch (currentLineSpacing) {
            case 0:
                context.getTheme().applyStyle(R.style.LineSpacing_S0, true);
                break;
            case 1:
                context.getTheme().applyStyle(R.style.LineSpacing_S1, true);
                break;
            case 2:
                context.getTheme().applyStyle(R.style.LineSpacing_S2, true);
                break;
            case 3:
                context.getTheme().applyStyle(R.style.LineSpacing_S3, true);
                break;
            case 4:
                context.getTheme().applyStyle(R.style.LineSpacing_S4, true);
                break;
            case 5:
                context.getTheme().applyStyle(R.style.LineSpacing_S5, true);
                break;
        }

        // Apply char spacing
        int currentCharSpacing = sharedPref.getInt(CHAR_SPACING,0);
        switch (currentCharSpacing) {
            case 0:
                context.getTheme().applyStyle(R.style.CharSpacing_S0, true);
                break;
            case 1:
                context.getTheme().applyStyle(R.style.CharSpacing_S1, true);
                break;
            case 2:
                context.getTheme().applyStyle(R.style.CharSpacing_S2, true);
                break;
            case 3:
                context.getTheme().applyStyle(R.style.CharSpacing_S3, true);
                break;
            case 4:
                context.getTheme().applyStyle(R.style.CharSpacing_S4, true);
                break;
            case 5:
                context.getTheme().applyStyle(R.style.CharSpacing_S5, true);
                break;
        }

        // Apply screen padding
        int currentScreenPadding = sharedPref.getInt(SCREEN_PADDING,0);
        switch (currentScreenPadding) {
            case 0:
                context.getTheme().applyStyle(R.style.ScreenPadding_S0, true);
                break;
            case 1:
                context.getTheme().applyStyle(R.style.ScreenPadding_S1, true);
                break;
            case 2:
                context.getTheme().applyStyle(R.style.ScreenPadding_S2, true);
                break;
            case 3:
                context.getTheme().applyStyle(R.style.ScreenPadding_S3, true);
                break;
            case 4:
                context.getTheme().applyStyle(R.style.ScreenPadding_S4, true);
                break;
            case 5:
                context.getTheme().applyStyle(R.style.ScreenPadding_S5, true);
                break;
            case 6:
                context.getTheme().applyStyle(R.style.ScreenPadding_S6, true);
                break;
            case 7:
                context.getTheme().applyStyle(R.style.ScreenPadding_S7, true);
                break;
            case 8:
                context.getTheme().applyStyle(R.style.ScreenPadding_S8, true);
                break;
            case 9:
                context.getTheme().applyStyle(R.style.ScreenPadding_S9, true);
                break;
            default:
                context.getTheme().applyStyle(R.style.ScreenPadding_S0, true);
                break;
        }

        // Apply brightness
        int currentBrightness = sharedPref.getInt(BRIGHTNESS,100);
        float backLightValue = (float)currentBrightness/100;
        WindowManager.LayoutParams layoutParams = ((Activity)context).getWindow().getAttributes(); // Get Params
        layoutParams.screenBrightness = backLightValue; // Set Value
        ((Activity)context).getWindow().setAttributes(layoutParams); // Set params

        // Apply speaker speed
        float currentSpeakerSpeed = sharedPref.getFloat(SPEAKER_SPEED, 1.0f);
        // TODO apply currentSpeakerSpeed to text to speech playback
    }

    public static void applyThemeToView(Context context, ViewGroup viewContainer) {
        /*
        Apply theme changes to a given viewGroup. Only use this for preference screen
         */
        Resources.Theme theme = context.getTheme();
        List<TextView> textViews = getViewsFromGroup(viewContainer);

        int[] attributes = new int[] { R.attr.font_size, R.attr.line_spacing, R.attr.char_spacing, R.attr.screen_padding };
        TypedArray array = theme.obtainStyledAttributes(attributes);

        final int fontSize = array.getDimensionPixelSize(0, 20);
        final float lineSpacing = array.getFloat(1, 1.0f);
        final float charSpacing = array.getFloat(2, 1.0f);
        final int screenPadding = array.getDimensionPixelSize(3, 0);
        array.recycle();

        for( int i = 0; i < textViews.size(); i++ ) {
            textViews.get(i).setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSize);
            textViews.get(i).setLineSpacing(0, lineSpacing);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                textViews.get(i).setLetterSpacing(charSpacing);
            }
        }
        viewContainer.setPadding(screenPadding, 0, screenPadding, 0);
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

    public static void markChange() {
        appliedToMain = false;
        appliedToConvo = false;
    }

    public static boolean shouldApplyToMain() {
        if (appliedToMain)
            return false;
        appliedToMain = true;
        return true;
    }

    public static boolean shouldApplyToConvo() {
        if (appliedToConvo)
            return false;
        appliedToConvo = true;
        return true;
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
    }

    public static void changeLineSpacing(Context context, int delta) {
        /*
        Change line spacing by delta
        */
        SharedPreferences sharedPref = getSharedPreferences(context);
        int MAX_LINE_SPACING = 5;
        int MIN_LINE_SPACING = 0;

        // Calculate new size
        int currentVal = sharedPref.getInt(LINE_SPACING, 0);
        int newVal = currentVal + delta;
        if (newVal > MAX_LINE_SPACING) { newVal = MAX_LINE_SPACING; }
        if (newVal < MIN_LINE_SPACING) { newVal = MIN_LINE_SPACING; }

        // Set preference (Try using commit instead of apply)
        sharedPref.edit().putInt(LINE_SPACING, newVal).apply();
    }

    public static void changeCharSpacing(Context context, int delta) {
        /*
        Change char spacing by delta
        */
        SharedPreferences sharedPref = getSharedPreferences(context);
        int MAX_VAL = 5;
        int MIN_VAL = 0;

        // Calculate new value
        int currentVal = sharedPref.getInt(CHAR_SPACING, 0);
        int newVal = currentVal + delta;
        if (newVal > MAX_VAL) { newVal = MAX_VAL; }
        if (newVal < MIN_VAL) { newVal = MIN_VAL; }

        // Set preference (Try using commit instead of apply)
        sharedPref.edit().putInt(CHAR_SPACING, newVal).apply();
    }

    public static void changeBrightness(Context context, int delta) {
        /*
        Change brightness by delta
        */
        SharedPreferences sharedPref = getSharedPreferences(context);
        int MAX_VAL = 100;
        int MIN_VAL = 20;

        // Calculate new value
        int currentVal = sharedPref.getInt(BRIGHTNESS, 100);
        int newVal = currentVal + (5 * delta);
        if (newVal > MAX_VAL) { newVal = MAX_VAL; }
        if (newVal < MIN_VAL) { newVal = MIN_VAL; }

        // Set preference (Try using commit instead of apply)
        sharedPref.edit().putInt(BRIGHTNESS, newVal).apply();
    }

    public static void changeSpeakerSpeed(Context context, int delta) {
        /*
        Change speaker speed by delta
        */
        SharedPreferences sharedPref = getSharedPreferences(context);
        float MAX_VAL = 5.0f;
        float MIN_VAL = 0.25f;

        // Calculate new value
        float currentVal = sharedPref.getFloat(SPEAKER_SPEED, 1.0f);
        float newVal = currentVal + (0.05f * delta);
        if (newVal > MAX_VAL) { newVal = MAX_VAL; }
        if (newVal < MIN_VAL) { newVal = MIN_VAL; }

        // Set preference (Try using commit instead of apply)
        sharedPref.edit().putFloat(SPEAKER_SPEED, newVal).apply();
    }

    public static void changeScreenPadding(Context context, int delta) {
        /*
        Change speaker speed by delta
        */
        SharedPreferences sharedPref = getSharedPreferences(context);
        int MAX_VAL = 9;
        int MIN_VAL = 0;

        // Calculate new value
        int currentVal = sharedPref.getInt(SCREEN_PADDING, 0);
        int newVal = currentVal + delta;
        if (newVal > MAX_VAL) { newVal = MAX_VAL; }
        if (newVal < MIN_VAL) { newVal = MIN_VAL; }

        // Set preference (Try using commit instead of apply)
        sharedPref.edit().putInt(SCREEN_PADDING, newVal).apply();
    }
}