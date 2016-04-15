package com.example.kunal.lucy.Controller;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.kunal.lucy.Model.DeviceDetails;
import com.example.kunal.lucy.Model.Devices;

/**
 * Created by Kunal on 10/4/2015.
 */
public class BluetoothController implements Devices{

    private static BluetoothAdapter mBluetoothAdapter;
    public static boolean isBluetoothAdapterAvailable = false;
    private Context mContext;


    public BluetoothController(Context context){
        rooms.add(new DeviceDetails("20:15:05:08:70:26", "My Room"));
        rooms.add(new DeviceDetails("20:14:03:19:90:73", "Waiting Room"));
        rooms.add(new DeviceDetails("20:15:05:05:73:24", "Secretary's Office"));
        rooms.add(new DeviceDetails("30:15:01:22:09:34", "Office 1"));
        rooms.add(new DeviceDetails("20:15:05:05:73:97", "Office 2"));
        rooms.add(new DeviceDetails("20:15:05:05:69:93", "Office 3"));

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
