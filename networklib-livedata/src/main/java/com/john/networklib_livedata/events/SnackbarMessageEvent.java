package com.john.networklib_livedata.events;

import androidx.annotation.StringRes;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

public class SnackbarMessageEvent extends SingleLiveEvent<Integer> {

	public interface SnackbarObserver {

		void onNewMessage(@StringRes int snackbarMessageResourceId);
	}

	public void observe(LifecycleOwner owner, final SnackbarObserver observer) {
		super.observe(owner, new Observer<Integer>() {
			@Override
			public void onChanged(Integer integer) {
				if (integer == null)
					return;
				observer.onNewMessage(integer);
			}
		});
	}
}
