package com.damingdan.lib.imageloader;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

public class ImageLoader {
	private static final String TAG = "ImageLoader";
	private static final boolean DEBUG = true;
	
	private static volatile ImageLoader instance;
	
	private AntiRepeatTaskExecutor<String> executor = new AntiRepeatTaskExecutor<String>();
	/** key=ImageView.hashCode() value=url 记录最后一次ImageView请求的url防止图片错位 */
	private ConcurrentHashMap<Integer, String> taskForImageView = new ConcurrentHashMap<Integer, String>(32, 0.75f, 8);
	/** 默认图片显示选项 */
	private DisplayImageOptions defDisplayImageOptions = new DisplayImageOptions.Builder().build();
	private MemoryCache memoryCache = new MemoryCache();
	private FileCache fileCache;
	
	private ImageLoader() {
		
	}
	
	public static ImageLoader getInstance() {
		if(instance == null) {
			synchronized(ImageLoader.class) {
				if(instance == null) {
					instance = new ImageLoader();
				}
			}
		}
		return instance;
	}
	
	public void init(File fileCacheDir) {
		fileCache = new FileCache(fileCacheDir);
	}
	
	public void displayImage(String url, ImageView imageView) {
		displayImage(url, imageView, defDisplayImageOptions, null, null);
	}
	
	public void displayImage(String url, ImageView imageView, DisplayImageOptions options,
			 ImageLoadingListener loadingListener, ImageLoadingProgressListener progressListener) {
		if(DEBUG) Log.i(TAG, "displayImage url=" + url);
		if(imageView == null || options == null) {
			throw new NullPointerException();
		}
		if(TextUtils.isEmpty(url)) {
			if(options.shouldShowImageOnFail()) {
				imageView.setImageResource(options.getImageResOnFail());
			}
			if(loadingListener != null) {
				loadingListener.onLoadingFailed(url, imageView, null);
			}
		}
		if(loadingListener != null) {
			loadingListener.onLoadingStarted(url, imageView);
		}
		Bitmap bitmap = memoryCache.get(url);
		if(bitmap != null) {
			imageView.setImageBitmap(bitmap);
			if(loadingListener != null) {
				loadingListener.onLoadingComplete(url, imageView, bitmap);
			}
		} else {
		//TODO	LoadAndDisplayImageTask task = new LoadAndDisplayImageTask(url, );
		}
		
	}
	
	public void loadImage(String url, ImageLoadingListener listener) {
		loadImage(url, defDisplayImageOptions, listener, null);
	}
	
	public void loadImage(String url, DisplayImageOptions options,
			  ImageLoadingListener loadingListener, ImageLoadingProgressListener progressListener) {
		if(DEBUG) Log.i(TAG, "loadImage url=" + url);
	}

}
