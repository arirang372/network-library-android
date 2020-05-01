package com.john.networklib.receivers;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;

import com.john.networklib.GPSStatus;
import com.john.networklib.NetworkBusWrapper;
import com.john.networklib.networkLogger.NetLogger;

/**
 * Created by john on 4/11/2016.
 */
public class GPSStatusChangeReceiver extends BaseBroadcastReceiver {
    public final static String INTENT = "android.location.PROVIDERS_CHANGED";
    public final static String INTENT_EXTRA = "networkevents.intent.extra.CONNECTED_TO_GPS";

    public GPSStatusChangeReceiver(NetworkBusWrapper wrapper, NetLogger netLogger, Context context) {
        super(wrapper, netLogger, context);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (isGPSOn(context)) {
            currentGPSStatus = GPSStatus.GPS_ON;
        } else {
            currentGPSStatus = GPSStatus.GPS_OFF;
        }

        postGpsStatusChanged(currentGPSStatus);
    }

    public void setGPSStatus(GPSStatus status) {
        this.currentGPSStatus = status;
    }

    public GPSStatus getGPSStatus() {
        return this.currentGPSStatus;
    }

    private boolean isGPSOn(Context context) {
        final LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }
}
