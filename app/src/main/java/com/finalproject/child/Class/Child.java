package com.finalproject.child.Class;

public class Child {
    private String childName;
    private String deviceName;
    private String date;
    private String age;
    private String childKey;


    public Child() {

    }

    public Child(String childName, String deviceName, String date, String age, String childKey) {
        this.childName = childName;
        this.deviceName = deviceName;
        this.date = date;
        this.age = age;
        this.childKey = childKey;
    }

    public String getChildName() {
        return childName;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public String getDate() {
        return date;
    }

    public String getAge() {
        return age;
    }

    public String getChildkey() {
        return childKey;
    }

}
