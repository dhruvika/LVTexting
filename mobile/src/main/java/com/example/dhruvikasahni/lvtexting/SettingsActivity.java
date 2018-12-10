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

        t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.US);
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
                                    "Brightness";
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
        SettingsManager.markChange();
        SettingsManager.applySettingsToTheme(this);
        SettingsManager.applyThemeToView(this, (ViewGroup) findViewById(R.id.Settings_Container));
    }
}

