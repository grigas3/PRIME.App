package com.example.primeapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.primeapp.scheduling.NotifyWorker;

import java.time.Duration;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public  class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Spinner spinner1 = (Spinner) findViewById(R.id.resting_sensor_spinner);
        Spinner spinner2 = (Spinner) findViewById(R.id.pronsup_sensor_spinner);
        Spinner spinner3 = (Spinner) findViewById(R.id.gait_sensor_spinner);
        Spinner spinner4 = (Spinner) findViewById(R.id.hour_spinner);

        Button schedule=(Button)findViewById(R.id.schedule_button);
        Button unschedule=(Button)findViewById(R.id.unschedule_button);
        schedule.setOnClickListener((e)->{

            setTimer();
        });
        Resources res = getResources();
        unschedule.setOnClickListener((e)-> {
                    WorkManager.getInstance(this).cancelAllWork();

                });
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,
                R.array.sensors_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner1.setAdapter(adapter1);




        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.sensors_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        spinner2.setAdapter(adapter2);



        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(this,
                R.array.gait_sensors_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        spinner3.setAdapter(adapter3);


        ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(this,
                R.array.hours_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        spinner4.setAdapter(adapter4);
    }
    public static final String workTag = "notificationWork";



    private void setTimer(){


//store DBEventID to pass it to the PendingIntent and open the appropriate event page on notification click

// we then retrieve it inside the NotifyWorker with:
// final int DBEventID = getInputData().getInt(DBEventIDTag, ERROR_VALUE);

        Spinner spinner4 = (Spinner) findViewById(R.id.hour_spinner);
        String selectedItem=(String)spinner4.getSelectedItem();
        int hour=0;
        if(selectedItem==null)
            return;

        int val=Integer.parseInt(selectedItem.split(":")[0]);

        Calendar calendar = Calendar.getInstance();
        int hour24hrs = calendar.get(Calendar.HOUR_OF_DAY);

        if(val>hour24hrs)
            val=(hour24hrs+24)-val;
        else
            val=hour24hrs-val;

        buildNotificationChannel();
        PeriodicWorkRequest notificationWork = new PeriodicWorkRequest.Builder(NotifyWorker.class, Duration.ofDays(1)).setInitialDelay(60, TimeUnit.SECONDS)
                //.setInitialDelay(val,TimeUnit.HOURS)
                .addTag(workTag)
                .build();


        WorkManager.getInstance(this).enqueue(notificationWork);
    }
    private final static String manasia_notification_channel = "PRIME";

    private void buildNotificationChannel(){

// Create the NotificationChannel, but only on API 26+ because
// the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //define the importance level of the notification
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            //build the actual notification channel, giving it a unique ID and name
            NotificationChannel channel =
                    new NotificationChannel(manasia_notification_channel, manasia_notification_channel, importance);

            //we can optionally add a description for the channel
            String description = "Remember to have a PRIME test today";
            channel.setDescription(description);

            //we can optionally set notification LED colour
            channel.setLightColor(Color.MAGENTA);

            // Register the channel with the system
            NotificationManager notificationManager = (NotificationManager) getApplicationContext().
                    getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }


}