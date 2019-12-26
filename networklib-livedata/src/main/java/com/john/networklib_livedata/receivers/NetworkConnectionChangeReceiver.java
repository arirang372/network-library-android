package com.john.networklib_livedata.receivers;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.john.networklib_livedata.ConnectivityStatus;
import com.john.networklib_livedata.events.ConnectivityLiveEvent;
import com.john.networklib_livedata.internet.OnlineChecker;
import com.john.networklib_livedata.logger.NetLogger;

/**
 * Created by john on 12/13/2015.
 */
public final class NetworkConnectionChangeReceiver extends BaseBroadcastReceiver {

	private static final String EVENT_NAME = NetworkConnectionChangeReceiver.class.getSimpleName();
	private boolean internetCheckEnabled = false;
	private final OnlineChecker onlineChecker;

	public NetworkConnectionChangeReceiver(ConnectivityLiveEvent event, NetLogger netLogger, Context context,
			OnlineChecker onlineChecker) {
		super(netLogger, context);
		this.onlineChecker = onlineChecker;
		addEvent(NetworkConnectionChangeReceiver.class.getSimpleName(), event);
	}

	public void enableInternetCheck() {
		this.internetCheckEnabled = true;
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

	private void onPostReceive(final ConnectivityStatus connectivityStatus) {
		if (statusNotChanged(connectivityStatus)) {
			return;
		}

		boolean isConnectedToWifi = connectivityStatus == ConnectivityStatus.WIFI_CONNECTED;

		if (internetCheckEnabled && isConnectedToWifi) {
			onlineChecker.check();
		} else {
			postConnectivityChanged(EVENT_NAME, connectivityStatus);
		}
	}

	@Override
	public void onReceive(final Context context, Intent intent) {
		onPostReceive(getConnectivityStatus(context));
	}
}