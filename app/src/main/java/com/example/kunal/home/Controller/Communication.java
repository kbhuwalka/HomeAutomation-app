package com.example.kunal.home.Controller;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by Kunal on 8/20/2015.
 */
public class Communication extends Thread {

    private final BluetoothServerSocket mBtServerSocket;
    private static BluetoothAdapter mBluetoothAdapter;

    public Communication(){
        BluetoothServerSocket temp = null;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        try{
            temp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(SERVICE_NAME, MY_UUID);
        }catch (IOException e){
            Log.e(TAG, "Unable to create a Socket");
        }
        mBtServerSocket = temp;
    }

    public void run(){
        BluetoothSocket socket = null;

        //Keep Listenting till a Socket is returned or an exception is thrown
        while(true){
            try {
                socket = mBtServerSocket.accept();
            } catch (IOException e) {
                Log.e(TAG, "An error occurred while listening for inoming connections");
                break;
            }

            if(socket!=null){
                manageConnection(socket);
                cancel();
                break;
            }

        }
    }

    private void manageConnection(BluetoothSocket socket) {

    }

    /** Will cancel the listening socket, and cause the thread to finish */
    public void cancel(){
        try {
            mBtServerSocket.close();
        } catch (IOException e) {
            Log.e(TAG, "Unable to close Socket Connection");
        }
    }


    public static final String UUID_STRING = "12dbe1c7-202f-4e09-8572-172fb79fb6ef";
    public static final UUID MY_UUID = UUID.fromString(UUID_STRING);
    public static final String SERVICE_NAME = "KS_HomeAutomation";
    public static final String TAG = Communication.class.getSimpleName()+" Class";
}
