package com.example.kunal.home.Model;

import android.bluetooth.BluetoothDevice;

import java.util.ArrayList;

/**
 * Created by Kunal on 10/4/2015.
 */
public interface Devices {

    public static final String CONNECT_DEVICE_POSITION = "POSITION";
    public static  final  String UPDATE = "UPDATE";
    public static  final  String UPDATE_POSITION = "UPDATE_POSITION";
    public static  final  String UPDATE_INTENT_FILTER = "UPDATE_INTENT_FILTER";


    public static ArrayList<DeviceDetails> rooms = new ArrayList<DeviceDetails>();
    public static ArrayList<String> availableDevices = new ArrayList<String>();
    public static ArrayList<BluetoothDevice> availableBluetoothDevices = new ArrayList<BluetoothDevice>();

    public static String[] switchName = {
            "Light 1",
            "Light 2",
            "Light 3",
            "Light 4",
            "Light 5"
    };

    public static byte[] outputData = {
            9,
            10,
            11,
            12,
            13
    };
}
