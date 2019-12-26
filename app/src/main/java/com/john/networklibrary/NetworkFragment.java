package com.john.networklibrary;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.john.networklib_livedata.ConnectivityStatus;
import com.john.networklib_livedata.NetworkEvents;
import com.john.networklib_livedata.events.SnackbarMessageEvent.SnackbarObserver;
import com.john.networklibrary.databinding.FragmentNetworkBinding;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

public class NetworkFragment extends Fragment {

	public static NetworkFragment newInstance() {
		return new NetworkFragment();
	}

	private NetworkEvents events;
	private FragmentNetworkBinding mFragmentNetworkBinding;
	private NetworkViewModel viewModel;

	public NetworkFragment() {
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setupSnackbar();
		events = new NetworkEvents(getActivity());
		events.enableWifiScan();
		setupNetworkChangedEvent();
		setUpGpsChangedEvent();
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mFragmentNetworkBinding = FragmentNetworkBinding.inflate(inflater, container, false);
		viewModel = NetworkActivity.obtainViewModel(getActivity());
		mFragmentNetworkBinding.setViewModel(viewModel);
		return mFragmentNetworkBinding.getRoot();
	}

	@Override
	public void onStart() {
		super.onStart();
		events.register();
		events.checkIfGpsStatusHasChanged();
		viewModel.setGpsStatusText(events.getCurrentGPSStatus());
	}

	@Override
	public void onStop() {
		super.onStop();
		events.unregister();
	}

	private void setUpGpsChangedEvent() {
		events.getGpsLiveEvent().observe(this, new Observer<Boolean>() {
			@Override
			public void onChanged(Boolean gpsOn) {
				viewModel.setGpsStatusText(gpsOn);
			}
		});
	}

	private void setupNetworkChangedEvent() {
		events.getNetworkConnectionChangedEvent().observe(this, new Observer<ConnectivityStatus>() {
			@Override
			public void onChanged(ConnectivityStatus connectivityStatus) {
				viewModel.setInternetStatusText(connectivityStatus);
			}
		});

		events.getInternetConnectionChangedEvent().observe(this, new Observer<ConnectivityStatus>() {
			@Override
			public void onChanged(ConnectivityStatus connectivityStatus) {
				viewModel.setInternetStatusText(connectivityStatus);
			}
		});
	}

	private void setupSnackbar() {
		viewModel.getSnackbarMessageEvent().observe(this, new SnackbarObserver() {
			@Override
			public void onNewMessage(int snackbarMessageResourceId) {
				Utils.showSnackbar(getView(), getString(snackbarMessageResourceId));
			}
		});
	}
}
