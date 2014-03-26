package com.damingdan.lib.imageloader.sample;

import java.util.Arrays;

import android.util.Log;

public final class L {
	private static final String TAG = "GJF";
	private static boolean DEBUG = true;

	private L() {
	}

	public static void d(String TAG, String message, Object... args) {
		if(DEBUG) log(Log.DEBUG, TAG, message, null, args);
	}
	
	public static void d(String message, Object... args) {
		if(DEBUG) log(Log.DEBUG, null, message, null, args);
	}
	
	public static void i(String TAG, String message, Object... args) {
		if(DEBUG) log(Log.INFO, TAG, message, null, args);
	}

	public static void i(String message, Object... args) {
		if(DEBUG) log(Log.INFO, null, message, null, args);
	}
	
	public static void w(String TAG, String message, Object... args) {
		if(DEBUG) log(Log.WARN, TAG, message, null, args);
	}

	public static void w(String message, Object... args) {
		if(DEBUG) log(Log.WARN, null, message, null, args);
	}
	
	public static void e(String TAG, Throwable ex) {
		if(DEBUG) log(Log.ERROR, TAG, null, ex);
	}

	public static void e(Throwable ex) {
		if(DEBUG) log(Log.ERROR, null, null, ex);
	}
	
	public static void e(String TAG, String message, Object... args) {
		if(DEBUG) log(Log.ERROR, TAG, message, null, args);
	}

	public static void e(String message, Object... args) {
		if(DEBUG) log(Log.ERROR, null, message, null, args);
	}
	
	public static void e(String TAG, String message, Throwable ex, Object... args) {
		if(DEBUG) log(Log.ERROR, TAG, message, ex, args);
	}

	public static void e(String message, Throwable ex, Object... args) {
		if(DEBUG) log(Log.ERROR, null, message, ex, args);
	}

	private static void log(int priority, String TAG, String message, Throwable ex, Object... args) {
		if (args != null && args.length > 0) {
			message += " " + Arrays.toString(args);
		}
		if (ex != null) {
			if(message == null) {
				message = ex.getMessage();
			}
			message += "\n" + Log.getStackTraceString(ex);
		}
		Log.println(priority, TAG == null ? L.TAG : TAG, message);
	}
}