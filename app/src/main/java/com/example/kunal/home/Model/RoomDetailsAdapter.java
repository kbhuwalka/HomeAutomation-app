package com.example.kunal.home.Model;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.example.kunal.home.Controller.Communication;
import com.example.kunal.home.Controller.NetworkDataController;
import com.example.kunal.home.R;

import java.util.Date;

/**
 * Created by Kunal on 10/3/2015.
 */
public class RoomDetailsAdapter extends RecyclerView.Adapter<RoomDetailsAdapter.RoomDetailsViewHolder> implements Devices {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private int deviceId;
    public static RecyclerView mRecyclerView;
    DeviceDetails room;
    BluetoothDevice router;
    Communication communication;


    public RoomDetailsAdapter(Context context, RecyclerView recyclerView, int position) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
        deviceId = position;
        mRecyclerView = recyclerView;

        room = rooms.get(position);

        if(availableDevices.contains(room.getAddress()))
            router = availableBluetoothDevices.get(availableDevices.indexOf(room.getAddress()));
        else
            router = null;
        communication = new Communication(router);
        communication.receiveDataFromDevice();
    }

    @Override
    public RoomDetailsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.room_details_list_item,parent,false);
        RoomDetailsViewHolder viewHolder = new RoomDetailsViewHolder(view);

        return viewHolder;
    }



    @Override
    public void onBindViewHolder(final RoomDetailsViewHolder holder, final int position) {
        holder.lightSwitch.setText(switchName[position]);
        holder.lightSwitch.setChecked(room.lightStates[position]);

        long lastUpdated = room.lastUpdated[position];
        holder.duration.setText(updateTimeMessage(lastUpdated, room.lightStates[position]));

        holder.lightSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Kunal", "Starting to send");
                communication.sendDataToDevice(NetworkDataController.generateValidOutput(position));
                notifyDataSetChanged();
            }
        });
    }

    private String updateTimeMessage(long lastUpdated, boolean state) {
        String message ="";

        if(lastUpdated==0)
            message = "Never updated";
        else{
            message = "Switched ";
            if(state)
                message+="on ";
            else
                message+="off ";
            long now = new Date().getTime();
            long time = now - lastUpdated;
            time = time/1000;
            if(time == 1)
                message += time + " second ago";
            else if(time < 60)
                message += time + " seconds ago";
            else if (time/60 == 1)
                message += time/60 + " minute ago";
            else if (time/60 >1  && time/60 < 60)
                message += time/60 + " minutes ago";
            else if (time/3600 == 1)
                message += time/3600 + " hour ago";
            else if(time/3600 > 1)
                message += time/3600 + " hours ago";

        }
        return message;
    }

    @Override
    public int getItemCount() {
        return switchName.length;
    }

    public class RoomDetailsViewHolder extends RecyclerView.ViewHolder {

        public TextView duration;
        public Switch lightSwitch;
        public RelativeLayout roomDetailsListItem;

        public RoomDetailsViewHolder(View itemView) {
            super(itemView);
            duration = (TextView) itemView.findViewById(R.id.switchDuration);
            lightSwitch = (Switch) itemView.findViewById(R.id.lightSwitch);
            roomDetailsListItem = (RelativeLayout) itemView.findViewById(R.id.roomDetailsListItem);
        }
    }
}
