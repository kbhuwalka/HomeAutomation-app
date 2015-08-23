package com.example.kunal.home.View;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.kunal.home.Controller.Communication;
import com.example.kunal.home.Controller.DiscoveryBroadcastReceiver;
import com.example.kunal.home.R;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {
    public static final String TAG = MainActivity.class.getSimpleName()+" Class";
    public static final int REQUEST_ENABLE_BT = 15;

    protected Button roomButton;
    protected Button onButton;
    protected Button offButtom;

    private ArrayList<BluetoothDevice> availableDevices = new ArrayList<BluetoothDevice>();
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice arduino = null;
    private DiscoveryBroadcastReceiver mReceiver = new DiscoveryBroadcastReceiver();
    private Communication connectedDevice;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        roomButton = (Button) findViewById(R.id.Discover);
        onButton = (Button) findViewById(R.id.onButton);
        offButtom = (Button) findViewById(R.id.offButton);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter == null)
            Log.e(TAG,"Device doesn't support bluetooth");
        else
            Log.i(TAG, "Bluetooth available");

        if (!mBluetoothAdapter.isEnabled()) {
            //The intent tries to turn on Bluetooth
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);  //Check the status of Bluetooth request
        }
        else{
            scanForDevices();
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        // The activity has become visible (it is now "resumed").

        //Register the BroadcastReceiver for a Local broadcast for updated available devices list
        LocalBroadcastManager.getInstance(this).registerReceiver(devicesUpdatedBroadcastReceiver,
                new IntentFilter(DiscoveryBroadcastReceiver.DEVICES_UPDATED));


        // Register the BroadcastReceiver for receiving new Bluetooth devices
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy

    }

    @Override
    protected void onPause(){
        super.onPause();


        unregisterReceiver(mReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(devicesUpdatedBroadcastReceiver);
    }

    @Override
    protected void onStop(){
        super.onStop();
        // The activity is no longer visible (it is now "stopped"

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch(id){
            case R.id.action_settings: return true;
            case R.id.action_search:
                availableDevices.clear();
                updateRoomButtons();
                scanForDevices();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //If the Activity that returned a result was supposed to turn on Bluetooth
        if( requestCode == REQUEST_ENABLE_BT){
            if( resultCode == Activity.RESULT_OK )  //Checks if Bluetooth successfully turned ON
                scanForDevices();   //Start scanning for available devices
            else
                bluetoothNotStarted();
        }

    }

    private void bluetoothNotStarted() {
        Toast.makeText(this, "Bluetooth did not start successfully.", Toast.LENGTH_LONG).show();
        finish();
    }

    private void scanForDevices() {
        mReceiver.clearValidDevices();
        if(mBluetoothAdapter.startDiscovery())
            Toast.makeText(this, "Searching for Devices...", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, "Some error occurred in starting Bluetooth device discovery", Toast.LENGTH_LONG).show();
    }

    private BroadcastReceiver devicesUpdatedBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Extract data included in the Intent
            if(intent.getAction().equals(DiscoveryBroadcastReceiver.DEVICES_UPDATED)){
                //The ArrayList containing all the valid and available devices
                availableDevices = intent.getParcelableArrayListExtra(DiscoveryBroadcastReceiver.VALID_DEVICES);
                Log.i(TAG, "Got a new device!!\n"+availableDevices.toString());
                //Update the Buttons
                updateRoomButtons();
            }
        }
    };

    public void updateRoomButtons() {
        arduino = null;
        toggleSwitches(false);
        roomButton.setTextColor(Color.parseColor("#80ffffff"));
        roomButton.setBackgroundColor(Color.parseColor("#80ffffff"));
        for(BluetoothDevice device : availableDevices){
            if(device.getAddress().equals("20:14:03:19:90:73"))
                arduino = device;
        }
        if(arduino!=null){
            roomButton.setBackgroundColor(Color.parseColor("#673AB7"));
            roomButton.setTextColor(Color.parseColor("#ffffff"));
            connectToDevice(arduino);
            toggleSwitches(true);
        }
    }

    private void connectToDevice(BluetoothDevice device) {
//        if(connectedDevice!=null && connectedDevice.isConnected())
//            return;
       connectedDevice = new Communication(device);
//        connectedDevice.establishConnection();
      }

    private void toggleSwitches(boolean state) {
        if(state==false){
            onButton.setBackgroundColor(Color.parseColor("#70689F38"));
            onButton.setTextColor(Color.parseColor("#70ffffff"));
            offButtom.setBackgroundColor(Color.parseColor("#70D32F2F"));
            offButtom.setTextColor(Color.parseColor("#70ffffff"));
            onButton.setOnClickListener(null);
            offButtom.setOnClickListener(null);
            return;
        }
        onButton.setBackgroundColor(Color.parseColor("#689F38"));
        onButton.setTextColor(Color.parseColor("#ffffff"));
        offButtom.setBackgroundColor(Color.parseColor("#D32F2F"));
        offButtom.setTextColor(Color.parseColor("#ffffff"));


        View.OnClickListener onListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(MainActivity.this, "Button Clicked!", Toast.LENGTH_SHORT).show();
                connectedDevice.sendDataToDevice(new byte[]{11});
            }
        };

        View.OnClickListener offListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connectedDevice.sendDataToDevice(new byte[]{12});
            }
        };

        onButton.setOnClickListener(onListener);
        offButtom.setOnClickListener(offListener);

    }

//    private boolean isDeviceBonded() {
//
//        ArrayList<String> mArrayList = new ArrayList<String>();
//
//        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
//        // If there are paired devices
//        if (pairedDevices.size() > 0) {
//            // Loop through paired devices
//            for (BluetoothDevice device : pairedDevices) {
//                // Add the name and address to an array adapter to show in a ListView
//                mArrayList.add("\n"+device.getName() + "\t\t" + device.getAddress());
//            }
//        }
//
//        Log.i("Bluetooth: ", mArrayList.toString());
//
//        return false;
//    }
}
