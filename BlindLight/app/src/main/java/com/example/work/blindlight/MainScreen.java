package com.example.work.blindlight;


import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;


public class MainScreen extends Activity {



    public static boolean hasSound = false;
    public static ImageButton sound;
    public static float log1 = (float)0.0;

    public static int maxVolume = 50;
    public static int currVolume = maxVolume;
    private LeDeviceListAdapter mLeDeviceListAdapter;
    private BluetoothAdapter mBluetoothAdapter;
    private Handler mHandler;

    private ImageButton button;

    ViewHolder[] beacons;
    int num_devices = 0;
    boolean[] deviceseen = new boolean[]{false,false,false,false,false};

    final String[] location = {"left", "right", "back", "forward"};

    ViewHolder currentBeacon;
    Context context = this;

    private ImageButton testButton;

    private ImageButton infoButton;

    //TEXT TO SPEECH
    private TextToSpeech t1;
    private EditText write;



    public void beaconInfo(View view){
        Intent myIntent = new Intent(this, BeaconInformationActivity.class);
        //myIntent.putExtra("beaconName", button.getText().toString()); //Optional parameters
        this.startActivity(myIntent);


    }


    @TargetApi(21)
    public void createNotification(View view) {

        //Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Notification n = new Notification.Builder(this)
                .setContentTitle("New mail from " + "test@gmail.com")
                .setContentText("Subject")
                .setSmallIcon(R.drawable.ic_stat_name)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setPriority(Notification.PRIORITY_MAX)
                .build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(context.getApplicationContext(), notification);
        r.play();

        notificationManager.notify(0, n);
        Log.v("v","Test");
    }


    private void resetSeen(){
        for(int i=0;i<5;i++){
            if(!deviceseen[i])
                beacons[i].deviceRSSI = -1000;
            deviceseen[i] = false;
        }
    }
    /*
    * method scans for BLE devices and displays them
    *
    * */
    private void updateBeacons() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                num_devices = 0;
                mBluetoothAdapter.startLeScan(mLeScanCallback);
                //mBluetoothAdapter.stopLeScan(mLeScanCallback);
                invalidateOptionsMenu();
            }
        }, 500);
        Log.w(".", "SIGNALS");
        for(int i=0; i<5;i++)
            Log.w(".", Integer.toString(beacons[i].deviceRSSI));
    }


    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        beacons = new ViewHolder[5];
        for(int i=0;i<5;i++){
            beacons[i] = new ViewHolder();
            beacons[i].device = null;
            beacons[i].deviceRSSI = -1000;
            switch(i){
                case 0:
                    beacons[i].room = "Living room";
                    break;
                case 1:
                    beacons[i].room = "Kitchen";
                    break;
                case 2:
                    beacons[i].room = "Bedroom";
                    break;
                case 3:
                    beacons[i].room = "Bathroom";
                    break;
                case 4:
                    beacons[i].room = "Garage";
                    break;
            }
        }
        currentBeacon = beacons[0];

        setContentView(R.layout.activity_main_screen);

        t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.US);
                }
            }
        });

        if(23 <= Build.VERSION.SDK_INT) {
            //requestPermissions(["android.permission.ACCESS_FINE_LOCATION"], 1);
            requestPermissions(new String[]{"android.permission.ACCESS_FINE_LOCATION"}, 1);
        }

        final MediaPlayer mp = MediaPlayer.create(this, R.raw.test2);
        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "LOL PHONE (no BLE founbd)!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        mHandler = new Handler();
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    while(true) {
                        sleep(500);
                        updateBeacons();
                        if (currentBeacon.deviceRSSI == 1000) {
                            currVolume = 49;
                        } else {
                            int devRSSI = currentBeacon.deviceRSSI;
                            if (devRSSI < -100) {
                                devRSSI = -99;
                            }
                            if (devRSSI > -50){
                                devRSSI = -51;
                            }
                            int volumeLevel = 100 - Math.abs(devRSSI);
                            //Log.v("v",Integer.toString(volumeLevel))
                            log1 = (float)(Math.log(maxVolume-volumeLevel)/Math.log(maxVolume));
                            Log.v("v",Float.toString(log1));
                            if (hasSound) {
                                mp.setVolume(1-log1,1-log1);
                            } else {
                                mp.setVolume(0.0f,0.0f);
                            }

                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


            }
        };

        thread.start();

        Thread threadReset = new Thread() {
            @Override
            public void run() {
                try {
                    while(true) {
                        sleep(5000);
                        resetSeen();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };


        threadReset.start();

        testButton = (ImageButton)findViewById(R.id.imageButton);
        //button's reference

        button = (ImageButton)findViewById(R.id.imageButton2);

        infoButton = (ImageButton)findViewById(R.id.imageButton3);

        Typeface typeFace= Typeface.createFromAsset(getAssets(),"fonts/Roboto-Thin.ttf");
        Typeface typefaceButtons = Typeface.createFromAsset(getAssets(),"fonts/Roboto-Regular.ttf");
        TextView title = (TextView)findViewById(R.id.beaconNameTextView);
        title.setTypeface(typeFace);

        TextView firstButton = (TextView)findViewById(R.id.textView23);
        firstButton.setTypeface(typefaceButtons);

        TextView secondButton = (TextView)findViewById(R.id.textView24);
        secondButton.setTypeface(typefaceButtons);

        TextView thirdButton = (TextView)findViewById(R.id.textView2);
        thirdButton.setTypeface(typefaceButtons);

        TextView fourthButton = (TextView)findViewById(R.id.textView);
        fourthButton.setTypeface(typefaceButtons);

        testButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                createNotification(v);
            }

        });

        infoButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                int selectedIndex = -2;
                int maxRssi = -200;
                for (int i = 0; i < 5; i++) {
                    if (maxRssi < beacons[i].deviceRSSI) {
                        selectedIndex = i;
                        maxRssi = beacons[i].deviceRSSI;
                    }
                }
                String currentRoom = beacons[selectedIndex].room;
                String toSpeak = "You are in the "+currentRoom;
                Toast.makeText(getApplicationContext(), toSpeak,Toast.LENGTH_SHORT).show();
                t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);

            }
        });


        sound = (ImageButton)findViewById(R.id.imageButton4);

        sound.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!hasSound) {
                    sound.setImageResource(R.drawable.volume_up);
                    hasSound = true;
                } else {
                    sound.setImageResource(R.drawable.volume_off);
                    hasSound = false;
                }

            }
        });


        //setting button's click and implementing the onClick method

        button.setOnClickListener(new OnClickListener() {



            @Override

            public void onClick(View v) {

                //List of items to be show in  alert Dialog are stored in array of strings/char sequences

                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                //set the title for alert dialog
                builder.setTitle("CHOOSE THE LOCATION: ");

                //set items to alert dialog. i.e. our array , which will be shown as list view in alert dialog
                String[] items = new String[4];
                for(int i=0;i<4;i++)
                    items[i] = beacons[i].room;
                builder.setItems(items, new DialogInterface.OnClickListener() {



                    @Override

                    public void onClick(DialogInterface dialog, int item) {

                        // setting the button text to the selected itenm from the list
                        if (beacons[item].deviceRSSI != 1000) {
                            currentBeacon = beacons[item];
                        } else {
                            int maxRSSI = -200;
                            int index = 0;
                            for (int i = 0; i < 5; i++){
                                if (beacons[i].deviceRSSI == 1000) continue;
                                if (beacons[i].deviceRSSI > maxRSSI) {
                                    maxRSSI = beacons[i].deviceRSSI;
                                    index = i;
                                }
                            }
                            currentBeacon = beacons[index];
                        }
                        String toSpeak = "Head "+ location[item] + " to get to the " +beacons[item].room;
                        Toast.makeText(getApplicationContext(), toSpeak,Toast.LENGTH_SHORT).show();
                        t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);

                        mp.start();

                    }


                });

                //Creating CANCEL button in alert dialog, to dismiss the dialog box when nothing is selected
                builder
                        .setCancelable(false)
                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {



                            @Override

                            public void onClick(DialogInterface dialog, int id) {
                                //When clicked on CANCEL button the dalog will be dismissed
                                dialog.dismiss();
                            }

                        });



                // Creating alert dialog
                AlertDialog alert = builder.create();

                //Showing alert dialog
                alert.show();



            }

        });

    }

    // Adapter for holding devices found through scanning.
    private class LeDeviceListAdapter extends BaseAdapter {
        private ArrayList<BluetoothDevice> mLeDevices;

        public LeDeviceListAdapter() {
            super();
            mLeDevices = new ArrayList<BluetoothDevice>();
        }

        public void addDevice(BluetoothDevice device) {
            if(!mLeDevices.contains(device)) {
                mLeDevices.add(device);
            }
        }

        public BluetoothDevice getDevice(int position) {
            return mLeDevices.get(position);
        }

        public void clear() {
            mLeDevices.clear();
        }

        @Override
        public int getCount() {
            return mLeDevices.size();
        }

        @Override
        public Object getItem(int i) {
            return mLeDevices.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            /*
            ViewHolder viewHolder;
            // General ListView optimization code.
            if (view == null) {
                viewHolder = new ViewHolder();
                viewHolder.deviceName = (TextView) view.findViewById(R.id.device_address);
                viewHolder.deviceRSSI = (TextView) view.findViewById(R.id.device_name);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            BluetoothDevice device = mLeDevices.get(i);
            viewHolder.deviceName.setText(device.getAddress());
            device.EXTRA_RSSI;
            */
            return null;
        }
    }


    @Override

    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.main, menu);

        return true;

    }
    int counter = 0;
    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

                @Override
                public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //mLeDeviceListAdapter.addDevice(device);
                            //mLeDeviceListAdapter.notifyDataSetChanged();

                            if (device.getName() != null)
                            {
                                num_devices = -1;
                                switch (device.getName()){
                                    case "FF-145":
                                        num_devices = 0;
                                        break;
                                    case "FF-156":
                                        num_devices = 1;
                                        break;
                                    case "FF-158":
                                        num_devices = 2;
                                        break;
                                    case "FF-160":
                                        num_devices = 3;
                                        break;
                                    case "FF-162":
                                        num_devices = 4;
                                        break;

                                }
                                if(num_devices<0)
                                    return; 
                                beacons[num_devices].device = device;
                                beacons[num_devices].deviceRSSI = rssi;
                                deviceseen[num_devices] = true;
                            }
                        }
                    });
                }
            };

    static class ViewHolder {
        BluetoothDevice device;
        int deviceRSSI;
        String room;
    }

}
