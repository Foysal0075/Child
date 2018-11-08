package com.finalproject.child.Class;

public class DeviceData {
    public static String key;

    public static String getUserId(){
        String user=null;
        if (key!=null){
            String[] item = key.split(" ");
            user = item[1];
        }
        return user;
    }

    public static String getDeviceId(){

        String device = null;
        if (key!=null){
            String[] item = key.split(" ");
            device = item[0];
        }
        return device;
    }


}
