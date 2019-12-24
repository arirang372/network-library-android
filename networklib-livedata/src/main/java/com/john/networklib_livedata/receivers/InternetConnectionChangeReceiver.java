package com.john.networklib_livedata.receivers;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.john.networklib_livedata.ConnectivityStatus;
import com.john.networklib_livedata.events.ConnectivityLiveEvent;
import com.john.networklib_livedata.logger.NetLogger;

/**
 * Created by john on 12/13/2015.
 */

public final class InternetConnectionChangeReceiver extends BaseBroadcastReceiver {

	public final static String INTENT =
			"networkevents.intent.action.INTERNET_CONNECTION_STATE_CHANGED";
	public final static String INTENT_EXTRA = "networkevents.intent.extra.CONNECTED_TO_INTERNET";

	public InternetConnectionChangeReceiver(ConnectivityLiveEvent eventWrapper, NetLogger netLogger, Context context) {
		super(eventWrapper, netLogger, context);
	}

	private boolean isConnectedToWifi(Context context) {
		ConnectivityManager connectivityManager =
				(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

		if (networkInfo != null) {
			return networkInfo.getType() == ConnectivityManager.TYPE_WIFI;
		}

		return false;
	}

	private void onPostReceive(boolean connectedToInternet, Context context) {
		ConnectivityStatus connectivityStatus =
				(connectedToInternet) ? ConnectivityStatus.WIFI_CONNECTED_HAS_INTERNET
						: ConnectivityStatus.WIFI_CONNECTED_HAS_NO_INTERNET;

		if (statusNotChanged(connectivityStatus)) {
			return;
		}
		if (context != null && !isConnectedToWifi(context)) {
			return;
		}
		postConnectivityChanged(connectivityStatus);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(INTENT)) {
			boolean connectedToInternet = intent.getBooleanExtra(INTENT_EXTRA, false);
			onPostReceive(connectedToInternet, context);
		}
	}
}
