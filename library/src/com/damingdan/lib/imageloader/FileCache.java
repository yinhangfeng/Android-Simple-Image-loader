package com.damingdan.lib.imageloader;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

import android.os.Process;
import android.os.SystemClock;
import android.util.Log;

public class FileCache {
	private static final String TAG = "FileCache";
	private static final boolean DEBUG = true;
	
	public static final long MAX_FILE_CACHE_SIZE = 64 * 1024 * 1024;
	private static final long CLEAN_TARGET_SIZE = MAX_FILE_CACHE_SIZE * 4 / 5;
	private static final String INFO_FILE_NAME = ".clean_info";
	private static final long CLEAN_INTERVAL = 1000 * 3600 * 24;
	
	private File cacheDir;
	
	public FileCache(File cacheDir) {
		if(!cacheDir.exists()) {
			cacheDir.mkdirs();
		}
		this.cacheDir = cacheDir;
		cleanCache(false);
	}
	
	public String getFileName(String key) {
		return Integer.toString(key.hashCode());
	}

	public void put(String key, File file) {
		file.setLastModified(System.currentTimeMillis());
	}

	public File get(String key) {
		File file = new File(cacheDir, getFileName(key));
		if(file.exists()) {
			file.setLastModified(System.currentTimeMillis());
		}
		return file;
	}
	
	/**
	 * 清理缓存到CLEAN_TARGET_SIZE
	 * @param force true不管上一次清理时间
	 */
	public void cleanCache(boolean force) {
		if(!force && !isNeedCheckClean()) {
			if(DEBUG) Log.d(TAG, "cleanCache not clean");
			return;
		}
		if(DEBUG) Log.d(TAG, "start cleanCache");
		new Thread() {
			@Override
			public void run() {
				Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
				if(DEBUG) Log.d(TAG, "cleanCache Thread started");
				checkAndClean();
				setNewCleanTime();
			}
		}.start();
		
	}
	
	/**
	 * 清空缓存
	 */
	public void clear() {
		File[] files = cacheDir.listFiles();
		if (files != null) {
			for (File f : files) {
				f.delete();
			}
		}
	}
	
	private boolean isNeedCheckClean() {
		File info = new File(cacheDir, INFO_FILE_NAME);
		long time = System.currentTimeMillis();
		long lastModified = info.lastModified();
		if(DEBUG) Log.i(TAG, "isNeedCheckClean time=" + time + " lastModified=" + lastModified);
		return time - lastModified > CLEAN_INTERVAL;
	}
	
	private void checkAndClean() {
		File[] files = cacheDir.listFiles();
		if(files == null) {
			return;
		}
		long totalSize = 0;
		for(File file : files) {
			totalSize += file.length();
		}
		if(totalSize < MAX_FILE_CACHE_SIZE) {
			return;
		}
		if(DEBUG) Log.d(TAG, "checkAndClean start clean totalSize=" + totalSize + "BYTE " + (totalSize / 1024f / 1024) + "MB");
		long start = SystemClock.elapsedRealtime();
		int len = files.length;
		long[][] modifiedData = new long[len][2];
		for(int i = 0; i < len; ++i) {
			modifiedData[i][0] = i;
			modifiedData[i][1] = files[i].lastModified();
		}
		Arrays.sort(modifiedData, new Comparator<long[]>() {

			@Override
			public int compare(long[] lhs, long[] rhs) {
				return (int) (lhs[1] - rhs[1]);
			}
			
		});
		for(int i = 0; totalSize > CLEAN_TARGET_SIZE && i < len; ++i) {
			File file = files[(int) modifiedData[i][0]];
			totalSize -= file.length();
			file.delete();
		}
		long end = SystemClock.elapsedRealtime();
		if(DEBUG) Log.i(TAG, "checkAndClean time-consuming " + (end - start) + "ms");
	}
	
	private void setNewCleanTime() {
		File info = new File(cacheDir, INFO_FILE_NAME);
		if(!info.exists()) {
			try {
				info.createNewFile();
			} catch(Exception ig) {	
			}
		}
		info.setLastModified(System.currentTimeMillis());
	}

	@Override
	public String toString() {
		File info = new File(cacheDir, INFO_FILE_NAME);
		String timeString = new Date(info.lastModified()).toString();
		return "FileCache [file count: " + cacheDir.list().length + ", last check clean time=" + timeString + "]";
	}
}
