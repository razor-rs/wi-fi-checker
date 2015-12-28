package com.comunications.razor.razorwi_fi.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.comunications.razor.razorwi_fi.Config;
import com.comunications.razor.razorwi_fi.R;
import com.comunications.razor.razorwi_fi.eventbus.EbusWifiChange;
import com.comunications.razor.razorwi_fi.services.WifiActiveService;
import com.comunications.razor.razorwi_fi.storage.PrefManager;
import com.parse.ParseUser;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

public class WiFiInfoActivity extends AppCompatActivity {

    private static final String TAG = "WiFiInfoActivity";

    private boolean isFirstTime;
    private boolean isConnectedToMyWiFi;

    @Bind(R.id.iv_connection_state) ImageView ivConnectionState;
    @Bind(R.id.tv_connection) TextView tvConnection;
    @Bind(R.id.tv_username) TextView tvUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wi_fi_info);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        isFirstTime = PrefManager.GENERAL.getPref().getBoolean(Config.PREF_KEY_IS_FIRST_TIME);
        isConnectedToMyWiFi = isConnectedToMyWiFi();
        tvUsername.setText(ParseUser.getCurrentUser().getString("username"));


        if (isFirstTime) {
            PrefManager.GENERAL.getPref().putBoolean(Config.PREF_KEY_IS_FIRST_TIME, false);

            Intent wifiService = new Intent(this, WifiActiveService.class);
            wifiService.putExtra(Config.BUNDEL_KEY_CONNECTION_STATE, Config.WIFI_CONNECTED);
            startService(wifiService);
            setConnectionImage(isConnectedToMyWiFi);

        } else {
            setConnectionImage(isConnectedToMyWiFi);
        }

    }

    /**
     * Set appropiate connection status
     * @param isConnectedToMyWiFi
     */
    private void setConnectionImage(boolean isConnectedToMyWiFi) {
        if (isConnectedToMyWiFi) {
            ivConnectionState.setImageResource(R.drawable.connected);
            tvConnection.setText(R.string.label_you_are_connected);

        } else {
            ivConnectionState.setImageResource(R.drawable.disconnected);
            tvConnection.setText(R.string.label_not_connected);
        }
    }

    /**
     * Check if user is connected to my wifi
     * @return true or false
     */
    private boolean isConnectedToMyWiFi() {

        final WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        WifiInfo info = wifiManager.getConnectionInfo();
        String mac = info.getMacAddress();
        String ssid = info.getSSID();

        if (ssid.length() > 0) {
            if (TextUtils.equals(ssid.substring(1, ssid.length() - 1), Config.SSID)) {

                return true;

            } else {

                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * This event is used to trigger change of connection status image and text
     *
     * @param wifiEvent
     */
    public void onEventMainThread(EbusWifiChange wifiEvent) {

        setConnectionImage(wifiEvent.isConnectedToRazor());

    }

}
