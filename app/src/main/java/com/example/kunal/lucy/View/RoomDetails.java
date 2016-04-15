package com.example.kunal.lucy.View;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.example.kunal.lucy.Model.Devices;
import com.example.kunal.lucy.Model.RoomDetailsAdapter;
import com.example.kunal.lucy.R;

public class RoomDetails extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    public static RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private int devicePosition;

    public static final int POSITION_ERROR = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_details);

        Intent intent = getIntent();
        devicePosition = intent.getIntExtra(Devices.CONNECT_DEVICE_POSITION, POSITION_ERROR);


        mRecyclerView = (RecyclerView) findViewById(R.id.roomDetailsRecyclerView);

        //Improve performance as the size does not change
        mRecyclerView.setHasFixedSize(true);

        //This activity has a list of switches, hence using Linear Layout
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        //Adapter to properly format content
        mAdapter = new RoomDetailsAdapter(this, mRecyclerView, devicePosition);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_room_details, menu);
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

}
