package com.comunications.razor.razorwi_fi.ui.activities;

import android.content.Context;
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
import android.widget.Toast;

import com.comunications.razor.razorwi_fi.R;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignupActivity extends AppCompatActivity {

    @Bind(R.id.etEmail) EditText etEmail;
    @Bind(R.id.etImei) EditText etImei;
    @Bind(R.id.btn_signup) Button bntSignup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.green));
        }

        setImei();
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

    /**
     * Find IMEI of device and set this value etImei
     */
    private void setImei()
    {
        TelephonyManager mngr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        String imei = mngr.getDeviceId();

        etImei.setText(imei);
    }

    @OnClick(R.id.btn_signup)
    public void onSignupClick()
    {
        ParseUser user = new ParseUser();

        user.setUsername(etEmail.getText().toString().toLowerCase().trim());
        user.setEmail(etEmail.getText().toString().toLowerCase().trim());
        user.setPassword("123");
        user.put("displayName", etEmail.getText().toString());

        user.signUpInBackground(new SignUpCallback() {
            @Override public void done(ParseException e) {
                if (e == null) {
                    Log.d("ParseInteresMe", "Succes ");
                } else
                    Log.d("ParseInterestME", "Error " + e.toString());
            }
        });
    }
}
