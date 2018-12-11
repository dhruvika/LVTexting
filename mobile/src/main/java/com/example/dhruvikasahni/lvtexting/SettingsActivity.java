package com.example.dhruvikasahni.lvtexting;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {
    TextToSpeech t1;
    Button readAloud;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SettingsManager.applySettingsToTheme(this);
        setContentView(R.layout.activity_settings);

        final float speechRate = SettingsManager.getSpeakerSpeed();
        t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.US);
                    t1.setSpeechRate(speechRate);
                }
            }
        });

        readAloud = (Button)findViewById(R.id.readAloud);
        readAloud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    public void run() {
                        if (!t1.isSpeaking()) {
                            readAloud.setBackgroundResource(R.drawable.stop);
                            t1.speak("Settings options: ", TextToSpeech.QUEUE_FLUSH, null);
                            String toSpeak = "Font size, " +
                                    "Line spacing, " +
                                    "Character spacing, " +
                                    "Screen padding, " +
                                    "Brightness, " +
                                    "Speaker speed.";
                            t1.speak(toSpeak, TextToSpeech.QUEUE_ADD, null);
                            while (t1.isSpeaking()) {}
                            readAloud.setBackgroundResource(R.drawable.play);
                        } else{
                            t1.speak("", TextToSpeech.QUEUE_FLUSH, null);
                            t1.stop();
                        }
                    }
                }).start();
            }
        });
    }

    public void onChangePref(View v) {

        // Get preference
        int buttonID = v.getId();
        switch (buttonID) {
            case R.id.font_size_i:
                SettingsManager.changeFontSize(this, 1); break;
            case R.id.font_size_d:
                SettingsManager.changeFontSize(this, -1); break;
            case R.id.line_space_i:
                SettingsManager.changeLineSpacing(this, 1); break;
            case R.id.line_space_d:
                SettingsManager.changeLineSpacing(this, -1); break;
            case R.id.char_space_i:
                SettingsManager.changeCharSpacing(this, 1); break;
            case R.id.char_space_d:
                SettingsManager.changeCharSpacing(this, -1); break;
            case R.id.brightness_i:
                SettingsManager.changeBrightness(this, 1); break;
            case R.id.brightness_d:
                SettingsManager.changeBrightness(this, -1); break;
            case R.id.speaker_speed_i:
                SettingsManager.changeSpeakerSpeed(this, 1); break;
            case R.id.speaker_speed_d:
                SettingsManager.changeSpeakerSpeed(this, -1); break;
            case R.id.screen_padding_i:
                SettingsManager.changeScreenPadding(this, 1); break;
            case R.id.screen_padding_d:
                SettingsManager.changeScreenPadding(this, -1); break;
        }
        SettingsManager.markChange();
        SettingsManager.applySettingsToTheme(this);
        SettingsManager.applyThemeToView(this, (ViewGroup) findViewById(R.id.Settings_Container));
        t1.setSpeechRate(SettingsManager.getSpeakerSpeed());
    }
}

