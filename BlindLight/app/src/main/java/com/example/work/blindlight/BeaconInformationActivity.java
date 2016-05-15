package com.example.work.blindlight;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;
import java.util.Map;


/**
 * Created by harisa on 14.5.2016.
 */
public class BeaconInformationActivity extends Activity {

    private String beaconName;
    private ImageView weather;
    private String temp;
    private String light;
    private String humid;
    private ImageButton speakInfo;
    TextView beaconNameText;
    TextView tempTextView;
    TextView lightTextView;
    TextView humidityTextView;
    private TextToSpeech t1;

    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_information_screen);

        Intent intent = getIntent();
        beaconName = intent.getStringExtra("beaconName");

        t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.US);
                }
            }
        });
        speakInfo = (ImageButton) this.findViewById(R.id.speakButton);
        tempTextView = (TextView)findViewById(R.id.tempTextView);
        lightTextView = (TextView)findViewById(R.id.lightTextView);
        humidityTextView = (TextView)findViewById(R.id.humidityTextView);
        beaconNameText = (TextView)findViewById(R.id.beaconNameTextView);

        weather = (ImageView) findViewById(R.id.imageView);


        beaconNameText.setText(beaconName);
        if(beaconName.equalsIgnoreCase("Living room")){
            weather.setImageResource(R.drawable.sunny);
            temp = "20° C";
            light = "100 Lux";
            humid = "55%";

        }
        else if(beaconName.equalsIgnoreCase("Kitchen")){
            weather.setImageResource(R.drawable.rain);
            temp = "10° C";
            light = "50 Lux";
            humid = "85%";
        }
        else{
            weather.setImageResource(R.drawable.night);
            temp = "0° C";
            light = "30 Lux";
            humid = "60%";
        }

        tempTextView.setText(temp);
        lightTextView.setText(light);
        humidityTextView.setText(humid);

        speakInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String toSpeak = "The temperature is "+temp+", The ligth level is "+light+", the humidity is "+humid;
                t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);

            }
        });



    }



}
