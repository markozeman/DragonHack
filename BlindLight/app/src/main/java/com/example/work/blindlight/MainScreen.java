package com.example.work.blindlight;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


public class MainScreen extends Activity  {

    private Button button;

    Context context = this;

    private Button testButton;


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

    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_screen);


        testButton = (Button)findViewById(R.id.button2);
        //button's reference

        button = (Button)findViewById(R.id.button);

        testButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                createNotification(v);
            }

        });


        //setting button's click and implementing the onClick method

        button.setOnClickListener(new OnClickListener() {



            @Override

            public void onClick(View v) {

                //List of items to be show in  alert Dialog are stored in array of strings/char sequences

                final String[] items = {"AAAAAA", "BBBBBBB", "CCCCCCC", "DDDDDDDD"};



                AlertDialog.Builder builder = new AlertDialog.Builder(context);



                //set the title for alert dialog

                builder.setTitle("Choose names: ");



                //set items to alert dialog. i.e. our array , which will be shown as list view in alert dialog

                builder.setItems(items, new DialogInterface.OnClickListener() {



                    @Override

                    public void onClick(DialogInterface dialog, int item) {

                        // setting the button text to the selected itenm from the list

                        button.setText(items[item]);

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


    @Override

    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.main, menu);

        return true;

    }



}
