package com.example.kunal.home.Model;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.kunal.home.R;
import com.example.kunal.home.View.RoomDetails;
import com.example.kunal.home.View.RoomsList;

import java.util.ArrayList;

/**
 * Created by Kunal on 10/2/2015.
 */
public class RoomsListAdapter extends RecyclerView.Adapter<RoomsListAdapter.RoomViewHolder> implements Devices{

    private Context mContext;
    private final LayoutInflater mInflater;
    private static RecyclerView mRecyclerView;


    public RoomsListAdapter(Context roomsList, RecyclerView recyclerView) {
        mContext = roomsList;
        mInflater = LayoutInflater.from(mContext);
        mRecyclerView = recyclerView;
    }


    @Override
    public RoomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = mInflater.inflate(R.layout.rooms_grid_item, parent, false);
        RoomViewHolder viewHolder = new RoomViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RoomViewHolder roomViewHolder, final int position) {
        formatItem(roomViewHolder,position);
    }

    private void formatItem(RoomViewHolder roomViewHolder, final int position) {
        DeviceDetails room = rooms.get(position);
        roomViewHolder.cardTitle.setText(room.getName());

        if(!room.isAvailable){
            roomViewHolder.peopleDetails.setText("Room details are currently unavailable.");
            roomViewHolder.peopleDetails.setPadding(dp(16), dp(0), dp(16), dp(24));

            roomViewHolder.lightDetails.setVisibility(View.GONE);
            roomViewHolder.moreDetails.setVisibility(View.GONE);

            //roomViewHolder.cardContainer.setVisibility(View.GONE);

            return;
        }

        //roomViewHolder.cardContainer.setVisibility(View.VISIBLE);
        roomViewHolder.lightDetails.setVisibility(View.VISIBLE);

        String peopleDetails = mContext.getResources().getString(R.string.authorized_people_in_room);
        peopleDetails =String.format(peopleDetails, room.getAuthorizedPeople());

        String lights = mContext.getResources().getString(R.string.switched_on_lights);
        lights = String.format(lights, room.getSwitchedOnLights());

        roomViewHolder.peopleDetails.setText(peopleDetails);
        roomViewHolder.lightDetails.setText(lights);


        roomViewHolder.cardContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, RoomDetails.class);
                intent.putExtra(CONNECT_DEVICE_POSITION, position);
                mContext.startActivity(intent);
            }
        });
    }

    public  void updateView(int index){
        if(mRecyclerView==null)
            return;
        View itemView = mRecyclerView.getChildAt(index);
        RoomViewHolder holder = new RoomViewHolder(itemView);
        formatItem(holder,index);
    }

    @Override
    public int getItemCount() {
        return Devices.rooms.size();
    }

    public static class RoomViewHolder extends RecyclerView.ViewHolder {

        public TextView cardTitle;
        public TextView peopleDetails;
        public TextView lightDetails;
        public Button moreDetails;
        public RelativeLayout cardContainer;

        public RoomViewHolder(View itemView) {
            super(itemView);
            cardTitle = (TextView) itemView.findViewById(R.id.cardTitle);
            peopleDetails = (TextView) itemView.findViewById(R.id.numberOfPeopleText);
            lightDetails = (TextView) itemView.findViewById(R.id.numberOfLightsText);
            moreDetails = (Button) itemView.findViewById(R.id.moreDetails);
            cardContainer = (RelativeLayout) itemView.findViewById(R.id.cardContainer);
        }
    }



    private int dp(int px){
        float scale = mContext.getResources().getDisplayMetrics().density;
        int dpAsPixels = (int) (px*scale + 0.5f);
        return dpAsPixels;
    }

}
