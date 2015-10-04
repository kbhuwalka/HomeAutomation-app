package com.example.kunal.home.Model;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.example.kunal.home.Controller.Communication;
import com.example.kunal.home.R;

/**
 * Created by Kunal on 10/3/2015.
 */
public class RoomDetailsAdapter extends RecyclerView.Adapter<RoomDetailsAdapter.RoomDetailsViewHolder> implements Devices {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private int deviceId;
    DeviceDetails room;
    BluetoothDevice router;
    Communication communication;

    public RoomDetailsAdapter(Context context, int position) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
        deviceId = position;

        room = rooms.get(position);

        if(availableDevices.contains(room.getAddress()))
            router = availableBluetoothDevices.get(availableDevices.indexOf(room.getAddress()));
        else
            router = null;
        communication = new Communication(router);
    }

    @Override
    public RoomDetailsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.room_details_list_item,parent,false);
        RoomDetailsViewHolder viewHolder = new RoomDetailsViewHolder(view);

        return viewHolder;
    }



    @Override
    public void onBindViewHolder(RoomDetailsViewHolder holder, final int position) {
        holder.lightSwitch.setText(switchName[position]);
        holder.duration.setText("15 min");

        holder.lightSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                communication.sendDataToDevice(new byte[]{outputData[position]});
            }
        });
    }

    @Override
    public int getItemCount() {
        return switchName.length;
    }

    public class RoomDetailsViewHolder extends RecyclerView.ViewHolder {

        public TextView duration;
        public Switch lightSwitch;

        public RoomDetailsViewHolder(View itemView) {
            super(itemView);
            duration = (TextView) itemView.findViewById(R.id.switchDuration);
            lightSwitch = (Switch) itemView.findViewById(R.id.lightSwitch);
        }
    }
}
