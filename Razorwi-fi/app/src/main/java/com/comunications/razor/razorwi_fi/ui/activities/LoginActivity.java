package com.comunications.razor.razorwi_fi.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.comunications.razor.razorwi_fi.R;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    @Bind(R.id.etEmail) EditText etEmail;
    @Bind(R.id.etImei) EditText etImei;
    @Bind(R.id.btn_login) Button btnLogin;
    @Bind(R.id.rl_signup) RelativeLayout rlSignup;
    @Bind(R.id.iv_category) SimpleDraweeView ivBck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.green));
        }

//        Uri uri;
//        uri = Uri.parse("res://" + getResources().getDrawable(R.drawable.gradient_bcg));
        
        ImageRequest request = ImageRequestBuilder.newBuilderWithResourceId(R.drawable.grayrazor).setProgressiveRenderingEnabled(true).build();
        DraweeController controller = Fresco.newDraweeControllerBuilder().setImageRequest(request).setOldController(ivBck.getController()).build();
        ivBck.setController(controller);

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

    @OnClick(R.id.btn_login)
    public void onLoginButtonClick()
    {

    }
    @OnClick(R.id.rl_signup)
    public void onSignuClick()
    {

        Toast.makeText(getBaseContext(), "Text", Toast.LENGTH_SHORT).show();
        Intent signupActivity = new Intent(this, SignupActivity.class);
        startActivity(signupActivity);
    }
}
