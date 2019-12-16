package com.john.networklib;

/**
 * Created by john on 12/13/2015.
 */
public interface NetworkBusWrapper {
    void register(Object object);

    void unregister(Object object);

    void post(Object event);
}
