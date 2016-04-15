
package com.example.kunal.lucy.Controller;

import android.bluetooth.BluetoothDevice;
import android.os.AsyncTask;
import android.util.Log;

import com.example.kunal.lucy.Model.DeviceDetails;
import com.example.kunal.lucy.Model.Devices;
import com.example.kunal.lucy.View.RoomDetails;
import com.example.kunal.lucy.View.RoomsList;

import java.nio.ByteBuffer;
import java.util.Date;

/**
 * Created by Kunal on 11/1/2015.
 */
public class NetworkDataController {

    public void setInput(byte[] input) {
        this.input = input;
        String buffer = "";
        for(int i = 0; i< input.length; i++)
            buffer+= input[i] + " ";
        Log.d("NetworkDataController", "Input Buffer: " + buffer);
    }

    public static byte[] generateValidOutput(int index){
        ByteBuffer buffer = ByteBuffer.allocate(OUTPUT_BUFFER_SIZE);
        byte pin = Devices.pin[index];
        long updateTime = new Date().getTime();
        buffer.put(pin).putLong(updateTime);

        String output = "";
        byte[] b = buffer.array();
        for(int i=0; i<OUTPUT_BUFFER_SIZE;i++)
            output+=b[i]+" ";

        return buffer.array();
    }

    public boolean containsValidMessage(){
        return containsValidMessage(0);
    }

    public boolean containsValidMessage(int index){
        int start =  lastIndexOf(input, (byte) '#');
        int end = lastIndexOf(input, (byte) '~');

        /*
            If message length is smaller than required input size it's not valid
            The required input also contains two markers
         */
        if(start == -1 || end == -1)
            return false;

        if(start > end)
            return false;

        if(end-start == INPUT_SIZE+1) {
            String op = "";
            for(int i = start; i < end; i++)
                op+= input[i] + " ";
            Log.i("NetworkDataController", "ContainsValidMessage: " + op);
            return true;
        }

        return false;
    }

    private int indexOf(byte[] array, int value){
        return indexOf(array, value, 0);
    }

    private int indexOf(byte[] array, int value, int index){
        if(array!=null && index < array.length)
            for(int i = index; i<array.length; i++)
                if(array[i]==value)
                    return i;

        return -1;
    }

    private int lastIndexOf(byte[] array, byte value){
        int lastIndex = -1;
        for(int index = array.length -1; index >= 0; index--){
            if(array[index]==value)
                return index;
        }
        return lastIndex;
    }

    public void updateData() {
        extractLatestValidInput();
        if(validatedInput == null)
            return;

        ByteBuffer data = ByteBuffer.allocateDirect(INPUT_SIZE);
        data.put(validatedInput);
        resetValidatedInput();
        DeviceDetails room = null;
        for(DeviceDetails r : Devices.rooms)
            if(r.getAddress().equals(mDevice.getAddress()))
                room = r;
        if(room == null) return;

        data.position(0);

        for(int i = 0; i< room.lightStates.length; i++)
            room.lightStates[i] = (data.get()!=0);
        for(int i = 0 ; i< room.lastUpdated.length; i++)
            room.lastUpdated[i] = data.getLong();

        new AsyncTask<Void,Void,Void>(){

            @Override
            protected Void doInBackground(Void... params) {
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                if(RoomDetails.mAdapter!=null)
                    RoomDetails.mAdapter.notifyDataSetChanged();
            }
        }.execute();

    }

    private void resetValidatedInput() {
        validatedInput = null;
    }

    public void extractLatestValidInput(){
        int start = lastIndexOf(input, (byte) '#');

        validatedInput = new byte[INPUT_SIZE];
        String inp = "";
        for(int i = 0; i < INPUT_SIZE; i++) {
            validatedInput[i] = input[start + i];
            inp += validatedInput[i]+ " ";
        }
        Log.i("NetworkDataController", inp);
    }

    public NetworkDataController(BluetoothDevice device){
        mDevice = device;
        input = new byte[INPUT_BUFFER_SIZE];
    }

    private byte[] input;
    private byte[] validatedInput;
    private BluetoothDevice mDevice;

    public static byte OUTPUT_BUFFER_SIZE = 9;
    public static int INPUT_BUFFER_SIZE = 1024;
    public static byte INPUT_SIZE = 54;

}
