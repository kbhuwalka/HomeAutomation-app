package com.example.kunal.home.Controller;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.kunal.home.Model.DeviceDetails;
import com.example.kunal.home.Model.Devices;
import com.example.kunal.home.Model.RoomDetailsAdapter;
import com.example.kunal.home.View.RoomDetails;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Kunal on 8/20/2015.
 */
public class Communication {

    private BluetoothSocket mBtSocket;
    private final BluetoothDevice mDevice;
    private final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private NetworkDataController mData;
    private InputStream inputStream;
    private OutputStream outputStream;
    private Method method;
    private boolean isConnecting;

    public Communication(BluetoothDevice device){
        method = null;
        mDevice = device;
        isConnecting = false;
        createSocket();
        mData = new NetworkDataController(mDevice);
    }

    private boolean createSocket() {
        try{
            method = mDevice.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
            mBtSocket = (BluetoothSocket) method.invoke(mDevice, 1);
            return true;
        } catch (InvocationTargetException e) {
            Log.e(TAG+" error: ", "Could not invoke a method");
        } catch (Exception e) {
            Log.e(TAG + " error: ", "Some error occurred in creating a socket.");
        }
        return false;
    }

    public boolean establishConnection() {
        mBluetoothAdapter.cancelDiscovery();

        if(mDevice == null)
            return false;

        pairDevice(mDevice);

        if(mBtSocket==null)
            createSocket();

        if(isConnected())
            return false;


        Log.i(TAG, "Trying to connect to server...");
        try{
            while (!mBtSocket.isConnected())
                mBtSocket.connect();
            Log.i("TAG", "Successfully established connection to device!");
            outputStream = mBtSocket.getOutputStream();
            inputStream = mBtSocket.getInputStream();
            return true;
        }catch(Exception e){
            cancel();
            //Toast.makeText(,"Unable to connect to Bluetooth Server.", Toast.LENGTH_SHORT).show();
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
                if(establishConnection()){
                    sendData(data);
                    receiveData();
                }

                        cancel();
                isConnecting = false;
            }
        }).start();
    }

    public void receiveDataFromDevice(){
        if(isConnecting)
            return;
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(establishConnection())
                    receiveData();

                cancel();
                isConnecting = false;
            }
        }).start();
    }



    public boolean receiveData(){
        byte inputBuffer[] = new byte[mData.INPUT_BUFFER_SIZE];
        int bytesRead = 0;
        boolean receivedValidInput = false;

        try {
            while(!receivedValidInput){
                receivedValidInput = false;
                bytesRead = 0;
                sendData(new byte[]{127});
                Thread.sleep(100);
                while(inputStream.available() > 0 ){
                    bytesRead += inputStream.read(inputBuffer,bytesRead,mData.INPUT_BUFFER_SIZE-bytesRead);
                    mData.setInput(inputBuffer);
                    if(!mData.containsValidMessage()){
                        sendData(new byte[]{127});
                        bytesRead = 0;
                        receivedValidInput = false;
                    }
                    else{
                        mData.updateData();
                        receivedValidInput = true;
                    }
                }
            }


            return true;
        } catch (IOException e) {
            Log.e(TAG,"Some error occurred in retrieving the data");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return false;
    }



    public boolean sendData(byte[] data){
        try {
            OutputStream output = outputStream;
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
            if(mBtSocket != null)
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
