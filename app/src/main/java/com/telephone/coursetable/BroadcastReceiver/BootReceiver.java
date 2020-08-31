package com.telephone.coursetable.BroadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.telephone.coursetable.FetchService;
import com.telephone.coursetable.MyApp;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED"))
        {
            FetchService.startAction_START_FETCH_DATA(context, MyApp.service_fetch_interval);
        }
    }
}
