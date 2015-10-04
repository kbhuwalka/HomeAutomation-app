package com.example.kunal.home.Controller;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.kunal.home.Model.DeviceDetails;
import com.example.kunal.home.Model.Devices;
import com.example.kunal.home.Model.RoomsListAdapter;

import java.util.ArrayList;

/**
 * Created by Kunal on 10/4/2015.
 */
public class BluetoothController implements Devices{

    private static BluetoothAdapter mBluetoothAdapter;
    public static boolean isBluetoothAdapterAvailable = false;
    private Context mContext;


    public BluetoothController(Context context){
        rooms.add(new DeviceDetails("30:15:01:22:09:34", "Reception"));
        rooms.add(new DeviceDetails("20:14:03:19:90:73", "Waiting Room"));
        rooms.add(new DeviceDetails("20:14:03:19:90:74", "Director's Office"));
        rooms.add(new DeviceDetails("20:14:03:19:90:75", "Office 1"));
        rooms.add(new DeviceDetails("20:14:03:19:90:76", "Office 2"));
        rooms.add(new DeviceDetails("20:14:03:19:90:77", "Office 3"));

        mContext = context;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter != null)
            isBluetoothAdapterAvailable = true;
    }

    public void refreshAll(){
        availableDevices.clear();
        availableBluetoothDevices.clear();

        for(DeviceDetails room : rooms)
            room.isAvailable = false;

        if(!isBluetoothAdapterAvailable)
            return;

        if(mBluetoothAdapter.startDiscovery())
            Toast.makeText(mContext, "Searching for Devices...", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(mContext, "Some error occurred in starting Bluetooth device discovery", Toast.LENGTH_SHORT).show();

    }

    public static void addAvailableDevice(BluetoothDevice newDevice){
        if(availableDevices.contains(newDevice.getAddress()))
            return;

        availableDevices.add(newDevice.getAddress());
        availableBluetoothDevices.add(newDevice);
        for(DeviceDetails room : rooms){
            if(room.getAddress().equals(newDevice.getAddress())) {
                room.isAvailable = true;
                Log.i("New", "Added device to available : "+room.getAddress());
            }
        }
    }

    public static boolean isBluetoothAdapterEnabled(){
        return mBluetoothAdapter.isEnabled();
    }

}
