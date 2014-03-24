package com.damingdan.lib.imageloader.sample;

import android.app.Application;

public class MyApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

		initImageLoader();
	}

	public static void initImageLoader() {
	}
}