package com.digipass.android;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import com.digipass.android.singletons.BluetoothScanner;

import java.util.Map;

import no.nordicsemi.android.support.v18.scanner.ScanRecord;
import no.nordicsemi.android.support.v18.scanner.ScanResult;

public class BackgroundService extends Service {

    private BluetoothScanner BTScanner;
    public BluetoothScanner getBTScanner(){
        return BTScanner;
    }

    // CONSTANTS
    private static final int INTERVAL_MS = 30000;
    private int NOTIFICATION_ID = 1;

    // GLOBAL OBJECTS
    private NotificationManager nm;
    private Handler h;
    private Notification.Builder notification;
    private Notification.BigTextStyle notificationStyle = new Notification.BigTextStyle();
    private PendingIntent pendingMainActivityIntent;


    @Override
    public void onCreate() {
        BTScanner = new BluetoothScanner(getApplicationContext());

        // Make sure BLE is available
        if (BTScanner.getState() == BluetoothScanner.STATE_UNAVAILABLE) {
            Toast.makeText(this, "Bluetooth Low Energy is not supported.", Toast.LENGTH_SHORT).show();
            stopSelf();
            return;
        }

        // Initialize variables
        nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Create notification
        pendingMainActivityIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);

        notification = new Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("DigiPass")
                .setContentText("DigiPass is loading...")
                .setContentIntent(pendingMainActivityIntent)
                .setStyle(notificationStyle);

        startForeground(NOTIFICATION_ID, notification.build());

        // Create a new handler. Everything added to it will be queued.
        h = new Handler();

        // Execute scan() now and every DELAY ms.
        BTScanner.scan();
        h.postDelayed(new Runnable() {
            public void run() {
                BTScanner.scan();
                h.postDelayed(this, INTERVAL_MS);
            }
        }, INTERVAL_MS);

        // Add callback
        BTScanner.addListner(new Runnable(){
            public void run() {
                onScannerChange();
            }
        });

        onScannerChange();
    }

    private void onScannerChange(){
        if (BTScanner.getState() == BluetoothScanner.STATE_BT_OFF) {
            notification
                    .setContentText("Bluetooth disabled. Click here to enable.")
                    .setContentIntent(
                            PendingIntent.getActivity(this, 0, new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), 0)
                    )
                    .setNumber(0);
        } else if(BTScanner.getState() == BluetoothScanner.STATE_NO_PERMISSION_LOC) {
            notification
                    .setContentText("We have no permission to scan for Bluetooth devices. Click here to grant permission.")
                    .setContentIntent(
                            pendingMainActivityIntent
                    )
                    .setNumber(0);
        } else {
            // Generate contentText (small text)
            String contentText = "Bluetooth is on, no devices found.";
            if(BTScanner.getDevices().size() == 1){
                ScanRecord scanRecord = BTScanner.getDevices().values().iterator().next().getScanRecord();

                contentText = "Click to connect with " + (scanRecord != null ? scanRecord.getDeviceName() : "the device");
            }
            else if(BTScanner.getDevices().size() > 1){
                contentText = "Click to connect with a Bluetooth device.";
            }

            // Generate bigText (when notification is expanded)
            if(BTScanner.getDevices().size() > 0){
                String bigText = "Devices found:";
                for(Map.Entry<String, ScanResult> scanResult : BTScanner.getDevices().entrySet()) {
                    if(scanResult.getValue().getScanRecord() != null)
                        bigText += "\n- " + scanResult.getValue().getScanRecord().getDeviceName();
                }

                notificationStyle.bigText(bigText);
            }

            // Put all info in the notification
            notification.setContentText(contentText)
                    .setContentIntent(pendingMainActivityIntent)
                    .setNumber(BTScanner.getDevices().size());
        }

        // Update notification
        nm.notify(NOTIFICATION_ID, notification.build());
    }

    @Override
    public void onDestroy() {
        nm.cancel(NOTIFICATION_ID);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // If the service is killed, start it back up
        return START_STICKY;
    }

    // Enable activities to bind to this Service
    public class LocalBinder extends Binder {
        BackgroundService getService() {
            return BackgroundService.this;
        }
    }
    private final IBinder mBinder = new LocalBinder();
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}
