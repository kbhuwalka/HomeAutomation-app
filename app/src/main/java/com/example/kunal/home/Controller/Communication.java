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
public class Communication{

    private final BluetoothSocket mBtSocket;
    private final BluetoothDevice mDevice;
    private final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private Method method;

    public Communication(BluetoothDevice device){
        method = null;
        mDevice = device;
        BluetoothSocket temp = null;

        try{
            method = mDevice.getClass().getMethod("createRfcommSocket", new Class[] {int.class});
            temp = (BluetoothSocket) method.invoke(mDevice, 1);
        } catch (InvocationTargetException e) {
            Log.e(TAG+" error: ", "Could not invoke a method");
        } catch (Exception e) {
           Log.e(TAG+" error: ", "Some error occured in creating a socket.");
        }

        mBtSocket = temp;

    }

    public void establishConnection() {
        mBluetoothAdapter.cancelDiscovery();

        pairDevice(mDevice);

        new Thread(new Runnable() {
            public void run() {
                try{
                    mBtSocket.connect();
                    Log.i("TAG", "Successfully established connection to device!");
                }catch(IOException e){
                    cancel();
                    Log.e(TAG, "Unable to connect to Bluetooth Server.");
                    e.printStackTrace();
                }
            }
        }).start();
    }


    private void pairDevice(BluetoothDevice device) {
        try {
            Method method = device.getClass().getMethod("createBond", (Class[]) null);
            method.invoke(device, (Object[]) null);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
