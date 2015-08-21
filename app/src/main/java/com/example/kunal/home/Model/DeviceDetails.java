package com.example.kunal.home.Model;

/**
 * Created by Kunal on 8/20/2015.
 */
public class DeviceDetails {
    private static String[] mAddress = {
            "A0:F4:50:CD:D9:98", //Kunal's Mobile
            "14:30:C6:16:F8:33", //Shubham's Mobile
            "20:14:03:19:90:73"  // The working HC-05
    };

    public static boolean isValidDevice(String deviceAddress){

        for(int i=0; i<mAddress.length;i++){
            if(mAddress[i].equals(deviceAddress))
                return true;
        }

        return false;
    }

}
