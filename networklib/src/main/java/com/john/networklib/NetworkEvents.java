package com.john.networklib;

import java.util.Objects;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;

import com.john.networklib.internet.OnlineCheckerImpl;
import com.john.networklib.networkLogger.NetLogger;
import com.john.networklib.networkLogger.NetworkEventsLogger;
import com.john.networklib.receivers.GPSStatusChangeReceiver;
import com.john.networklib.receivers.InternetConnectionChangeReceiver;
import com.john.networklib.receivers.NetworkConnectionChangeReceiver;
import com.john.networklib.receivers.WifiSignalStrengthChangeReceiver;

/**
 * Created by john on 12/13/2015.
 */
public final class NetworkEvents {

	private final Context context;
	private final GPSStatusChangeReceiver gpsStatusChangeReceiver;
	private final InternetConnectionChangeReceiver internetConnectionChangeReceiver;
	private boolean isReceiverRegistered;
	private final NetworkConnectionChangeReceiver networkConnectionChangeReceiver;
	private boolean wifiAccessPointsScanEnabled = false;
	private final WifiSignalStrengthChangeReceiver wifiSignalStrengthChangeReceiver;

	/**
	 * initializes NetworkEvents object
	 * with NetworkEventsLogger as default netLogger
	 *
	 * @param context Android context
	 * @param busWrapper Wrapper for event bus
	 */
	public NetworkEvents(Context context, NetworkBusWrapper busWrapper) {
		this(context, busWrapper, new NetworkEventsLogger());
	}

	/**
	 * initializes NetworkEvents object
	 *
	 * @param context Android context
	 * @param networkBusWrapper Wrapper fo event bus
	 * @param netLogger message netLogger (NetworkEventsLogger logs messages to LogCat)
	 */
	public NetworkEvents(Context context, NetworkBusWrapper networkBusWrapper, NetLogger netLogger) {
		Objects.requireNonNull(context);
		Objects.requireNonNull(networkBusWrapper);
		Objects.requireNonNull(netLogger);
		this.context = context;
		this.networkConnectionChangeReceiver =
				new NetworkConnectionChangeReceiver(networkBusWrapper, netLogger, context, new OnlineCheckerImpl(context));
		this.internetConnectionChangeReceiver =
				new InternetConnectionChangeReceiver(networkBusWrapper, netLogger, context);
		this.wifiSignalStrengthChangeReceiver =
				new WifiSignalStrengthChangeReceiver(networkBusWrapper, netLogger, context);

		this.gpsStatusChangeReceiver =
				new GPSStatusChangeReceiver(networkBusWrapper, netLogger, context);

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

	public GPSStatus getCurrentGPSStatus() {
		return gpsStatusChangeReceiver.getGPSStatus();
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
			registerWifiSignalStrengthChangeReceiver();
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

	private void registerWifiSignalStrengthChangeReceiver() {
		IntentFilter filter = new IntentFilter(WifiManager.RSSI_CHANGED_ACTION);
		context.registerReceiver(wifiSignalStrengthChangeReceiver, filter);
	}

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
		if (wifiAccessPointsScanEnabled) {
			context.unregisterReceiver(wifiSignalStrengthChangeReceiver);
		}
		isReceiverRegistered = false;
	}
}
