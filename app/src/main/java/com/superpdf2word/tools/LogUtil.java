package com.superpdf2word.tools;

import com.superpdf2word.BuildConfig;

import android.util.Log;

public class LogUtil{
	
	public static boolean isDebug = BuildConfig.DEBUG;
	
	public static final int VERBOSE = 1;
	
	public static final int DEBUG = 2;
	
	public static final int INFO = 3;
	
	public static final int WARN = 4;
	
	public static final int ERROR = 5;
	
	public static final int NOTHING = 6;
	
	public static final int LEVEL = 1;
	
	public static void v(String tag,String msg) {
		if (LEVEL <= VERBOSE && isDebug) {
			Log.v(tag, msg);
		}
	}
	
	public static void d(String tag,String msg) {
		if (LEVEL <= DEBUG && isDebug) {
			Log.d(tag, msg);
		}
	}
	
	public static void i(String tag,String msg) {
		if (LEVEL <= INFO) {
			Log.i(tag, msg);
		}
	}
	
	public static void w(String tag,String msg) {
		if (LEVEL <= WARN) {
			Log.w(tag, msg);
		}
	}
	
	public static void e(String tag,String msg) {
		if (LEVEL <= ERROR) {
			Log.e(tag, msg);
		}
	}
}