package com.john.networklib_livedata;

/**
 * Created by John Sung on 12/13/2015.
 */
public enum ConnectivityStatus {
	UNKNOWN("unknown"),
	WIFI_CONNECTED("connected to WiFi"),
	WIFI_CONNECTED_HAS_INTERNET("connected to WiFi (Internet available)"),
	WIFI_CONNECTED_HAS_NO_INTERNET("connected to WiFi (Internet not available)"),
	MOBILE_CONNECTED("connected to mobile network"),
	OFFLINE("offline");

	private final String status;

	ConnectivityStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return status;
	}
}
