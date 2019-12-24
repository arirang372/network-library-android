package com.john.networklibrary;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.john.networklib_livedata.events.SnackbarMessageEvent.SnackbarObserver;
import com.john.networklibrary.databinding.FragmentNetworkBinding;

import androidx.fragment.app.Fragment;

public class NetworkFragment extends Fragment {

	public static NetworkFragment newInstance() {
		return new NetworkFragment();
	}

	private FragmentNetworkBinding mFragmentNetworkBinding;
	private NetworkViewModel viewModel;

	public NetworkFragment() {
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setupSnackbar();

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mFragmentNetworkBinding = FragmentNetworkBinding.inflate(inflater, container, false);
		viewModel = NetworkActivity.obtainViewModel(getActivity());
		mFragmentNetworkBinding.setViewModel(viewModel);
		return mFragmentNetworkBinding.getRoot();
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
