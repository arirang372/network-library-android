package com.john.networklib_livedata.receivers;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.widget.Toast;

import com.john.networklib_livedata.GPSStatus;
import com.john.networklib_livedata.events.GpsLiveEvent;
import com.john.networklib_livedata.logger.NetLogger;

/**
 * Created by john on 4/11/2016.
 */
public class GPSStatusChangeReceiver extends BaseBroadcastReceiver {

	private static final String EVENT_NAME = GPSStatusChangeReceiver.class.getSimpleName();
	public final static String INTENT = "android.location.PROVIDERS_CHANGED";
	public final static String INTENT_EXTRA = "networkevents.intent.extra.CONNECTED_TO_GPS";
	private final GpsLiveEvent event;
	private GPSStatus gpsStatus = GPSStatus.GPS_OFF;

	public GPSStatusChangeReceiver(GpsLiveEvent event, NetLogger netLogger, Context context) {
		super(netLogger, context);
		this.event = event;
		addEvent(EVENT_NAME, event);
	}

	public GPSStatus getGPSStatus() {
		return this.gpsStatus;
	}

	private boolean isGPSOn(Context context) {
		final LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		Toast.makeText(context, "GPS Status has changed ...", Toast.LENGTH_LONG).show();
		processGpsEvent();
	}

	public void processGpsEvent() {
		boolean isGpsOn = isGPSOn(context);
		if (isGpsOn) {
			this.gpsStatus = GPSStatus.GPS_ON;
		} else {
			this.gpsStatus = GPSStatus.GPS_OFF;
		}
		postFromAnyThread(event, isGpsOn);
	}

	public void setGPSStatus(GPSStatus status) {
		this.gpsStatus = status;
	}
}
