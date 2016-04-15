package com.example.kunal.lucy.Controller;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.kunal.lucy.Model.DeviceDetails;
import com.example.kunal.lucy.Model.Devices;

/**
 * Created by Kunal on 8/20/2015.
 */
public class DiscoveryBroadcastReceiver extends BroadcastReceiver implements Devices{

    public static final String DEVICES_UPDATED = "DEVICES_UPDATED";
    public static final String VALID_DEVICES = "VALID_DEVICES";

    protected String deviceAddress;
    protected String deviceName;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("Bluetooth: ", "Found a device!");
        if(BluetoothDevice.ACTION_FOUND.equals(intent.getAction())){
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            deviceAddress = device.getAddress();
            deviceName = device.getName();
            Log.i("Bluetooth Device: ", deviceAddress + "\t" + deviceName);
            if(isValidDevice(deviceAddress)) {
                BluetoothController.addAvailableDevice( device);
                Intent updateItemIntent = new Intent(UPDATE_INTENT_FILTER);
                updateItemIntent.putExtra(UPDATE_POSITION,position(deviceAddress));
                LocalBroadcastManager.getInstance(context).sendBroadcast(updateItemIntent);
                Log.i("Kunal", "Sent update intent");
            }
        }
    }

    private int position(String deviceAddress) {
        for(DeviceDetails room : rooms){
            if(room.getAddress().equals(deviceAddress))
                return rooms.indexOf(room);
        }

        return -1;
    }

    public boolean isValidDevice(String address){
        for(DeviceDetails room : rooms)
            if(room.getAddress().equals(address))
                return true;

        return false;
    }

}
