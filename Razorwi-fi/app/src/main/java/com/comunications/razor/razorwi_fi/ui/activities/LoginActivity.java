package com.comunications.razor.razorwi_fi.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.comunications.razor.razorwi_fi.Config;
import com.comunications.razor.razorwi_fi.R;
import com.comunications.razor.razorwi_fi.storage.PrefManager;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    private final static String TAG = LoginActivity.class.getSimpleName();

    @Bind(R.id.etEmail) EditText etEmail;
    @Bind(R.id.btn_login) Button btnLogin;
    @Bind(R.id.rl_signup) RelativeLayout rlSignup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean isConnectedToMyWiFi() {

        final WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        boolean isMyWiFi;

        WifiInfo info = wifiManager.getConnectionInfo();
        String mac = info.getMacAddress();
        String ssid = info.getSSID();

        Log.v(TAG, "The SSID name is: " + ssid);

        if (ssid.length() > 0) {
            if (TextUtils.equals(ssid.substring(1, ssid.length() - 1), Config.SSID)) {
                Log.v(TAG, "The SSID & MAC are my: " + ssid + " " + mac);

                return true;

            } else {
                Log.v(TAG, "The SSID & MAC are not my: " + ssid + " " + mac);
                return false;
            }
        } else {
            Log.v(TAG, "The SSID lengt is null");
            return false;
        }
    }

    @OnClick(R.id.btn_login)
    public void onLoginButtonClick() {

        if(isConnectedToMyWiFi()) {
            ParseUser user = new ParseUser();

            user.setUsername(etEmail.getText().toString().toLowerCase().trim());
            user.setEmail(etEmail.getText().toString().toLowerCase().trim());
            user.setPassword("123");
            user.put("displayName", etEmail.getText().toString());

            ParseUser.logInInBackground(etEmail.getText().toString().trim(), "123", new LogInCallback() {
                @Override public void done(ParseUser user, ParseException e) {

                    if (e == null) {
                        PrefManager.GENERAL.getPref().putBoolean(Config.PREF_KEY_IS_FIRST_TIME, true);
                        Intent wifiInfo = new Intent(LoginActivity.this, WiFiInfoActivity.class);
                        startActivity(wifiInfo);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finish();
                    } else {
                        Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else
        {
            Toast.makeText(getBaseContext(), "Please connect to RazorGuest Wi-fi", Toast.LENGTH_SHORT).show();
        }

    }

    @OnClick(R.id.rl_signup)
    public void onSignuClick() {

        Toast.makeText(getBaseContext(), "Text", Toast.LENGTH_SHORT).show();
        Intent signupActivity = new Intent(this, SignupActivity.class);
        startActivity(signupActivity);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}
