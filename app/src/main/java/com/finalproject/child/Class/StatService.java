package com.finalproject.child.Class;

import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.security.PrivateKey;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class StatService extends Service {

    private String userId;
    private String deviceId;
    Calendar calendar;

    UsageStatsManager usageStatsManager;


    public StatService(Context context) {
        super();
    }

    public StatService() {
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        super.onStartCommand(intent, flags, startId);

        userId = (String)intent.getExtras().get("user");
        deviceId = (String)intent.getExtras().get("device");
        if (!TextUtils.isEmpty(userId) && !TextUtils.isEmpty(deviceId)){
            uploadStat();
        }
        return START_STICKY;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent broadcastIntent = new Intent("com.finalproject.child.ActivityRecognition.RestartUsage");
        sendBroadcast(broadcastIntent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void uploadStat(){

        final List<String> hours = new ArrayList<>();
        for (int i=0;i<24;i++){
            int j =i+1;
            String hour = String.valueOf(j);
            hours.add(hour);
        }

        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                Calendar calendar = Calendar.getInstance();
                int h = calendar.get(Calendar.HOUR_OF_DAY);
                String time = String.valueOf(h);
                if (hours.contains(time)){

                    String today = getDateTime();
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                    reference.keepSynced(true);
                    List<UsageStats> stats = getUsageList(UsageStatsManager.INTERVAL_BEST);
                    if (stats.size()!=0){
                        for (int i=0;i<stats.size();i++){
                            String key = reference.child("Stats").child(userId).child(deviceId).child(today).push().getKey();
                            UsageData usageData = new UsageData(stats.get(i).getPackageName(), stats.get(i).getFirstTimeStamp(), stats.get(i).getLastTimeStamp()
                                    , stats.get(i).getLastTimeUsed(), stats.get(i).getTotalTimeInForeground());
                            reference.child("Stats").child(userId).child(deviceId).child(today).setValue(usageData);
                        }
                    }


                }

            }
        },5,1*60*60, TimeUnit.SECONDS);


    }

    public String getDateTime() {
        calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        return simpleDateFormat.format(calendar.getTime());

    }

    public List<UsageStats> getUsageList(int interval){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -1);

        usageStatsManager = (UsageStatsManager) this.getSystemService(Context.USAGE_STATS_SERVICE);
        List<UsageStats> usageStats = usageStatsManager.queryUsageStats(interval, calendar.getTimeInMillis(), System.currentTimeMillis());
        if (usageStats.size() == 0) {

        }
        return usageStats;
    }
}
