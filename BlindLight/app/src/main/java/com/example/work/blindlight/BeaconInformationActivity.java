package com.example.work.blindlight;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;


/**
 * Created by harisa on 14.5.2016.
 */
public class BeaconInformationActivity extends Activity {

    private String beaconName;
    TextView beaconNameText;

    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_information_screen);

        Intent intent = getIntent();
        beaconName = intent.getStringExtra("beaconName");
        beaconNameText = (TextView)findViewById(R.id.beaconNameTextView);
        beaconNameText.setText(beaconName);
    }



}
