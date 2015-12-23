package com.comunications.razor.razorwi_fi;

import android.app.Application;
import android.preference.PreferenceManager;

import com.comunications.razor.razorwi_fi.storage.PrefManager;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.parse.Parse;
import com.parse.ParseObject;

/**
 * Created by ilijavucetic on 12/22/15.
 */
public class RazorWiFiApp extends Application{

    private static RazorWiFiApp appInstance;

    public static RazorWiFiApp getInstance() {
        return appInstance;
    }

    @Override public void onCreate() {
        super.onCreate();

        appInstance = this;
        Parse.initialize(this, Config.PARSE_APPLICATION_KEY, Config.PARSE_CLIENT_KEY);
        PrefManager.GENERAL.initialize(getBaseContext());
    }
}
