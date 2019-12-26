# network-library-android

This is the network library for Android written in Java.

This library can be used for any reactive features upon the situation where we lose or gain the internet connection or GPS on the device.

There are two libraries on this repo. 
1. networklib is the one that EventBus triggers the event when internet connection or GPS status has changed
2. networklib-livedata is the one that LiveData triggers the event when internet connection or GPS status has changed

## The open sourced libraries used on this project
* [EventBus]    https://github.com/greenrobot/EventBus
