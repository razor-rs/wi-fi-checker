package com.comunications.razor.razorwi_fi.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;

import com.comunications.razor.razorwi_fi.Config;
import com.comunications.razor.razorwi_fi.services.NetworkUtil;
import com.comunications.razor.razorwi_fi.services.WifiActiveService;

/**
 * Created by ejoe on 12/21/2015.
 */
public class WifiReceiver extends BroadcastReceiver {

    private final static String TAG = "RazorWifi";
    private static boolean firstDisconnect = true;
    private static boolean firstConnect = true;
    private static boolean firstConnectData = true;
    @Override
    public void onReceive(final Context context, final Intent intent) {

        int connectionType = NetworkUtil.getConnectivityStatus(context);
        Intent wifiService;

        switch (connectionType) {

            case NetworkUtil.NETWORK_STATUS_MOBILE:

                if(firstDisconnect) {
                    wifiService = new Intent(context, WifiActiveService.class);
                    wifiService.putExtra(Config.BUNDEL_KEY_CONNECTION_STATE, Config.WIFI_DISCONECTED);
                    context.startService(wifiService);
                    firstDisconnect = false;
                }
                firstConnect = true;

                break;

            case NetworkUtil.NETWORK_STAUS_WIFI:

                if(firstConnect)
                {
                    wifiService = new Intent(context, WifiActiveService.class);
                    wifiService.putExtra(Config.BUNDEL_KEY_CONNECTION_STATE, Config.WIFI_CONNECTED);
                    context.startService(wifiService);
                    firstConnect = false;
                }
                firstDisconnect = true;

                break;


            case NetworkUtil.NETWORK_STATUS_NOT_CONNECTED:

                if(firstDisconnect) {
                    wifiService = new Intent(context, WifiActiveService.class);
                    wifiService.putExtra(Config.BUNDEL_KEY_CONNECTION_STATE, Config.WIFI_DISCONECTED);
                    context.startService(wifiService);
                    firstDisconnect = false;
                }
                firstConnect = true;

                break;
        }
    }
}
