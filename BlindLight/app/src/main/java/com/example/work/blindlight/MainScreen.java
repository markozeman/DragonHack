package com.example.work.blindlight;

import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;

import android.app.Activity;

import android.app.AlertDialog;

import android.app.AlertDialog.Builder;

import android.content.Context;

import android.content.DialogInterface;

import android.view.Menu;

import android.view.View;

import android.view.View.OnClickListener;

import android.widget.Button;



public class MainScreen extends Activity  {

    private Button button;

    Context context = this;



    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_screen);



        //button's reference

        button = (Button)findViewById(R.id.button);



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
