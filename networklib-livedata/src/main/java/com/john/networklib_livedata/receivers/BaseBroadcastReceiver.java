package com.john.networklib_livedata.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Looper;

import com.john.networklib_livedata.ConnectivityStatus;
import com.john.networklib_livedata.GPSStatus;
import com.john.networklib_livedata.NetworkState;
import com.john.networklib_livedata.events.ConnectivityChanged;
import com.john.networklib_livedata.events.SingleLiveEvent;
import com.john.networklib_livedata.logger.NetLogger;

/**
 *
 * @author John Sung 12/24/2019
 */
public abstract class BaseBroadcastReceiver extends BroadcastReceiver {

	protected static GPSStatus currentGPSStatus;
	private final Context context;
	private final SingleLiveEvent eventWrapper;
	protected final NetLogger netLogger;

	public BaseBroadcastReceiver(SingleLiveEvent eventWrapper, NetLogger logger, Context context) {
		this.eventWrapper = eventWrapper;
		this.netLogger = logger;
		this.context = context;
	}

	@Override
	public abstract void onReceive(Context context, Intent intent);

	protected void postConnectivityChanged(ConnectivityStatus connectivityStatus) {
		NetworkState.status = connectivityStatus;
		postFromAnyThread(new ConnectivityChanged(connectivityStatus, netLogger, context));
	}

	protected void postFromAnyThread(final Object event) {
		if (Looper.myLooper() == Looper.getMainLooper()) {
			eventWrapper.setValue(event);
		} else {
			eventWrapper.postValue(event);
		}
	}

	protected boolean statusNotChanged(ConnectivityStatus connectivityStatus) {
		return NetworkState.status == connectivityStatus;
	}
}
