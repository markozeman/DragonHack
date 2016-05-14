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
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class MainScreen extends Activity  {

    public static int maxVolume = 10;
    public static int currVolume = maxVolume;
    private LeDeviceListAdapter mLeDeviceListAdapter;
    private BluetoothAdapter mBluetoothAdapter;
    private Handler mHandler;

    private Button button;

    ViewHolder[] beacons;
    int num_devices = 0;

    ViewHolder currentBeacon;
    Context context = this;

    private Button testButton;

    private Button infoButton;


    public void beaconInfo(View view){
        Intent myIntent = new Intent(this, BeaconInformationActivity.class);
        myIntent.putExtra("beaconName", button.getText().toString()); //Optional parameters
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
        }, 1000);

    }


    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        beacons = new ViewHolder[5];

        setContentView(R.layout.activity_main_screen);

        if(23 <= Build.VERSION.SDK_INT) {
            //requestPermissions(["android.permission.ACCESS_FINE_LOCATION"], 1);
            requestPermissions(new String[]{"android.permission.ACCESS_FINE_LOCATION"}, 1);
        }

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
                        sleep(1500);
                        updateBeacons();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        thread.start();

        testButton = (Button)findViewById(R.id.button2);
        //button's reference

        button = (Button)findViewById(R.id.button);

        infoButton = (Button)findViewById(R.id.button3);

        testButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                createNotification(v);
            }

        });


        Button one = (Button) this.findViewById(R.id.button3);
        final MediaPlayer mp = MediaPlayer.create(this, R.raw.test2);
        one.setOnClickListener(new OnClickListener(){

            public void onClick(View v) {
                    mp.start();


            }
        });


        Button two = (Button)this.findViewById(R.id.button4);
        two.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                float log1=(float)(Math.log(maxVolume-currVolume)/Math.log(maxVolume));
                mp.setVolume(1-log1,1-log1);
                currVolume--;
                Log.v("v",currentBeacon.device.getName());
            }
        });


        //setting button's click and implementing the onClick method

        button.setOnClickListener(new OnClickListener() {



            @Override

            public void onClick(View v) {

                //List of items to be show in  alert Dialog are stored in array of strings/char sequences

                final String[] items = {"Living room", "Kitchen", "Bedroom", "Bathroom"};



                AlertDialog.Builder builder = new AlertDialog.Builder(context);



                //set the title for alert dialog

                builder.setTitle("CHOOSE THE LOCATION: ");



                //set items to alert dialog. i.e. our array , which will be shown as list view in alert dialog

                builder.setItems(items, new DialogInterface.OnClickListener() {



                    @Override

                    public void onClick(DialogInterface dialog, int item) {

                        // setting the button text to the selected itenm from the list

                        //button.setText(items[item]);
                        Log.v("v",Integer.toString(item));
                        if (beacons[item].deviceRSSI != 1000) {
                            currentBeacon = beacons[item];
                        } else {
                            int minRSSI = 200;
                            int index = 0;
                            for (int i = 0; i < 5; i++){
                                if (beacons[i].deviceRSSI == 1000) continue;
                                if (beacons[i].deviceRSSI < minRSSI) {
                                    minRSSI = beacons[i].deviceRSSI;
                                    index = i;
                                }
                            }
                            currentBeacon = beacons[index];
                        }



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
                                for( int i= 0; i<5;i++)
                                    beacons[i] = null;
                                switch (device.getName()){
                                    case "FF-145":
                                        num_devices = 0;
                                        break;
                                    case "FF-158":
                                        num_devices = 1;
                                        break;
                                    case "FF-160":
                                        num_devices = 2;
                                }
                                beacons[num_devices] = new ViewHolder();
                                beacons[num_devices].device = device;
                                beacons[num_devices].deviceRSSI = rssi;
                                for( int i= 0; i<5;i++) {
                                    if (beacons[i] == null)
                                        continue;
                                    Log.w("w", beacons[i].device.toString());
                                }
                            }
                        }
                    });
                }
            };

    static class ViewHolder {
        BluetoothDevice device;
        int deviceRSSI;
    }

}
