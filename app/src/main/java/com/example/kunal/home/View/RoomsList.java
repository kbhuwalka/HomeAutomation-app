package com.example.kunal.home.View;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.example.kunal.home.Model.RoomsListAdapter;
import com.example.kunal.home.R;

public class RoomsList extends AppCompatActivity {

    private RecyclerView roomsList;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public static final int NUMBER_OF_COLUMNS = 1;
    public static int HEIGHT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rooms_list);

        roomsList = (RecyclerView) findViewById(R.id.roomsList);
        //Since the contents do not change the size of the layout, this improves performance
        roomsList.setHasFixedSize(true);


        //Using a GridLayoutManager
        mLayoutManager = new GridLayoutManager(this, NUMBER_OF_COLUMNS);
        roomsList.setLayoutManager(mLayoutManager);

        //Adding an Adapter to the View
        mAdapter = new RoomsListAdapter(this);
        roomsList.setAdapter(mAdapter);

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

        return super.onOptionsItemSelected(item);
    }
}
