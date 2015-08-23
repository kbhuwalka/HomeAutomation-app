package com.example.kunal.home.Controller;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

/**
 * Created by Kunal on 8/20/2015.
 */
public class Communication {

    private BluetoothSocket mBtSocket;
    private final BluetoothDevice mDevice;
    private final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private Method method;
    private boolean isConnecting;

    public Communication(BluetoothDevice device){
        method = null;
        mDevice = device;
        isConnecting = false;
        createSocket();
    }

    private boolean createSocket() {
        try{
            method = mDevice.getClass().getMethod("createRfcommSocket", new Class[] {int.class});
            mBtSocket = (BluetoothSocket) method.invoke(mDevice, 1);
            return true;
        } catch (InvocationTargetException e) {
            Log.e(TAG+" error: ", "Could not invoke a method");
        } catch (Exception e) {
            Log.e(TAG+" error: ", "Some error occurred in creating a socket.");
        }
        return false;
    }

    public boolean establishConnection() {
        mBluetoothAdapter.cancelDiscovery();

        pairDevice(mDevice);

        if(mBtSocket==null)
            createSocket();

        if(isConnected())
            return false;


        Log.i(TAG, "Trying to connect to server...");
        try{
            mBtSocket.connect();
            Log.i("TAG", "Successfully established connection to device!");
            return true;
        }catch(IOException e){
            cancel();
            Log.e(TAG, "Unable to connect to Bluetooth Server.");
            e.printStackTrace();
        }
        return false;
    }

    public boolean isConnected() {
        return mBtSocket.isConnected();
    }

    public void sendDataToDevice (final byte[] data){
        if(isConnecting)
            return;

        new Thread(new Runnable() {
            @Override
            public void run() {
                isConnecting = true;
                if(establishConnection())
                    if(sendData(data))
                        cancel();
                isConnecting = false;
            }
        }).start();
    }

    public boolean sendData(byte[] data){
        try {
            OutputStream output = mBtSocket.getOutputStream();
            Log.i(TAG, "Output stream opened");
            output.write(data);
            Log.i(TAG, "Data successfully sent!");
            return true;
        } catch (IOException e) {
            Log.e(TAG,"Could not get OutputStream.");
            e.printStackTrace();
        }
        return false;
    }


    private void pairDevice(BluetoothDevice device) {
        try {
            Method method = device.getClass().getMethod("createBond", (Class[]) null);
            method.invoke(device, (Object[]) null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cancel() {
        try{
            mBtSocket.close();
            mBtSocket = null;
        }catch (IOException closeException){
            Log.e(TAG, "Unable to close the Bluetooth Socket");
        }
    }

    public static final String UUID_STRING = "12dbe1c7-202f-4e09-8572-172fb79fb6ef";
    public static final UUID MY_UUID = UUID.fromString(UUID_STRING);
    public static final String SERVICE_NAME = "KS_HomeAutomation";
    public static final String TAG = Communication.class.getSimpleName()+" Class";
}
