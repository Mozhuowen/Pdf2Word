package com.superpdf2word.tools;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.os.Environment;


public class FileUtil {
	
	public static File updateDir = null;
	public static File updateFile = null;
	/***********��������APK��Ŀ¼***********/
	public static final String KonkaApplication = "konkaUpdateApplication";
	
	public static boolean isCreateFileSucess;

	public static void createFile(Context context,String app_name) {
		
		if (android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment.getExternalStorageState())) {
			isCreateFileSucess = true;
			
			updateDir = new File(context.getExternalCacheDir()+ "/");
			updateFile = new File(updateDir + "/" + app_name + ".apk");

			if (!updateDir.exists()) {
				updateDir.mkdirs();
			}
			if (!updateFile.exists()) {
				try {
					updateFile.createNewFile();
				} catch (IOException e) {
					isCreateFileSucess = false;
					e.printStackTrace();
				}
			}

		}else{
			isCreateFileSucess = false;
		}
	}
}