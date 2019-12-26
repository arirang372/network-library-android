package com.john.networklib_livedata.receivers;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Looper;

import com.john.networklib_livedata.ConnectivityStatus;
import com.john.networklib_livedata.NetworkState;
import com.john.networklib_livedata.events.SingleLiveEvent;
import com.john.networklib_livedata.logger.NetLogger;

/**
 *
 * @author John Sung 12/24/2019
 */
public abstract class BaseBroadcastReceiver extends BroadcastReceiver {

	//protected static GPSStatus currentGPSStatus;
	protected static final Map<String, SingleLiveEvent> events = new ConcurrentHashMap<>();
	protected final Context context;
	protected final NetLogger netLogger;

	public BaseBroadcastReceiver(NetLogger logger, Context context) {
		this.netLogger = logger;
		this.context = context;
	}

	public void addEvent(String className, SingleLiveEvent liveEvent) {
		if (liveEvent == null)
			return;
		if (!events.containsKey(liveEvent.getClass()))
			this.events.put(className, liveEvent);
	}

	public SingleLiveEvent getEvent(String className) {
		if (events.containsKey(className))
			return events.get(className);
		return null;
	}

	@Override
	public abstract void onReceive(Context context, Intent intent);

	protected void postConnectivityChanged(String eventName, ConnectivityStatus connectivityStatus) {
		NetworkState.status = connectivityStatus;
		SingleLiveEvent event = getEvent(eventName);
		if (event == null)
			return;
		postFromAnyThread(event, connectivityStatus);
	}

	protected void postFromAnyThread(SingleLiveEvent event, Object object) {
		if (Looper.myLooper() == Looper.getMainLooper()) {
			event.setValue(object);
		} else {
			event.postValue(object);
		}
	}

	protected boolean statusNotChanged(ConnectivityStatus connectivityStatus) {
		return NetworkState.status == connectivityStatus;
	}
}
