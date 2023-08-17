package com.example.primeapp;

import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.primeapp.databinding.ActivityMainBinding;
import com.example.primeapp.services.DeviceIMURecordingService;
import com.example.primeapp.services.IIMURecordingService;
import com.example.primeapp.services.IMURecordingService;
import com.mbientlab.metawear.android.BtleService;


/**
 * Splash activity
 * First activity with SmartInSole Logo
 */
public class SplashActivity extends AppCompatActivity {
    private static final long SPLASH_DURATION = 2500L;

    private Handler mHandler;

    private Runnable mRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mHandler = new Handler();
        mRunnable = new Runnable() {
            @Override
            public void run() {
                dismissSplash();
            }
        };

        // allow user to click and dismiss the splash screen prematurely
        View rootView = findViewById(android.R.id.content);
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissSplash();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHandler.postDelayed(mRunnable, SPLASH_DURATION);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHandler.removeCallbacks(mRunnable);
    }

    private void dismissSplash() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }


}
