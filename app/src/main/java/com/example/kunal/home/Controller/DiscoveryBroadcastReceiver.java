package com.example.kunal.home.Controller;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.kunal.home.Model.DeviceDetails;
import com.example.kunal.home.View.MainActivity;

import java.util.ArrayList;

/**
 * Created by Kunal on 8/20/2015.
 */
public class DiscoveryBroadcastReceiver extends BroadcastReceiver{

    public static final String DEVICES_UPDATED = "DEVICES_UPDATED";
    public static final String VALID_DEVICES = "VALID_DEVICES";

    protected String deviceAddress;
    protected String deviceName;
    protected Communication communication;

    private ArrayList<BluetoothDevice> validDevices = new ArrayList<BluetoothDevice>();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("Bluetooth: ", "Found a device!");
        if(BluetoothDevice.ACTION_FOUND.equals(intent.getAction())){
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            deviceAddress = device.getAddress();
            deviceName = device.getName();
            Log.i("Bluetooth Device: ", deviceAddress + "\t" + deviceName);
            if(DeviceDetails.isValidDevice(deviceAddress)) {
               // communication = new Communication(device);
               // communication.establishConnection();
                addDeviceToList(device);
            }
        }
    }

    private void addDeviceToList(BluetoothDevice device) {

        for(BluetoothDevice validDevice: validDevices){
            if(validDevice.getAddress().equals(device.getAddress()))
                return;
        }
        validDevices.add(device);
        sendUpdateBroadcast();
    }

    private void sendUpdateBroadcast() {
        Intent intent = new Intent();
        intent.setAction(DEVICES_UPDATED);
        intent.putParcelableArrayListExtra(VALID_DEVICES, validDevices);
        LocalBroadcastManager.getInstance(null).sendBroadcast(intent);
    }

    public ArrayList<BluetoothDevice> getBluetoothDevices(){
        return validDevices;
    }

}
