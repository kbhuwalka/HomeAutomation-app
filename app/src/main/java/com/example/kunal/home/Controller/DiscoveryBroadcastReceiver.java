package com.example.kunal.home.Controller;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.kunal.home.Model.DeviceDetails;

/**
 * Created by Kunal on 8/20/2015.
 */
public class DiscoveryBroadcastReceiver extends BroadcastReceiver{

    protected String deviceAddress;
    protected String deviceName;
    protected Communication communication;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("Bluetooth: ", "Received something!!!!");
        if(BluetoothDevice.ACTION_FOUND.equals(intent.getAction())){
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            deviceAddress = device.getAddress();
            deviceName = device.getName();
            Log.i("Bluetooth Device: ", deviceAddress + "\t" + deviceName);
            if(DeviceDetails.isValidDevice(deviceAddress)) {
                communication = new Communication(device);
                communication.establishConnection();
            }
        }
    }
}
