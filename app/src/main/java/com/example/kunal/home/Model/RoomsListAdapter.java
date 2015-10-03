package com.example.kunal.home.Model;

import android.content.Context;
import android.database.DataSetObserver;
import android.support.v7.widget.RecyclerView;
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
import com.example.kunal.home.View.RoomsList;

import java.util.ArrayList;

/**
 * Created by Kunal on 10/2/2015.
 */
public class RoomsListAdapter extends RecyclerView.Adapter<RoomsListAdapter.RoomViewHolder> {

    private Context mContext;
    private final LayoutInflater mInflater;

    private final String roomNames[] = {
      "Reception",
      "Waiting Room",
      "Director's Office",
      "Office 1",
      "Office 2",
      "Office 3"
    };

    public RoomsListAdapter(Context roomsList) {
        mContext = roomsList;
        mInflater = LayoutInflater.from(mContext);
    }


    @Override
    public RoomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = mInflater.inflate(R.layout.rooms_grid_item, parent, false);
        RoomViewHolder viewHolder = new RoomViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RoomViewHolder roomViewHolder, int position) {
        roomViewHolder.cardTitle.setText(roomNames[position]);
        roomViewHolder.peopleDetails.setText("");
        roomViewHolder.lightDetails.setText("");
    }

    @Override
    public int getItemCount() {
        return roomNames.length;
    }

    public class RoomViewHolder extends RecyclerView.ViewHolder {

        public TextView cardTitle;
        public TextView peopleDetails;
        public TextView lightDetails;

        public RoomViewHolder(View itemView) {
            super(itemView);
            cardTitle = (TextView) itemView.findViewById(R.id.cardTitle);
            peopleDetails = (TextView) itemView.findViewById(R.id.numberOfPeopleText);
            lightDetails = (TextView) itemView.findViewById(R.id.numberOfLightsText);
        }
    }
}
