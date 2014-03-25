package com.damingdan.lib.imageloader;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

import android.os.Process;

public class FileCache {
	public static final long MAX_FILE_CACHE_SIZE = 1 * 1024 * 1024;
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
			return;
		}
		new Thread() {
			@Override
			public void run() {
				Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
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
		return System.currentTimeMillis() - info.lastModified() > CLEAN_INTERVAL;
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

}
