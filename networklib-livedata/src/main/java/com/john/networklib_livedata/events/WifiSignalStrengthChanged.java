package com.john.networklib_livedata.events;

import java.util.List;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import com.john.networklib_livedata.logger.NetLogger;

/**
 * Created by John Sung on 12/13/2015.
 */
public final class WifiSignalStrengthChanged {

	private static final String MESSAGE = "WifiSignalStrengthChanged";
	private Context context;

	public WifiSignalStrengthChanged(NetLogger netLogger, Context context) {
		this.context = context;
		netLogger.log(MESSAGE);
	}

	public List<ScanResult> getWifiScanResults() {
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		return wifiManager.getScanResults();
	}
}