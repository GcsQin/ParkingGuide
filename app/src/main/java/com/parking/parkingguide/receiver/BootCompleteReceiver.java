package com.parking.parkingguide.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.parking.parkingguide.services.WeatherService;

public class BootCompleteReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        //手机启动的时候启动服务
        context.startService(new Intent(context, WeatherService.class));
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
