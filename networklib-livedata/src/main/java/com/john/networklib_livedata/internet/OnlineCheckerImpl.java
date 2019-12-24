package com.john.networklib_livedata.internet;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.john.networklib_livedata.receivers.InternetConnectionChangeReceiver;

/**
 * @author John Sung on 12/13/2015.
 */
public final class OnlineCheckerImpl implements OnlineChecker {

	private Context context;

	public OnlineCheckerImpl(Context context) {
		this.context = context;
	}

	@Override
	public void check() {
		boolean connectedToInternet = isOnline(context);
		Intent intent = new Intent(InternetConnectionChangeReceiver.INTENT);
		intent.putExtra(InternetConnectionChangeReceiver.INTENT_EXTRA, connectedToInternet);
		context.sendBroadcast(intent);
	}

	private boolean isOnline(Context context) {
		ConnectivityManager connectivityManager =
				(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		return networkInfo != null && networkInfo.isConnected();
	}
}

