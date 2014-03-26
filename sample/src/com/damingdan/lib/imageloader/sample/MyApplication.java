package com.damingdan.lib.imageloader.sample;

import com.damingdan.lib.imageloader.ImageLoader;

import android.app.Application;

public class MyApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

		initImageLoader();
	}

	public void initImageLoader() {
		ImageLoader.getInstance().init(getCacheDir());
	}
}