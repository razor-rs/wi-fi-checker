package com.comunications.razor.razorwi_fi.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.comunications.razor.razorwi_fi.Config;
import com.comunications.razor.razorwi_fi.R;
import com.comunications.razor.razorwi_fi.storage.PrefManager;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignupActivity extends AppCompatActivity implements View.OnKeyListener {

    private final static String TAG = LoginActivity.class.getSimpleName();

    @Bind(R.id.etEmail) EditText etEmail;
    @Bind(R.id.btn_signup) Button bntSignup;
    @Bind(R.id.rl_container) RelativeLayout rlContainer;
    @Bind(R.id.rl_loading) RelativeLayout rlLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);
        etEmail.setOnKeyListener(this);
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


    private void setLoading(boolean visible)
    {
        if(visible)
        {
            rlContainer.setVisibility(View.GONE);
            rlLoader.setVisibility(View.VISIBLE);
        }
        else
        {
            rlContainer.setVisibility(View.VISIBLE);
            rlLoader.setVisibility(View.GONE);
        }
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

    @OnClick(R.id.btn_signup)
    public void onSignupClick()
    {
        signUpParse();
    }

    private void signUpParse() {
        if(isConnectedToMyWiFi()) {
            setLoading(true);
            ParseUser user = new ParseUser();

            user.setUsername(etEmail.getText().toString().toLowerCase().trim());
            user.setEmail(etEmail.getText().toString().toLowerCase().trim());
            user.setPassword("123");
            user.put("displayName", etEmail.getText().toString());

            user.signUpInBackground(new SignUpCallback() {
                @Override public void done(ParseException e) {
                    if (e == null) {
                        PrefManager.GENERAL.getPref().putBoolean(Config.PREF_KEY_IS_FIRST_TIME, true);
                        Intent wifiInfo = new Intent(SignupActivity.this, WiFiInfoActivity.class);
                        startActivity(wifiInfo);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finish();
                    } else
                        Log.d("ParseInterestME", e.getLocalizedMessage());
                    setLoading(false);
                }
            });

        }
        else
        {
            Toast.makeText(getBaseContext(), "Please connect to RazorGuest Wi-fi", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        switch (v.getId()) {
            case R.id.etEmail:
                if ((keyCode == KeyEvent.KEYCODE_ENTER)) {
                    signUpParse();
                    hideKeyboardImplicit(getBaseContext(),etEmail);
                }
                break;
        }
        return false;
    }

    public static void hideKeyboardImplicit(Context context,EditText editText){
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }
}
