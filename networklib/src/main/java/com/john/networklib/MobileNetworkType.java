package com.john.networklib;

/**
 * Created by tstinson on 12/13/2015.
 */
public enum MobileNetworkType {
    UNKNOWN("unknown"),
    LTE("LTE"),
    HSPAP("HSPAP"),
    EDGE("EDGE"),
    GPRS("GPRS");

    private final String type;

    MobileNetworkType(String status) {
        this.type = status;
    }

    @Override
    public String toString() {
        return type;
    }
}
