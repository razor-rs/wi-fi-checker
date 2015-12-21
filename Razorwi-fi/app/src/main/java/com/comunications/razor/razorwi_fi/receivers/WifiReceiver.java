package com.comunications.razor.razorwi_fi.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.comunications.razor.razorwi_fi.Config;
import com.comunications.razor.razorwi_fi.services.WifiActiveService;

/**
 * Created by ejoe on 12/21/2015.
 */
public class WifiReceiver extends BroadcastReceiver {

    private final static String TAG = "RazorWifi";

    @Override
    public void onReceive(final Context context, final Intent intent) {
        int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, -1);

        if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction()) && WifiManager.WIFI_STATE_ENABLED == wifiState) {

            Log.v(TAG, "Wifi is now enabled");
            Intent wifiService = new Intent(context, WifiActiveService.class);
            wifiService.putExtra(Config.BUNDEL_KEY_CONNECTION_STATE, Config.WIFI_CONNECTED);
            context.startService(wifiService);
        }
        else
        {
            Log.v(TAG, "Wifi is disabled");
            Intent wifiService = new Intent(context, WifiActiveService.class);
            wifiService.putExtra(Config.BUNDEL_KEY_CONNECTION_STATE, Config.WIFI_DISCONECTED);
            context.startService(wifiService);
        }
    }
}
