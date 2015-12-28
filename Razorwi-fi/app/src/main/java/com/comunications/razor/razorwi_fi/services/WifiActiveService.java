package com.comunications.razor.razorwi_fi.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import com.comunications.razor.razorwi_fi.eventbus.EbusWifiChange;
import com.comunications.razor.razorwi_fi.storage.PrefManager;
import com.comunications.razor.razorwi_fi.ui.activities.WiFiInfoActivity;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.util.Date;

import de.greenrobot.event.EventBus;

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

        switch (wifiState) {
            case Config.WIFI_CONNECTED:

                checkWifiConnectiosData();

                break;


            case Config.WIFI_DISCONECTED:

                PrefManager.GENERAL.getPref().putBoolean(Config.PREF_KEY_IS_CONNECTED, false);
                EventBus.getDefault().post(new EbusWifiChange(false));


                if(isMyWiFi((PrefManager.GENERAL.getPref().getString(Config.PREF_KEY_CURRENT_SSID))))
                {
                    ParseQuery<ParseObject> query = ParseQuery.getQuery("WifiSession");

                    Log.v(TAG, "Disconnected 1b");

                    query.fromLocalDatastore();

                    query.getInBackground(PrefManager.GENERAL.getPref().getString(Config.PREF_KEY_CURRENT_SESSION_ID), new GetCallback<ParseObject>() {
                        @Override public void done(ParseObject wifiSession, ParseException e) {

                            Log.v(TAG, "Disconnected 2b");
                            if (e == null) {

                                wifiSession.put("endTime", new Date());
                                wifiSession.put("isUpdated", true);
                                Log.v(TAG, "Disconnected put end time 3b");
                                wifiSession.saveEventually(new SaveCallback() {
                                    @Override public void done(ParseException e) {
                                        Log.v(TAG, "Update sessiod after delete is done. 4b");
                                        //PrefManager.GENERAL.getPref().putString(Config.PREF_KEY_CURRENT_SSID, "");
                                        //PrefManager.GENERAL.getPref().putString(Config.PREF_KEY_CURRENT_SESSION_ID, "");
                                    }
                                });
                            } else {
                                Log.v(TAG, "Error while updating: " + e.toString());
                            }
                        }
                    });
                }
                else
                {
                    Log.v(TAG, "Not my wifi after disconecting . Wifi name is " + PrefManager.GENERAL.getPref().getString(Config.PREF_KEY_CURRENT_SSID));
                }


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


                Log.v(TAG, "The SSID & MAC is: " + ssid + " " + mac);
                PrefManager.GENERAL.getPref().putString(Config.PREF_KEY_CURRENT_SSID, ssid);



                if (ssid.length() > 0) {

                    if (isMyWiFi(ssid)) {
                        Log.v(TAG, "The SSID & MAC are my: " + ssid + " " + mac);

                        if (ParseUser.getCurrentUser() != null) {

                                PrefManager.GENERAL.getPref().putBoolean(Config.PREF_KEY_IS_CONNECTED, true);
                                createNotification("Razor Comunications", "Welcome " + ParseUser.getCurrentUser().getUsername(), ssid);

                                final ParseObject sessionObject = new ParseObject("WifiSession");
                                sessionObject.put("userName", ParseUser.getCurrentUser());
                                sessionObject.put("startTime", new Date());
                                sessionObject.put("endTime", new Date());
                                sessionObject.put("email",ParseUser.getCurrentUser().getEmail());
                                sessionObject.put("isUpdated", false);

                                Log.v(TAG, "Session before saveEventually + " );

                                sessionObject.saveEventually(new SaveCallback() {
                                    @Override public void done(ParseException e) {
                                        if (e == null) {
                                            Log.v(TAG, "Session " + sessionObject.getObjectId() + " has been saved in cloud.");
                                            String sessionId = sessionObject.getObjectId();
                                            PrefManager.GENERAL.getPref().putString(Config.PREF_KEY_CURRENT_SESSION_ID, sessionId);

                                            sessionObject.pinInBackground(new SaveCallback() {
                                                @Override public void done(ParseException e) {
                                                    if (e == null) {
                                                        Log.v(TAG, "Session " + sessionObject.getObjectId() + " has been saved in local database.");
                                                    } else {
                                                        Log.v(TAG, "Session " + sessionObject.getObjectId() + " not saved in local database.");
                                                    }
                                                }
                                            });

                                        } else {
                                            Log.v(TAG, "Session not Saved in Cloud + " + e.toString());
                                        }
                                    }
                                });


                            EventBus.getDefault().post(new EbusWifiChange(true));

                        } else {
                            Log.v(TAG, "The SSID & MAC are not my: " + ssid + " " + mac);
                            PrefManager.GENERAL.getPref().putBoolean(Config.PREF_KEY_IS_CONNECTED, false);
                            createNotification("Razor Comunications", "Welcome. Please login. ", ssid);
                            EventBus.getDefault().post(new EbusWifiChange(false));
                        }
                    }
                } else {
                    Log.v(TAG, "The SSID lengt is null");
                    PrefManager.GENERAL.getPref().putBoolean(Config.PREF_KEY_IS_CONNECTED, false);
                    EventBus.getDefault().post(new EbusWifiChange(false));
                }

                stopSelf();
            }
        }, 3000);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private boolean isMyWiFi(String ssid) {
        if (!TextUtils.isEmpty(ssid))
            return TextUtils.equals(ssid.substring(1, ssid.length() - 1), Config.SSID);
        else
            return false;
    }

    /**
     * Creates a notification displaying the SSID & MAC addr
     */
    private void createNotification(String title, String text, String ssid) {
        Notification n = new NotificationCompat.Builder(this)
                .setContentTitle(title)
                .setContentText(text)
                .setStyle(new NotificationCompat.BigTextStyle().bigText("You are connected to " + ssid))
                .setSmallIcon(R.drawable.ic_notification)
                .build();
        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).notify(0, n);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
