package com.damingdan.lib.imageloader;

import android.view.View;

public interface ImageLoadingProgressListener {

	void onProgressUpdate(String url, View view, int current, int total);
}
