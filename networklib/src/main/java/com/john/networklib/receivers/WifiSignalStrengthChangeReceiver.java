package com.john.networklib.receivers;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;

import com.john.networklib.NetworkBusWrapper;
import com.john.networklib.events.WifiSignalStrengthChanged;
import com.john.networklib.networkLogger.NetLogger;

/**
 * Created by john on 12/13/2015.
 */

public final class WifiSignalStrengthChangeReceiver extends BaseBroadcastReceiver {
    private Context context;

    public WifiSignalStrengthChangeReceiver(NetworkBusWrapper networkBusWrapper, NetLogger netLogger, Context context) {
        super(networkBusWrapper, netLogger, context);
        this.context = context;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // We need to start WiFi scan after receiving an Intent
        // in order to get update with fresh data as soon as possible
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        wifiManager.startScan();
        onPostReceive();
    }

    public void onPostReceive() {
        postFromAnyThread(new WifiSignalStrengthChanged(netLogger, context));
    }
}
