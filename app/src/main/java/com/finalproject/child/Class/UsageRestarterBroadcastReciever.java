package com.finalproject.child.Class;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class UsageRestarterBroadcastReciever extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intent1 = new Intent(context,StatService.class);
        intent1.putExtra("user",DeviceData.getUserId());
        intent1.putExtra("device",DeviceData.getDeviceId());
        context.startService(intent1);
    }
}
