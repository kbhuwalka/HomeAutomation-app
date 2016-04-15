package com.example.kunal.lucy.Controller;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
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

        try{
            while (!mBtSocket.isConnected())
                mBtSocket.connect();
            Log.i(TAG, "Successfully established connection to device!");
            outputStream = mBtSocket.getOutputStream();
            inputStream = mBtSocket.getInputStream();
            return true;
        }catch(Exception e){
            cancel();
            e.printStackTrace();
        }
        return false;
    }

    public boolean isConnected() {
        return mBtSocket.isConnected();
    }


    /**
     * Creates a new thread to send data to arduino and receive updated data back.
     * @param data The output that is to be sent to the Arduino
     */
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

    /*
     *Creates a new thread to receive data from Arduino
     */
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
        boolean receivedValidInput = false;

        try {
            while(!receivedValidInput){ // Request for data and read till a valid reply is received

                requestForInput();
                while(inputStream.available() > 0)
                    inputStream.read(inputBuffer);
                mData.setInput(inputBuffer);
                receivedValidInput = mData.containsValidMessage();
                if(!receivedValidInput)
                    requestForInput();
                 else
                    mData.updateData();
                Arrays.fill(inputBuffer, (byte) 0); //Clear the buffer by setting all elements to 0
            }
            return true;
        } catch (IOException e) {
            Log.e(TAG,"Some error occurred in retrieving the data");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return false;
        }
    }

    //Send the request code to Arduino for a reply
    private void requestForInput() {
        sendData(new byte[]{127});
    }

    /*
        Clears out the old data that might be present in the InputStream by
        reading data till the stream is empty
     */
    private void clearInputStream() {
        try{
            while(inputStream.available() > 0)
                inputStream.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
    public static final String SERVICE_NAME = "HomeAutomation";
    public static final String TAG = Communication.class.getSimpleName()+" Class";
}
