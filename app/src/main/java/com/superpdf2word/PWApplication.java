package com.superpdf2word;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;

public class PWApplication extends Application
{
	private static Context context;
	private static boolean isalterword = true;
	private static boolean isalterupload = true;
	private static boolean isftpopen = false;
	
	@Override
	public void onCreate() {
		context = this.getApplicationContext();
	}
	
	public static Context getContext() {
		return context;
	}
	
	public static boolean getIsalterword() {
		return isalterword;
	}
	
	public static void setIsalterword(boolean b) {
		isalterword = b;
	}
	
	public static boolean getIsalterupload() {
		return isalterupload;
	}
	
	public static void setIsalterupload(boolean b){
		isalterupload = b;
	}
	
	public static int getSoftWareVersion() {
    	int version = 0;
    	try {
			version  = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return version;
    }
	
	public static void setftpstat(boolean stat) {
		isftpopen = stat;
	}
	public static boolean getftpstat() {
		return isftpopen;
	}
}