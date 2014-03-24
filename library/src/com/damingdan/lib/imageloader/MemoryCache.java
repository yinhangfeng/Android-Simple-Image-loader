package com.damingdan.lib.imageloader;

import android.graphics.Bitmap;
import android.util.Log;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import com.googlecode.concurrentlinkedhashmap.EvictionListener;
import com.googlecode.concurrentlinkedhashmap.Weigher;

public class MemoryCache implements EvictionListener<String, Bitmap>, Weigher<Bitmap> {
	private static final String TAG = "BitmapLruCache";
	private static final boolean DEBUG = true;
	private static final long DEF_MAX_SIZE = Runtime.getRuntime().maxMemory() / 8;
	
	private ConcurrentLinkedHashMap<String, Bitmap> cache;
	private long maxSize;
	
	public MemoryCache() {
		this(DEF_MAX_SIZE);
	}

	public MemoryCache(long maxSize) {
		cache = new ConcurrentLinkedHashMap.Builder<String, Bitmap>()
				.initialCapacity(16)
				.maximumWeightedCapacity(maxSize)
				.concurrencyLevel(8)
				.listener(this)
				.weigher(this)
				.build();
		this.maxSize = maxSize;
	}

	@Override
	public void onEviction(String key, Bitmap bm) {
		if(DEBUG) Log.i(TAG, "onEviction key=" + key + " bitmap size " + getSizeInBytes(bm) / 1024 + "KB");
	}

	@Override
	public int weightOf(Bitmap bm) {
		return getSizeInBytes(bm);
	}

	public Bitmap get(String key) {
		return cache.get(key);
	}

	public Bitmap put(String key, Bitmap bm) {
		return cache.put(key, bm);
	}

	public boolean exists(String key) {
		return cache.containsKey(key);
	}

	public void clear() {
		cache.clear();
	}
	
	private int getSizeInBytes(Bitmap bitmap) {
		return bitmap.getRowBytes() * bitmap.getHeight();
	}

	@Override
	public String toString() {
		return "MemoryCache [maxSize=" + maxSize + ", size=" + cache.size()
				+ ", weightedSize=" + cache.weightedSize() + "BYPE "
				+ (((float) cache.weightedSize()) / 1024 / 1024) + "MB ]";
	}
	
	
}
