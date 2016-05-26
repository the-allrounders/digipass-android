package com.digipass.android.singetons;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat;
import no.nordicsemi.android.support.v18.scanner.ScanCallback;
import no.nordicsemi.android.support.v18.scanner.ScanResult;

public class BluetoothScanner extends ContextWrapper {

    // CONSTANTS
    private static final int SCAN_PERIOD_MS = 2000;

    // STATES
    public static final int STATE_UNAVAILABLE = 0;
    public static final int STATE_AVAILABLE = 1;
    public static final int STATE_BT_OFF = 2;
    public static final int STATE_NO_PERMISSION_BT = 3;
    public static final int STATE_NO_PERMISSION_LOC = 4;
    public static final int STATE_SCANNING = 5;

    private int STATE = -1;
    public int getState() {
        detectState();
        return STATE;
    }

    // VARIABLES

    private BluetoothLeScannerCompat scanner = BluetoothLeScannerCompat.getScanner();
    private Handler handler = new Handler();

    public BluetoothScanner(Context context) {
        super(context);
        detectState();
    }

    /**
     * Returns all currently active devices.
     */
    private ConcurrentHashMap<String, ScanResult> devices = new ConcurrentHashMap<>();
    public ConcurrentHashMap<String, ScanResult> getDevices() {
        // TODO: Remove old devices
        return devices;
    }

    /**
     * Detects and sets the state of the scanner.
     */
    private void detectState(){
        int prevState = STATE;

        BluetoothAdapter bluetoothAdapter = ((BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            scanner.stopScan(scanCallback);
            STATE = STATE_UNAVAILABLE;
        }
        else if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            STATE = STATE_NO_PERMISSION_LOC;
        }
        else if(bluetoothAdapter == null || !bluetoothAdapter.isEnabled()){
            scanner.stopScan(scanCallback);
            STATE = STATE_BT_OFF;
        }
        else{
            STATE = STATE_AVAILABLE;
        }

        if(prevState != STATE) {
            Log.d("BluetoothScanner", "State changed from " + String.valueOf(prevState) + " to " + String.valueOf(STATE));
            callListners();
        }
    }


    /**
     * Starts scanning for Bluetooth devices.
     * Also stops scanning after SCAN_PERIOD_MS.
     */
    public void scan(){
        detectState();
        if(STATE == STATE_AVAILABLE){
            STATE = STATE_SCANNING;
            Log.d("BluetoothScanner", "Start scanning..");
            scanner.startScan(scanCallback);
            handler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    scanner.stopScan(scanCallback);
                    Log.d("BluetoothScanner", "Scanning stopped.");
                }
            }, SCAN_PERIOD_MS);
        }
    }


    /**
     * This callback is called as soon as a Bluetooth LE device sends a pulse.
     */
    private ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            Log.d("BluetoothScanner", "Device found: " + result.getDevice().toString());

            boolean isNew = !devices.containsKey(result.getDevice().toString());

            devices.put(result.getDevice().toString(), result);

            if(isNew) callListners();
        }
    };

    /**
     * Holds a list of Runnables, that are called everytime callListners is run.
     * Also has an adder and remover.
     */
    private ArrayList<Runnable> listners = new ArrayList<>();

    /**
     * Adds a listner to the scanner.
     * @param runnable Is called everty time the state or the device list changes.
     */
    public void addListner(Runnable runnable){
        listners.add(runnable);
    }

    /**
     * Removes a listner from the scanner.
     * @param runnable Was added before with addListner
     */
    public void removeListner(Runnable runnable){
        listners.add(runnable);
    }

    /**
     * Calls all registred listners.
     */
    private void callListners(){
        Log.d("BluetoothScanner", "Calling all listners..");
        for(Runnable listner: listners){
            listner.run();
        }
    }
}
