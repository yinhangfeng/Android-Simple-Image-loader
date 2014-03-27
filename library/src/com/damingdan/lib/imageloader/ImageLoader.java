package com.damingdan.lib.imageloader;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

public class ImageLoader {
	private static final String TAG = "ImageLoader";
	private static final boolean DEBUG = true;
	
	private static volatile ImageLoader instance;
	
	/** 封装的线程池 可防止请求相同url的任务并发执行 */
	private AntiRepeatTaskExecutor<String> executor = new AntiRepeatTaskExecutor<String>();
	/** key=ImageView.hashCode() value=url 记录某ImageView最后一次请求的url 防止图片错位 */
	private ConcurrentHashMap<Integer, String> taskForImageView
			= new ConcurrentHashMap<Integer, String>(32, 0.75f, 8);
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
	
	public void displayImage(String url, ImageView imageView,
			DisplayImageOptions displayImageOptions,
			ImageLoadingListener loadingListener,
			ImageLoadingProgressListener progressListener) {
		if(DEBUG) Log.i(TAG, "displayImage url=" + url);
		if(imageView == null || displayImageOptions == null) {
			throw new NullPointerException();
		}
		if(TextUtils.isEmpty(url)) {
			if(DEBUG) Log.w(TAG, "displayImage TextUtils.isEmpty(url)");
			if(loadingListener != null) {
				loadingListener.onLoadingStarted(url, imageView);
			}
			if(displayImageOptions.shouldShowImageOnFail()) {
				imageView.setImageResource(displayImageOptions.getImageResOnFail());
			} else if(displayImageOptions.shouldShowImageOnLoading()) {
				imageView.setImageResource(displayImageOptions.getImageResOnLoading());
			}
			if(loadingListener != null) {
				loadingListener.onLoadingFailed(url, imageView, null);
			}
			return;
		}
		taskForImageView.put(imageView.hashCode(), url);
		if(loadingListener != null) {
			loadingListener.onLoadingStarted(url, imageView);
		}
		Bitmap bitmap = memoryCache.get(url);
		if(bitmap != null) {
			if(DEBUG) Log.i(TAG, "displayImage bitmap in memoryCache");
			imageView.setImageBitmap(bitmap);
			if(loadingListener != null) {
				loadingListener.onLoadingComplete(url, imageView, bitmap);
			}
		} else {
			if(displayImageOptions.shouldShowImageOnLoading()) {
				imageView.setImageResource(displayImageOptions.getImageResOnLoading());
			}
			LoadAndDisplayImageTask task = new LoadAndDisplayImageTask(url,
					imageView, displayImageOptions, memoryCache, fileCache,
					taskForImageView, loadingListener, progressListener);
			executor.execute(url, task);
		}
	}
	
	public void loadImage(String url, ImageLoadingListener listener) {
		loadImage(url, defDisplayImageOptions, listener, null);
	}
	
	public void loadImage(String url, DisplayImageOptions displayImageOptions,
			ImageLoadingListener loadingListener,
			ImageLoadingProgressListener progressListener) {
		if(DEBUG) Log.i(TAG, "loadImage url=" + url);
		if(TextUtils.isEmpty(url) || displayImageOptions == null || loadingListener == null) {
			throw new IllegalArgumentException();
		}
		loadingListener.onLoadingStarted(url, null);
		Bitmap bitmap = memoryCache.get(url);
		if(bitmap != null) {
			if(DEBUG) Log.i(TAG, "loadImage bitmap in memoryCache");
			loadingListener.onLoadingComplete(url, null, bitmap);
		} else {
			LoadImageTask task = new LoadImageTask(url, displayImageOptions,
					memoryCache, fileCache, loadingListener, progressListener);
			executor.execute(url, task);
		}
	}
	
	public MemoryCache getMemoryCache() {
		return memoryCache;
	}
	
	public FileCache getFileCache() {
		return fileCache;
	}
	
	public void putToCache(String url, File file, Bitmap bitmap) {
    	if(DEBUG) Log.i(TAG, "putToCache url="+url);
    	if(TextUtils.isEmpty(url)) {
    		return;
    	}
    	if(file != null && file.exists()) {
    		try {
    			IoUtils.copyFile(file, fileCache.get(url));
    		} catch(IOException e) {
    			if(DEBUG) Log.e(TAG, "putToCache copyFile error=" + e);
    		}
    	}
    	if(bitmap != null && !bitmap.isRecycled()) {
    		memoryCache.put(url, bitmap);
    	}
    }
	
	public void logStatus() {
		StringBuilder sb = new StringBuilder();
		sb.append("ImageLoader=============================================\n")
				.append("AntiRepeatTaskExecutor:\n").append(executor.toString())
				.append("\ntaskForImageView.size:").append(taskForImageView.size())
				.append("\nMemoryCache:\n").append(memoryCache.toString())
				.append("\nFileCache:\n").append(fileCache.toString())
				.append("\n=============================================");
		Log.i(TAG, sb.toString());
	}

}
