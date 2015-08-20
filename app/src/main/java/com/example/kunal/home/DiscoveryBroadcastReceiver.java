package com.example.kunal.home;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Kunal on 8/20/2015.
 */
public class DiscoveryBroadcastReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        if(BluetoothDevice.ACTION_FOUND.equals(intent.getAction())){
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        }
        Log.i("Bluetooth: ", "Received something!!!!");
    }
}
