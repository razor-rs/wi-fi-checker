package com.comunications.razor.razorwi_fi.storage;

import android.content.Context;

/**
 * Created by ilijavucetic on 12/14/15.
 */
public enum PrefManager {
    GENERAL;

    private Context mContext;
    private TinyDB tinyDB;

    public void initialize(Context context) {
        mContext = context;
        tinyDB = new TinyDB(mContext);
    }

    public void initialize(Context context, String prefName) {
        mContext = context;
        tinyDB = new TinyDB(mContext, prefName);
    }

    public TinyDB getPref() {
        return tinyDB;
    }

}
