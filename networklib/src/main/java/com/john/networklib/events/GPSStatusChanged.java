package com.john.networklib.events;

import com.john.networklib.GPSStatus;
import com.john.networklib.networkLogger.NetLogger;


/**
 * Created by john on 4/11/2016.
 */
public final class GPSStatusChanged {
    private static final String MESSAGE_FORMAT = "GPSStatusChanged: %s";
    private final GPSStatus gpsStatus;

    public GPSStatusChanged(GPSStatus gpsStatus, NetLogger netLogger) {
        this.gpsStatus = gpsStatus;
        String message = String.format(MESSAGE_FORMAT, gpsStatus.toString());
        netLogger.log(message);
    }

    public GPSStatus getGPSStatus() {
        return gpsStatus;
    }
}
