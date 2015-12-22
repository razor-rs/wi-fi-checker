package com.comunications.razor.razorwi_fi.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.comunications.razor.razorwi_fi.R;
import com.parse.ParseUser;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StartActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(ParseUser.getCurrentUser() != null)
        {
            Log.d("ParseUserWiFi", "Parse object is not null");
            Intent wifiInfo = new Intent(this, WiFiInfoActivity.class);
            startActivity(wifiInfo);
            finish();
        }
        else
        {
            Log.d("ParseUserWiFi", "Parse object is null");
            Intent loginActivity = new Intent(this, LoginActivity.class);
            startActivity(loginActivity);
            finish();
        }


    }

}
