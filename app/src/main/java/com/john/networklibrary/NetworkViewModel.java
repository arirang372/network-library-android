package com.john.networklibrary;

import android.app.Application;
import android.content.Context;

import com.john.networklib_livedata.events.SnackbarMessageEvent;

import androidx.annotation.NonNull;
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

}
