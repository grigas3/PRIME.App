package com.example.primeapp;

import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;


import com.example.primeapp.services.DeviceIMURecordingService;
import com.example.primeapp.services.IIMURecordingService;
import com.example.primeapp.services.IMURecordingService;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.os.IBinder;
import android.view.View;

import androidx.core.view.WindowCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.primeapp.databinding.ActivityMainBinding;
import com.mbientlab.metawear.android.BtleService;

import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {
    private final static int REQUEST_ENABLE_BT = 0, SCAN_DEVICE = 1, PERMISSION_REQUEST_BLUETOOTH = 2,SETTINGS=3;
    public final static String EXTRA_BT_DEVICE= "com.mbientlab.tutorial.sensorfusion.IMUAgent.EXTRA_BT_DEVICE";

    public static final String EXTRA_BLE_DEVICE = "com.mbientlab.bletoolbox.examples.MainActivity.EXTRA_BLE_DEVICE";
    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        //BluetoothDevice device = data.getParcelableExtra(MainActivity.EXTRA_BLE_DEVICE);

        getDevice(savedInstanceState);

        initServices();
        /*

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAnchorView(R.id.fab)
                        .setAction("Action", null).show();
            }
        });

         */
    }
    BluetoothDevice device;
    private void getDevice(Bundle savedInstanceState){

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                device= null;
            } else {
                device=extras.getParcelable(EXTRA_BLE_DEVICE);
            }
        } else {
            device= savedInstanceState.getParcelable(EXTRA_BLE_DEVICE);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_scan) {


            Intent bleScanIntent = new Intent(this, ScannerActivity.class);
            startActivityForResult(bleScanIntent, SCAN_DEVICE);
            return true;
        }
        if (id == R.id.action_settings) {


            Intent bleScanIntent = new Intent(this, SettingsActivity.class);
            startActivityForResult(bleScanIntent, SETTINGS);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
    Intent intent;


    private void initServices()
    {

       // agent=new HoloMoticonAgent(getApplicationContext());

        if(device==null)
            return;
        //Binding with sampling services for the two soles
        intent = new Intent(getApplicationContext(), IMURecordingService.class);
        //binding Services
        getApplicationContext().bindService(intent, imuServiceConnection, Context.BIND_AUTO_CREATE);
        getApplicationContext().bindService(new Intent(this, BtleService.class), bteServiceConnection, BIND_AUTO_CREATE);





    }
    private boolean isBound=false;
    BtleService.LocalBinder binder;
    public ServiceConnection imuServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            IMURecordingService.LocalBinder lbinder = (IMURecordingService.LocalBinder) service;
            IMURecordingService mservice = lbinder.getService();
            imuService=mservice;
            if(device!=null)
                mservice.setBtDevice(device);
            if(binder!=null)
                mservice.setBleBinder(binder);
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };


    public ServiceConnection bteServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (BtleService.LocalBinder) service;

       /*     if(imuService!=null)
            {
                imuService.setBleBinder(binder);
            }*/
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };


    private IIMURecordingService imuService;

    public IIMURecordingService getImuService(){

        if(imuService==null)
        {
            //In case the IMU device is not set then use the Mobile phone data

            imuService=new DeviceIMURecordingService(this.getApplicationContext());

        }
        return imuService;
    }


}