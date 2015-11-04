package com.example.kunal.home.Controller;

import android.bluetooth.BluetoothDevice;
import android.os.AsyncTask;
import android.util.Log;

import com.example.kunal.home.Model.DeviceDetails;
import com.example.kunal.home.Model.Devices;
import com.example.kunal.home.View.RoomDetails;
import com.example.kunal.home.View.RoomsList;

import java.nio.ByteBuffer;
import java.util.Date;

/**
 * Created by Kunal on 11/1/2015.
 */
public class NetworkDataController {

    public void setInput(byte[] input) {
        this.input = input;
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
        Log.i("Input Output Time", output);

        return buffer.array();
    }

    public boolean containsValidMessage(){
        return containsValidMessage(0);
    }

    public boolean containsValidMessage(int index){
        int start =  indexOf(input, '#',index);
        int end = indexOf(input, '~', start+INPUT_SIZE+1);

        /*
            If message length is smaller than required input size it's not valid
            The required input also contains two markers
         */
        if(start == -1 || end == -1)
            return false;

        if(end-start == INPUT_SIZE+1)
            return true;

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
                if(RoomsList.mAdapter != null)
                    RoomsList.mAdapter.notifyDataSetChanged();
            }
        }.execute();

    }

    private void resetValidatedInput() {
        validatedInput = null;
    }

//    public void extractValidInput(){
//        if(containsValidMessage()){
//            validatedInput = new byte[INPUT_SIZE];
//            int start = indexOf(input, '#')+1;
//            for(int i =0; i < INPUT_SIZE; i++){
//                validatedInput[i] = input[start+i];
//            }
//        }
//    }

    public void extractLatestValidInput(){
        int start = -1;
        int end = 0;
        boolean isLatest = false;
        while(!isLatest){
            start = indexOf(input, '#', end)+1;
            end = indexOf(input, '~', start);
            if(!containsValidMessage(end))
                isLatest = true;
        }
        validatedInput = new byte[INPUT_SIZE];
        for(int i = 0; i < INPUT_SIZE; i++)
            validatedInput[i] = input[start+i];
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
