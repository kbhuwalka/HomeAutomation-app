package com.example.kunal.home.View;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.kunal.home.Controller.DiscoveryBroadcastReceiver;
import com.example.kunal.home.R;

import java.util.ArrayList;
import java.util.Set;


public class MainActivity extends ActionBarActivity {

    private static final int REQUEST_ENABLE_BT = 15;

    private BluetoothAdapter mBluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter == null)
            Log.e(TAG,"Device doesn't support bluetooth");
        else
            Log.i(TAG, "Bluetooth available");

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        else{
            bluetoothStarted();
        }
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if( requestCode == REQUEST_ENABLE_BT){
            if( resultCode == Activity.RESULT_OK )
                bluetoothStarted();
            else
                bluetoothNotStarted();
        }

    }

    private void bluetoothNotStarted() {
        Toast.makeText(this, "Bluetooth did not start successfully.",Toast.LENGTH_LONG).show();
        finish();
    }

    private void bluetoothStarted() {
        Toast.makeText(this, "Bluetooth successfully started.",Toast.LENGTH_LONG).show();

        ArrayList<String> mArrayList = new ArrayList<String>();

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        // If there are paired devices
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                // Add the name and address to an array adapter to show in a ListView
                mArrayList.add("\n"+device.getName() + "\t\t" + device.getAddress());
            }
        }

        Log.i("Bluetooth: ", mArrayList.toString());

        if(isDeviceBonded())
            initiateConnection();
        else
            bondWithDevices();
    }

    private void bondWithDevices() {
        if(mBluetoothAdapter.startDiscovery())
            Toast.makeText(this, "Device discovery started", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(this, "Some error occurred in starting Bluetooth device discovery", Toast.LENGTH_LONG).show();

        DiscoveryBroadcastReceiver mReceiver = new DiscoveryBroadcastReceiver();
        // Register the BroadcastReceiver
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy
    }

    private void initiateConnection() {

    }

    private boolean isDeviceBonded() {
        return false;
    }

    public static final String TAG = MainActivity.class.getSimpleName()+" Class";

}
