package com.john.networklibrary;

import android.app.Application;
import android.content.Context;

import com.john.networklib_livedata.events.SnackbarMessageEvent;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.databinding.ObservableField;
import androidx.lifecycle.AndroidViewModel;

public class NetworkViewModel extends AndroidViewModel {

	public final ObservableField<String> gpsStatusText = new ObservableField<>();
	public final ObservableField<String> internetStatusText = new ObservableField<>();
	private final Context mContext;
	private final SnackbarMessageEvent mSnackbarMessageEvent = new SnackbarMessageEvent();

	public NetworkViewModel(@NonNull Application application) {
		super(application);
		mContext = application.getApplicationContext();
	}

	SnackbarMessageEvent getSnackbarMessageEvent() {
		return mSnackbarMessageEvent;
	}

	public void setGpsStatusText(boolean gpsOn) {
		this.gpsStatusText.set(gpsOn ? mContext.getString(R.string.gps_on) : mContext.getString(R.string.gps_off));
		showSnackbarMessage(gpsOn ? R.string.gps_on : R.string.gps_off);
	}

	public void setInternetStatusText(com.john.networklib_livedata.ConnectivityStatus connectivityStatus) {
		if (connectivityStatus == com.john.networklib_livedata.ConnectivityStatus.OFFLINE ||
				connectivityStatus == com.john.networklib_livedata.ConnectivityStatus.WIFI_CONNECTED_HAS_NO_INTERNET ||
				connectivityStatus == com.john.networklib_livedata.ConnectivityStatus.UNKNOWN) {
			internetStatusText.set(mContext.getString(R.string.internet_off));
			showSnackbarMessage(R.string.internet_off);
		} else {
			internetStatusText.set(mContext.getString(R.string.internet_on));
			showSnackbarMessage(R.string.internet_on);
		}
	}

	private void showSnackbarMessage(@StringRes int resourceId) {
		mSnackbarMessageEvent.setValue(resourceId);
	}
}
