package com.damingdan.lib.imageloader;

import android.graphics.Bitmap;
import android.view.View;

public interface ImageLoadingListener {

	void onLoadingStarted(String url, View view);

	void onLoadingFailed(String url, View view, Exception ex);

	void onLoadingComplete(String url, View view, Bitmap bm);

	void onLoadingCancelled(String url, View view);
}
