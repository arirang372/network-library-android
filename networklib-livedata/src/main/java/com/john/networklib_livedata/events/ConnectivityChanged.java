package com.john.networklib_livedata.events;

import android.content.Context;
import android.telephony.TelephonyManager;

import com.john.networklib_livedata.ConnectivityStatus;
import com.john.networklib_livedata.MobileNetworkType;
import com.john.networklib_livedata.logger.NetLogger;

/**
 * Created by john on 12/13/2015.
 */
public final class ConnectivityChanged {

	private static final String MESSAGE_FORMAT = "ConnectivityChanged: %s";
	private final ConnectivityStatus connectivityStatus;
	private final Context context;

	public ConnectivityChanged(ConnectivityStatus connectivityStatus, NetLogger netLogger,
			Context context) {
		this.connectivityStatus = connectivityStatus;
		this.context = context;
		String message = String.format(MESSAGE_FORMAT, connectivityStatus.toString());
		netLogger.log(message);
	}

	public ConnectivityStatus getConnectivityStatus() {
		return connectivityStatus;
	}

	public MobileNetworkType getMobileNetworkType() {

		if (connectivityStatus != ConnectivityStatus.MOBILE_CONNECTED) {
			return MobileNetworkType.UNKNOWN;
		}

		TelephonyManager telephonyManager =
				(TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

		switch (telephonyManager.getNetworkType()) {
			case (TelephonyManager.NETWORK_TYPE_LTE):
				return MobileNetworkType.LTE;
			case (TelephonyManager.NETWORK_TYPE_HSPAP):
				return MobileNetworkType.HSPAP;
			case (TelephonyManager.NETWORK_TYPE_EDGE):
				return MobileNetworkType.EDGE;
			case (TelephonyManager.NETWORK_TYPE_GPRS):
				return MobileNetworkType.GPRS;
			default:
				return MobileNetworkType.UNKNOWN;
		}
	}
}