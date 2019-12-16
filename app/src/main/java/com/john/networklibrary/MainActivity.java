package com.john.networklibrary;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.john.networklib.ConnectivityStatus;
import com.john.networklib.GPSStatus;
import com.john.networklib.NetworkBusWrapper;
import com.john.networklib.NetworkEvents;
import com.john.networklib.events.ConnectivityChanged;
import com.john.networklib.events.GPSStatusChanged;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

@SuppressWarnings("ALL")
public class MainActivity extends AppCompatActivity {
    private NetworkBusWrapper networkBusWrapper;
    private NetworkEvents networkEvents;
    private Button button_internet_status;
    private final EventBus bus = EventBus.getDefault();
    private Button button_gps_status;

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

    private void enableOrDisableButton(Button button, boolean enabled) {
        button.setEnabled(enabled);
        button.setBackgroundColor(enabled ? ContextCompat.getColor(this, R.color.color_button_enabled) : Color.GRAY);
    }

    @Override
    public void onStart() {
        super.onStart();
        networkBusWrapper.register(this);
        networkEvents.register();
        if (networkEvents.getCurrentGPSStatus() == GPSStatus.GPS_OFF) {
            enableOrDisableButton(button_gps_status, false);
        }
    }

    @Override
    public void onStop() {
        networkBusWrapper.unregister(this);
        networkEvents.unregister();
        super.onStop();
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
        } else {
            enableOrDisableButton(button_gps_status, true);
        }
    }

    private NetworkBusWrapper getGreenRobotBusWrapper(final EventBus bus) {
        return new NetworkBusWrapper() {
            @Override
            public void register(Object object) {
                bus.register(object);
            }

            @Override
            public void unregister(Object object) {
                bus.unregister(object);
            }

            @Override
            public void post(Object event) {
                bus.post(event);
            }
        };
    }
}