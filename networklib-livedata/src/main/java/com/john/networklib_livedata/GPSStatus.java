package com.john.networklib_livedata;

/**
 * Created by John Sung on 4/11/2016.
 */
public enum GPSStatus {
	GPS_ON("connected to GPS"),
	GPS_OFF("disconnected to GPS");

	private final String status;

	GPSStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return status;
	}
}
