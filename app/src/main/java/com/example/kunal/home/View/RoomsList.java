package com.example.kunal.home.View;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.kunal.home.Controller.BluetoothController;
import com.example.kunal.home.Controller.DiscoveryBroadcastReceiver;
import com.example.kunal.home.Model.DeviceDetails;
import com.example.kunal.home.Model.Devices;
import com.example.kunal.home.Model.RoomsListAdapter;
import com.example.kunal.home.R;

public class RoomsList extends AppCompatActivity {

    private RecyclerView roomsList;
    private RoomsListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private SwipeRefreshLayout swipeContainer;

    private DiscoveryBroadcastReceiver mReceiver = new DiscoveryBroadcastReceiver();
    private BluetoothController bluetoothController;

    public static final int NUMBER_OF_COLUMNS = 1;
    public static int HEIGHT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rooms_list);

        addPullToRefresh();

        bluetoothController = new BluetoothController(this);

        roomsList = (RecyclerView) findViewById(R.id.roomsList);
        //Since the contents do not change the size of the layout, this improves performance
        roomsList.setHasFixedSize(true);


        //Using a GridLayoutManager
        mLayoutManager = new GridLayoutManager(this, NUMBER_OF_COLUMNS);
        roomsList.setLayoutManager(mLayoutManager);

        //Adding an Adapter to the View
        mAdapter = new RoomsListAdapter(this, roomsList);
        roomsList.setAdapter(mAdapter);

        LocalBroadcastManager.getInstance(this).registerReceiver(roomsUpdateReceiver,
                new IntentFilter(Devices.UPDATE_INTENT_FILTER));
    }

    private void addPullToRefresh() {
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);

        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Code to refresh the list goes here
                // swipeContainer.setRefreshing(false) once refreshing is done
                bluetoothController.refreshAll();
                mAdapter.removeAll();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // The activity has become visible (it is now "resumed").

        // Register the BroadcastReceiver for receiving new Bluetooth devices
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy
        bluetoothController.refreshAll();

        //Make sure bluetooth is still on
        if (!bluetoothController.isBluetoothAdapterEnabled())
            goToSplash();
    }

    private void goToSplash() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(roomsUpdateReceiver);
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_rooms_list, menu);
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
        if (id == R.id.action_search) {
            bluetoothController.refreshAll();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private BroadcastReceiver roomsUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int position = intent.getIntExtra(Devices.UPDATE_POSITION, 100);
            if (position < 0 && position>=Devices.rooms.size())
                return;

            DeviceDetails device = Devices.rooms.get(position);
            int index = mAdapter.getItemCount();

            for(DeviceDetails room : mAdapter.mDataSet)
                if(room.getAddress().equals(device.getAddress()))
                    return;

            mAdapter.add(index,device);

            swipeContainer.setRefreshing(false);
        }
    };

}
