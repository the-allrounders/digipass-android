package com.digipass.android;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class MainActivity extends AppCompatActivity {
    BackgroundService backgroundService;
    boolean boundWithService = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Start the background service
        Intent intent = new Intent(this, BackgroundService.class);
        startService(intent);

        // Check if we have permission to access location
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            // If not, ask
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
        }
    }



    @Override
    protected void onStart() {
        super.onStart();

        // Start the background service
        Intent intent = new Intent(this, BackgroundService.class);
        bindService(intent, backgroundServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        backgroundService.getBTScanner().removeListner(onScannerChange);
        // Unbind from the service
        if (boundWithService) {
            unbindService(backgroundServiceConnection);
            boundWithService = false;
        }
    }

    private ServiceConnection backgroundServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            BackgroundService.LocalBinder binder = (BackgroundService.LocalBinder) service;
            backgroundService = binder.getService();
            boundWithService = true;
            backgroundService.getBTScanner().addListner(onScannerChange);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            boundWithService = false;
        }
    };

    private Runnable onScannerChange = new Runnable(){
        public void run() {
            Log.d("MainActivity", "Scanner callback recieved.");
        }
    };
}
