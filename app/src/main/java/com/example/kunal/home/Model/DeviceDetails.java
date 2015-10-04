package com.example.kunal.home.Model;

/**
 * Created by Kunal on 8/20/2015.
 */
public class DeviceDetails {

    private String mName;
    private String mAddress;
    private int switchedOnLights;
    private int authorizedPeople;

    public boolean isAvailable;
    public boolean isConnected;

    public DeviceDetails(String address, String name){
        mAddress = address;
        mName = name;
        isAvailable = false;
        isConnected = false;
    }

    public String getAddress(){
        return mAddress;
    }

    public String getName() {
        return mName;
    }

    public int getSwitchedOnLights() {
        return switchedOnLights;
    }

    public int getAuthorizedPeople() {
        return authorizedPeople;
    }
}
