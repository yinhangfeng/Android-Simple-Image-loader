package com.damingdan.lib.imageloader;

import java.lang.ref.WeakReference;
import java.util.concurrent.ConcurrentHashMap;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class LoadAndDisplayImageTask extends LoadImageTask {
	
	private WeakReference<ImageView> imageViewRef;
	private Integer ImageViewHashCode;
	private ConcurrentHashMap<Integer, String> taskForImageView;
	
	public LoadAndDisplayImageTask(
			String url,
			ImageView imageView,
			DisplayImageOptions displayImageOptions,
			MemoryCache memoryCache,
			FileCache fileCache,
			ConcurrentHashMap<Integer, String> taskForImageView,
			ImageLoadingListener loadingListener,
			ImageLoadingProgressListener progressListener) {
		super(url, displayImageOptions, memoryCache, fileCache, loadingListener, progressListener);
		imageViewRef = new WeakReference<ImageView>(imageView);
		ImageViewHashCode = imageView.hashCode();
		this.taskForImageView = taskForImageView;
	}
	
	@Override
	protected boolean isTaskNotActual() {
		if(imageViewRef.get() == null) {
			if(DEBUG) Log.i(TAG, "isTaskNotActual imageViewRef.get() == null");
			return true;
		}
		if(!url.equals(taskForImageView.get(ImageViewHashCode))) {
			if(DEBUG) Log.i(TAG, "isTaskNotActual taskForImageView.get(ImageViewHashCode) != url");
			return true;
		}
		return false;
	}
	
	@Override
	protected View getView() {
		return imageViewRef.get();
	}
	
	@Override
	protected void displayImage(Bitmap bitmap) {
		if(DEBUG) Log.i(TAG, "displayImage");
		ImageView view = imageViewRef.get();
		if(view != null) {
			view.setImageBitmap(bitmap);
		}
	}

}
