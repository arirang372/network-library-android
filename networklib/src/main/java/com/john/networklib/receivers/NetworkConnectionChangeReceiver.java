package com.john.networklib.receivers;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.john.networklib.ConnectivityStatus;
import com.john.networklib.NetworkBusWrapper;
import com.john.networklib.internet.OnlineChecker;
import com.john.networklib.networkLogger.NetLogger;

/**
 * Created by john on 12/13/2015.
 */

public final class NetworkConnectionChangeReceiver extends BaseBroadcastReceiver {
    private final OnlineChecker onlineChecker;
    private boolean internetCheckEnabled = false;

    public NetworkConnectionChangeReceiver(NetworkBusWrapper networkBusWrapper, NetLogger netLogger, Context context,
                                           OnlineChecker onlineChecker) {
        super(networkBusWrapper, netLogger, context);
        this.onlineChecker = onlineChecker;
    }

    public void enableInternetCheck() {
        this.internetCheckEnabled = true;
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        onPostReceive(getConnectivityStatus(context));
    }

    public void onPostReceive(final ConnectivityStatus connectivityStatus) {
        if (statusNotChanged(connectivityStatus)) {
            return;
        }

        boolean isConnectedToWifi = connectivityStatus == ConnectivityStatus.WIFI_CONNECTED;

        if (internetCheckEnabled && isConnectedToWifi) {
            onlineChecker.check();
        } else {
            postConnectivityChanged(connectivityStatus);
        }
    }

    private ConnectivityStatus getConnectivityStatus(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                return ConnectivityStatus.WIFI_CONNECTED;
            } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                return ConnectivityStatus.MOBILE_CONNECTED;
            }
        }

        return ConnectivityStatus.OFFLINE;
    }
}