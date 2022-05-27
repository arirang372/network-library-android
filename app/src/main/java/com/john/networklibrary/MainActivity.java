package com.john.networklibrary;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.john.networklib.ConnectivityStatus;
import com.john.networklib.GPSStatus;
import com.john.networklib.NetworkBusWrapper;
import com.john.networklib.NetworkEvents;
import com.john.networklib.events.ConnectivityChanged;
import com.john.networklib.events.GPSStatusChanged;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

@SuppressWarnings("ALL")
public class MainActivity extends AppCompatActivity {
	private static final int REQUEST_LOCATION_PERMISSION = 9000;
	private final EventBus bus = EventBus.getDefault();
	private Button button_gps_status;
	private Button button_internet_status;
	private NetworkBusWrapper networkBusWrapper;
	private NetworkEvents networkEvents;

	private void enableOrDisableButton(Button button, boolean enabled) {
		button.setEnabled(enabled);
		button.setBackgroundColor(enabled ? ContextCompat.getColor(this, R.color.color_button_enabled) : Color.GRAY);
		switch (button.getId()) {
			case R.id.button_gps_status:
				Utils.showSnackbar(findViewById(R.id.mainView), enabled ? "GPS is on" : "GPS is off");
				break;
			case R.id.button_internet_status:
				Utils.showSnackbar(findViewById(R.id.mainView), enabled ? "Internet is on" : "Internet is off");
				break;
		}
	}

	private NetworkBusWrapper getGreenRobotBusWrapper(final EventBus bus) {
		return new NetworkBusWrapper() {
			@Override
			public void post(Object event) {
				bus.post(event);
			}

			@Override
			public void register(Object object) {
				bus.register(object);
			}

			@Override
			public void unregister(Object object) {
				bus.unregister(object);
			}
		};
	}

	public void onButtonGoToNetworkPageClicked(View view) {
		startActivity(new Intent(this, NetworkActivity.class));
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		button_internet_status = findViewById(R.id.button_internet_status);
		button_gps_status = findViewById(R.id.button_gps_status);
		networkBusWrapper = getGreenRobotBusWrapper(bus);
		networkEvents = new NetworkEvents(this, networkBusWrapper);
		networkEvents.enableWifiScan();
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onEvent(ConnectivityChanged event) {
		if (event.getConnectivityStatus() == ConnectivityStatus.OFFLINE ||
				event.getConnectivityStatus() == ConnectivityStatus.WIFI_CONNECTED_HAS_NO_INTERNET ||
				event.getConnectivityStatus() == ConnectivityStatus.UNKNOWN) {
			enableOrDisableButton(button_internet_status, false);
		} else {
			enableOrDisableButton(button_internet_status, true);
		}
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onEvent(GPSStatusChanged event) {
		if (event.getGPSStatus() == GPSStatus.GPS_OFF) {
			enableOrDisableButton(button_gps_status, false);
			turnOnLocation();
		} else {
			enableOrDisableButton(button_gps_status, true);
		}
		Toast.makeText(this, event.getGPSStatus().name().toString(), Toast.LENGTH_LONG).show();
	}

	private void turnOnLocation(){
		LocationRequest locationRequest = LocationRequest.create();
		locationRequest.setInterval(1000);
		locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
		Task<LocationSettingsResponse> task = LocationServices.getSettingsClient(this).checkLocationSettings(builder.build());
		task.addOnFailureListener(new OnFailureListener() {
			@Override
			public void onFailure(@NonNull Exception exception) {
				if(exception instanceof ResolvableApiException){
					try{
						ResolvableApiException resolvableApiException = (ResolvableApiException) exception;
						resolvableApiException.startResolutionForResult(MainActivity.this, REQUEST_LOCATION_PERMISSION);
					}
					catch (IntentSender.SendIntentException sendIntentException){
						//do nothing...
					}
				}
			}
		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data){
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == REQUEST_LOCATION_PERMISSION){
			if(resultCode == RESULT_OK){
				LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
				if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
					enableOrDisableButton(button_gps_status, true);
				}
			}
			else if(resultCode == RESULT_CANCELED){
				LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
				if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
					enableOrDisableButton(button_gps_status, false);
				}
				turnOnLocation();
			}

		}

	}


	@Override
	public void onResume() {
		super.onResume();
		networkEvents.checkIfGpsStatusHasChanged();
		if (networkEvents.getCurrentGPSStatus() == GPSStatus.GPS_OFF) {
			enableOrDisableButton(button_gps_status, false);
			turnOnLocation();
		} else {
			enableOrDisableButton(button_gps_status, true);
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		networkBusWrapper.register(this);
		networkEvents.register();
	}

	@Override
	public void onStop() {
		networkBusWrapper.unregister(this);
		networkEvents.unregister();
		super.onStop();
	}
}
