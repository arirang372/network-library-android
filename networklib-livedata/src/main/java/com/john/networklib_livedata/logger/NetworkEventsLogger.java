package com.john.networklib_livedata.logger;

import android.util.Log;

/**
 * Created by John Sung on 12/13/2015.
 */
public final class NetworkEventsLogger implements NetLogger {

	private final static String TAG = "NetworkEvents";

	@Override
	public void log(String message) {
		Log.d(TAG, message);
	}
}
