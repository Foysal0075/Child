package com.finalproject.child.Class;

public class UsageData {

    private String appName;
    private long firstTimeStamp;
    private long lastTimeStamp;
    private long lastTimeUsed;
    private long totalTimeInForeground;

    public UsageData() {
    }

    public UsageData(String appName, long firstTimeStamp, long lastTimeStamp, long lastTimeUsed, long totalTimeInForeground) {
        this.appName = appName;
        this.firstTimeStamp = firstTimeStamp;
        this.lastTimeStamp = lastTimeStamp;
        this.lastTimeUsed = lastTimeUsed;
        this.totalTimeInForeground = totalTimeInForeground;
    }


    public String getAppName() {
        return appName;
    }

    public long getFirstTimeStamp() {
        return firstTimeStamp;
    }

    public long getLastTimeStamp() {
        return lastTimeStamp;
    }

    public long getLastTimeUsed() {
        return lastTimeUsed;
    }

    public long getTotalTimeInForeground() {
        return totalTimeInForeground;
    }
}


