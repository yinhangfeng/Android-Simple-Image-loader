package com.damingdan.lib.imageloader;

import java.lang.ref.WeakReference;
import java.util.concurrent.ConcurrentHashMap;

import android.graphics.Bitmap;
import android.util.Log;
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
			if(DEBUG) Log.i(TAG, "isTaskNotActual !url.equals(taskForImageView.get(ImageViewHashCode))");
			return true;
		}
		return false;
	}
	
	protected void onCancelled() {
		if(loadingListener != null) {
			handler.post(new Runnable() {

				@Override
				public void run() {
					loadingListener.onLoadingCancelled(url, imageViewRef.get());
				}
				
			});
		}
	}
	
	protected void onFailed(final Exception e) {
		if(loadingListener != null || displayImageOptions.shouldShowImageOnFail()) {
			handler.post(new Runnable() {

				@Override
				public void run() {
					ImageView imageView = imageViewRef.get();
					if(!isTaskNotActual()) {
						if(displayImageOptions.shouldShowImageOnFail()) {
							imageView.setImageResource(displayImageOptions.getImageResOnFail());
						}
					}
					if(loadingListener != null) {
						loadingListener.onLoadingFailed(url, imageView, e);
					}
				}
				
			});
		}
	}
	
	protected void onComplete(final Bitmap bm) {
		handler.post(new Runnable() {

			@Override
			public void run() {
				ImageView imageView = imageViewRef.get();
				if (isTaskNotActual()) {
					if (loadingListener != null) {
						loadingListener.onLoadingCancelled(url, imageView);
					}
				} else {
					imageView.setImageBitmap(bm);
					if (loadingListener != null) {
						loadingListener.onLoadingComplete(url, imageView, bm);
					}
				}
			}

		});
	}

	@Override
	public void onBytesCopied(final int current, final int total) {
		if(progressListener != null) {
			final float percent = current >= total ? 1f : ((float) current / total);
			handler.post(new Runnable() {

				@Override
				public void run() {
					progressListener.onProgressUpdate(url, imageViewRef.get(), current, total, percent);
				}
				
			});
		}
		
	}

}
