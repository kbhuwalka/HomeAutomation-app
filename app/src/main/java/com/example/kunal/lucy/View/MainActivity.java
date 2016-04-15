package com.example.kunal.lucy.View;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kunal.lucy.R;


public class MainActivity extends Activity {
    public static final String TAG = MainActivity.class.getSimpleName() + " Class";
    public static final int REQUEST_ENABLE_BT = 15; //Any constant number to ensure that the correct result is obtained

    private BluetoothAdapter mBluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button bluetoothButton = (Button) findViewById(R.id.bluetoothButton);
        TextView message = (TextView) findViewById(R.id.splashMessage);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            message.setText("Your device does not support Bluetooth.");
            bluetoothButton.setVisibility(View.GONE);
        }else{
            if (!mBluetoothAdapter.isEnabled()) {
                //The intent tries to turn on Bluetooth
                bluetoothButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);  //Check the status of Bluetooth request
                    }
                });
            }
            else    //If Bluetooth is already turned on
                goToNextScreen();
        }

    }

    private void goToNextScreen() {
        Intent intent = new Intent(MainActivity.this, RoomsList.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // The activity has become visible (it is now "resumed").
        // Register the BroadcastReceiver for receiving new Bluetooth devices
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);

    }

    @Override
    public void onBackPressed(){
        return;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
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
        switch (id) {
            case R.id.action_settings:
                return true;
            case R.id.action_search:
                scanForDevices();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //If the Activity that returned a result was supposed to turn on Bluetooth
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK)  //Checks if Bluetooth successfully turned ON
                goToNextScreen();
        }

    }

    private void bluetoothNotStarted() {
        Toast.makeText(this, "Bluetooth did not start successfully.", Toast.LENGTH_LONG).show();
        finish();
    }

    private void scanForDevices() {
        if (mBluetoothAdapter.startDiscovery())
            Toast.makeText(this, "Searching for Devices...", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, "Some error occurred in starting Bluetooth device discovery", Toast.LENGTH_LONG).show();
    }

}
