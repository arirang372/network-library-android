package com.john.networklibrary;

import android.app.Application;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class ViewModelFactory extends ViewModelProvider.NewInstanceFactory {

	private static volatile ViewModelFactory INSTANCE;

	public static ViewModelFactory getInstance(Application application) {

		if (INSTANCE == null) {
			synchronized (ViewModelFactory.class) {
				if (INSTANCE == null) {
					INSTANCE = new ViewModelFactory(application);
				}
			}
		}
		return INSTANCE;
	}

	private final Application mApplication;

	private ViewModelFactory(Application application) {
		mApplication = application;
	}

	public <T extends ViewModel> T create(Class<T> modelClass) {
		return (T) new NetworkViewModel(mApplication);
	}
}
