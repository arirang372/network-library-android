package com.john.networklibrary;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;

public class NetworkActivity extends AppCompatActivity {

	public static NetworkViewModel obtainViewModel(FragmentActivity activity) {
		ViewModelFactory factory = ViewModelFactory.getInstance(activity.getApplication());
		NetworkViewModel viewModel = ViewModelProviders.of(activity, factory).get(NetworkViewModel.class);
		return viewModel;
	}

	private NetworkViewModel viewModel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_network);
		setupToolbar();
		setupFragment();

		viewModel = obtainViewModel(this);
	}

	private void setupFragment() {
		NetworkFragment fragment = (NetworkFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
		if (fragment == null) {
			fragment = NetworkFragment.newInstance();
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.contentFrame, fragment)
					.commit();
		}
	}

	private void setupToolbar() {
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		ActionBar ab = getSupportActionBar();
		ab.setDisplayHomeAsUpEnabled(true);
	}

}
