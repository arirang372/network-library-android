package com.john.networklib_livedata;

import java.util.Objects;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;

import com.john.networklib_livedata.events.ConnectivityLiveEvent;
import com.john.networklib_livedata.events.GpsLiveEvent;
import com.john.networklib_livedata.internet.OnlineCheckerImpl;
import com.john.networklib_livedata.logger.NetLogger;
import com.john.networklib_livedata.logger.NetworkEventsLogger;
import com.john.networklib_livedata.receivers.GPSStatusChangeReceiver;
import com.john.networklib_livedata.receivers.InternetConnectionChangeReceiver;
import com.john.networklib_livedata.receivers.NetworkConnectionChangeReceiver;

/**
 *
 * @author John Sung, 12/26/2019
 */
public final class NetworkEvents {

	private final Context context;
	private final GpsLiveEvent gpsLiveEvent = new GpsLiveEvent();
	private final GPSStatusChangeReceiver gpsStatusChangeReceiver;
	private final InternetConnectionChangeReceiver internetConnectionChangeReceiver;
	private final ConnectivityLiveEvent internetConnectionChangedEvent = new ConnectivityLiveEvent();
	private boolean isReceiverRegistered;
	private final NetworkConnectionChangeReceiver networkConnectionChangeReceiver;
	//private final WifiSignalStrengthChangeReceiver wifiSignalStrengthChangeReceiver;
	private final ConnectivityLiveEvent networkConnectionChangedEvent = new ConnectivityLiveEvent();
	private boolean wifiAccessPointsScanEnabled = false;

	/**
	 * initializes NetworkEvents object
	 * with NetworkEventsLogger as default netLogger
	 *
	 * @param context Android context
	 */
	public NetworkEvents(Context context) {
		this(context, new NetworkEventsLogger());
	}

	/**
	 * initializes NetworkEvents object
	 *
	 * @param context Android context
	 * @param netLogger message netLogger (NetworkEventsLogger logs messages to LogCat)
	 */
	public NetworkEvents(Context context, NetLogger netLogger) {
		Objects.requireNonNull(context);
		Objects.requireNonNull(netLogger);
		this.context = context;
		this.networkConnectionChangeReceiver =
				new NetworkConnectionChangeReceiver(networkConnectionChangedEvent, netLogger, context, new OnlineCheckerImpl(context));
		this.internetConnectionChangeReceiver =
				new InternetConnectionChangeReceiver(internetConnectionChangedEvent, netLogger, context);
//		this.wifiSignalStrengthChangeReceiver =
//				new WifiSignalStrengthChangeReceiver(networkBusWrapper, netLogger, context);

		this.gpsStatusChangeReceiver = new GPSStatusChangeReceiver(gpsLiveEvent, netLogger, context);

		if (isGPSOn(context))
			this.gpsStatusChangeReceiver.setGPSStatus(GPSStatus.GPS_ON);
		else
			this.gpsStatusChangeReceiver.setGPSStatus(GPSStatus.GPS_OFF);
	}

	public void checkIfGpsStatusHasChanged() {
		if (isGPSOn(context))
			this.gpsStatusChangeReceiver.setGPSStatus(GPSStatus.GPS_ON);
		else
			this.gpsStatusChangeReceiver.setGPSStatus(GPSStatus.GPS_OFF);
	}

	/**
	 * enables internet connection check
	 * when it's not called, WIFI_CONNECTED_HAS_INTERNET
	 * and WIFI_CONNECTED_HAS_NO_INTERNET ConnectivityStatus will never be set
	 * Please, be careful! Internet connection check may contain bugs
	 * that's why it's disabled by default.
	 *
	 * @return NetworkEvents object
	 */
	public NetworkEvents enableInternetCheck() {
		networkConnectionChangeReceiver.enableInternetCheck();
		return this;
	}

	/**
	 * enables wifi access points scan
	 * when it's not called, WifiSignalStrengthChanged event will never occur
	 *
	 * @return NetworkEvents object
	 */
	public NetworkEvents enableWifiScan() {
		this.wifiAccessPointsScanEnabled = true;
		return this;
	}

	public boolean getCurrentGPSStatus() {
		return gpsStatusChangeReceiver.getGPSStatus() == GPSStatus.GPS_ON;
	}

	public GpsLiveEvent getGpsLiveEvent() {
		return this.gpsLiveEvent;
	}

	public ConnectivityLiveEvent getInternetConnectionChangedEvent() {
		return this.internetConnectionChangedEvent;
	}

	public ConnectivityLiveEvent getNetworkConnectionChangedEvent() {
		return this.networkConnectionChangedEvent;
	}

	private boolean isGPSOn(Context context) {
		final LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}

	/**
	 * registers NetworkEvents
	 * should be executed in onCreate() method in activity
	 * or during creating instance of class extending Application
	 */
	public void register() {
		if (isReceiverRegistered)
			return;

		registerNetworkConnectionChangeReceiver();
		registerInternetConnectionChangeReceiver();
		registerGpsStatusReceiver();
		registerGpsStatusChangeReceiver();

		if (wifiAccessPointsScanEnabled) {
			//registerWifiSignalStrengthChangeReceiver();
			// start WiFi scan in order to refresh access point list
			// if this won't be called WifiSignalStrengthChanged may never occur
			WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
			wifiManager.startScan();
		}

		isReceiverRegistered = true;
	}

	private void registerGpsStatusChangeReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(GPSStatusChangeReceiver.INTENT);
		context.registerReceiver(gpsStatusChangeReceiver, filter);
	}

	private void registerGpsStatusReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(GPSStatusChangeReceiver.INTENT);
		filter.addCategory(Intent.CATEGORY_DEFAULT);
		context.registerReceiver(gpsStatusChangeReceiver, filter);
	}

	private void registerInternetConnectionChangeReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(InternetConnectionChangeReceiver.INTENT);
		context.registerReceiver(internetConnectionChangeReceiver, filter);
	}

	private void registerNetworkConnectionChangeReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
		context.registerReceiver(networkConnectionChangeReceiver, filter);
	}

//	private void registerWifiSignalStrengthChangeReceiver() {
//		IntentFilter filter = new IntentFilter(WifiManager.RSSI_CHANGED_ACTION);
//		context.registerReceiver(wifiSignalStrengthChangeReceiver, filter);
//	}

	/**
	 * unregisters NetworkEvents
	 * should be executed in onDestroy() method in activity
	 * or during destroying instance of class extending Application
	 */
	public void unregister() {
		if (!isReceiverRegistered)
			return;
		context.unregisterReceiver(networkConnectionChangeReceiver);
		context.unregisterReceiver(internetConnectionChangeReceiver);
		context.unregisterReceiver(gpsStatusChangeReceiver);
//		if (wifiAccessPointsScanEnabled) {
//			context.unregisterReceiver(wifiSignalStrengthChangeReceiver);
//		}
		isReceiverRegistered = false;
	}
}
