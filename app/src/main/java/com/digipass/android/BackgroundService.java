package com.digipass.android;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import java.util.Random;

public class BackgroundService extends Service {

    // CONSTANTS
    private int NOTIFICATION_ID = 1;
    private int INTERVAL_MS = 5000;

    // INITIALIZED OBJECTS
    private Random random = new Random();
    private NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

    // GLOBAL OBJECTS
    private Handler h;
    private Notification notification;


    @Override
    public void onCreate() {

        // Make sure BLE is available
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "Bluetooth Low Energy is not supported.", Toast.LENGTH_SHORT).show();
            stopSelf();
            return;
        }

        // Create notification
        Intent mainActivityIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingMainActivityIntent = PendingIntent.getActivity(this, 0, mainActivityIntent, 0);

        notification = new Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("DigiPass")
                .setContentText("No bluetooth devices found.")
                .setContentIntent(pendingMainActivityIntent)
                .build();

        startForeground(NOTIFICATION_ID, notification);

        // Execute scan() now and every DELAY ms.
        h = new Handler();
        h.postDelayed(new Runnable() {
            public void run() {
                scan();
                h.postDelayed(this, INTERVAL_MS);
            }
        }, INTERVAL_MS);
        scan();
    }

    public void scan() {
        notification.number = random.nextInt(100);
        nm.notify(NOTIFICATION_ID, notification);
    }

    @Override
    public void onDestroy() {
        nm.cancel(NOTIFICATION_ID);
    }


    // Necessary functions for Android, but not used by us.

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
