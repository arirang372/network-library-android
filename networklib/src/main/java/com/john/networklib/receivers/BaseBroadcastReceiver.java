package com.john.networklib.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import com.john.networklib.ConnectivityStatus;
import com.john.networklib.GPSStatus;
import com.john.networklib.NetworkBusWrapper;
import com.john.networklib.NetworkState;
import com.john.networklib.events.ConnectivityChanged;
import com.john.networklib.events.GPSStatusChanged;
import com.john.networklib.networkLogger.NetLogger;

/**
 * Created by john on 12/13/2015.
 */

public abstract class BaseBroadcastReceiver extends BroadcastReceiver {
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final NetworkBusWrapper networkBusWrapper;
    private final Context context;
    protected final NetLogger netLogger;
    protected static GPSStatus currentGPSStatus;

    public BaseBroadcastReceiver(NetworkBusWrapper networkBusWrapper, NetLogger netLogger, Context context) {
        this.networkBusWrapper = networkBusWrapper;
        this.netLogger = netLogger;
        this.context = context;
    }

    @Override
    public abstract void onReceive(Context context, Intent intent);

    protected boolean statusNotChanged(ConnectivityStatus connectivityStatus) {
        return NetworkState.status == connectivityStatus;
    }

    protected void postConnectivityChanged(ConnectivityStatus connectivityStatus) {
        NetworkState.status = connectivityStatus;
        postFromAnyThread(new ConnectivityChanged(connectivityStatus, netLogger, context));
    }

    protected void postGpsStatusChanged(GPSStatus status) {
        currentGPSStatus = status;
        postFromAnyThread(new GPSStatusChanged(currentGPSStatus, netLogger));
    }

    protected void postFromAnyThread(final Object event) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            networkBusWrapper.post(event);
        } else {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    networkBusWrapper.post(event);
                }
            });
        }
    }
}
