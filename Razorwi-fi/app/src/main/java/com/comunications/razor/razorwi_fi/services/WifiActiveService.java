package com.comunications.razor.razorwi_fi.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.comunications.razor.razorwi_fi.Config;
import com.comunications.razor.razorwi_fi.R;
import com.comunications.razor.razorwi_fi.storage.PrefManager;

/**
 * Getting the network info and displaying the notification is handled in a service
 * as we need to delay fetching the SSID name. If this is done when the receiver is
 * called, the name isn't yet available and you'll get null.
 * <p/>
 * As the broadcast receiver is flagged for termination as soon as onReceive() completes,
 * there's a chance that it will be killed before the handler has had time to finish. Placing
 * it in a service lets us control the lifetime.
 */
public class WifiActiveService extends Service {

    private final static String TAG = WifiActiveService.class.getSimpleName();
    boolean isConnectedAlready;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        PrefManager.GENERAL.initialize(getBaseContext());
        isConnectedAlready = PrefManager.GENERAL.getPref().getBoolean(Config.PREF_KEY_IS_CONNECTED);


        int wifiState = intent.getExtras().getInt(Config.BUNDEL_KEY_CONNECTION_STATE);

        switch (wifiState)
        {
            case Config.WIFI_CONNECTED:

                checkWifiConnectiosData();

                break;



            case Config.WIFI_DISCONECTED:

                Log.v(TAG, "The wifi is disconected");
                PrefManager.GENERAL.getPref().putBoolean(Config.PREF_KEY_IS_CONNECTED, false);
                stopSelf();
                break;
        }



        return START_NOT_STICKY;
    }

    private void checkWifiConnectiosData() {
        final WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        // Need to wait a bit for the SSID to get picked up; if done immediately all we'll get is null
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                WifiInfo info = wifiManager.getConnectionInfo();
                String mac = info.getMacAddress();
                String ssid = info.getSSID();

                Log.v(TAG, "The SSID name is: " + ssid );

                if(ssid.length() > 0) {
                    if (TextUtils.equals(ssid.substring(1, ssid.length()-1), Config.SSID))
                    {
                        Log.v(TAG, "The SSID & MAC are my: " + ssid + " " + mac);

                        if(isConnectedAlready)
                        {

                        }
                        else {
                            PrefManager.GENERAL.getPref().putBoolean(Config.PREF_KEY_IS_CONNECTED, true);
                            createNotification(ssid, mac);
                        }

                    }
                    else {
                        Log.v(TAG, "The SSID & MAC are not my: " + ssid + " " + mac);
                        PrefManager.GENERAL.getPref().putBoolean(Config.PREF_KEY_IS_CONNECTED, false);
                    }
                }
                else
                {
                    Log.v(TAG, "The SSID lengt is null");
                    PrefManager.GENERAL.getPref().putBoolean(Config.PREF_KEY_IS_CONNECTED, false);
                }

                stopSelf();
            }
        }, 5000);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Creates a notification displaying the SSID & MAC addr
     */
    private void createNotification(String ssid, String mac) {
        Notification n = new NotificationCompat.Builder(this)
                .setContentTitle("Wifi Connection")
                .setContentText("Connected to " + ssid)
                .setStyle(new NotificationCompat.BigTextStyle().bigText("You're connected to " + ssid + " at " + mac))
                .setSmallIcon(R.mipmap.ic_launcher)
                .build();
        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE))
                .notify(0, n);
    }
}
