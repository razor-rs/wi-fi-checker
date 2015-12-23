package com.comunications.razor.razorwi_fi.eventbus;

/**
 * Created by ilijavucetic on 12/23/15.
 */
public class EbusWifiChange {

    private boolean isConnectedToRazor;

    public EbusWifiChange(boolean isConnectedToRazor) {
        this.isConnectedToRazor = isConnectedToRazor;
    }

    public boolean isConnectedToRazor() {
        return isConnectedToRazor;
    }
}
