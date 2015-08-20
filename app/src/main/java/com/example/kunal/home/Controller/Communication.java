package com.example.kunal.home.Controller;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by Kunal on 8/20/2015.
 */
public class Communication{

    private final BluetoothSocket mBtSocket;
    private final BluetoothDevice mDevice;
    private final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    public Communication(BluetoothDevice device){
        mDevice = device;
        BluetoothSocket temp = null;

        try{
            temp = device.createRfcommSocketToServiceRecord(MY_UUID);
        }catch(IOException e){
            Log.e(TAG, "Unable to get a socket.");
        }

        mBtSocket = temp;

    }

    public void establishConnection() {
        mBluetoothAdapter.cancelDiscovery();

        new Thread(new Runnable() {
            public void run() {
                try{
                    mBtSocket.connect();
                }catch(IOException e){
                    cancel();
                    Log.e(TAG, "Unable to connect to Bluetooth Server.");
                }
            }
        }).start();


    }

    private void cancel() {
        try{
            mBtSocket.close();
        }catch (IOException closeException){
            Log.e(TAG, "Unable to close the Bluetooth Socket");
        }
    }

    public static final String UUID_STRING = "12dbe1c7-202f-4e09-8572-172fb79fb6ef";
    public static final UUID MY_UUID = UUID.fromString(UUID_STRING);
    public static final String SERVICE_NAME = "KS_HomeAutomation";
    public static final String TAG = Communication.class.getSimpleName()+" Class";
}
