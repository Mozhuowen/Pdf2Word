package com.superpdf2word.tools;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import java.security.MessageDigest;  
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.io.FileInputStream;  

public class FileIO
{
	static private int maxUploadSize = 10 * 1024 * 1024;
	
	private static final char HEX_DIGITS[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',  
		'A', 'B', 'C', 'D', 'E', 'F' };
	/**检查上传文件大小是否超过限制值，超过false否则true*/
	public static boolean checkFileSize(String filepath) {
		if (filepath == null)
			return false;
		File file = new File(filepath);
		long filesize = file.length();		
		
		return filesize > maxUploadSize ? false:true;
	}
	
	public static void copyByUri(Context context,String targetpath,Uri uri) {
		try {
			InputStream in = context.getContentResolver().openInputStream(uri);
			FileOutputStream out = new FileOutputStream(targetpath);
			byte[] bbuf = new byte[1024];
			int hasRead = 0;
			while ((hasRead = in.read(bbuf)) > 0) {
				out.write(bbuf);
			}
			in.close();
			out.close();
		} catch (IOException e) {e.printStackTrace();}
	}

	public static Bitmap decodeUriAsBitmap(Context context,Uri uri){
	    Bitmap bitmap = null;
	    try {
	    bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri));
	        } catch (FileNotFoundException e) {e.printStackTrace();return null;}
	    return bitmap;
	}
	

	public static int getFileSize(String targetpath) {
		File file = new File(targetpath);
		return (int)file.length();
	}

	public static String getMd5(String filepath) {	
		return md5sum(filepath);
	}
	
	public static String toHexString(byte[] b) {  
        StringBuilder sb = new StringBuilder(b.length * 2);  
        for (int i = 0; i < b.length; i++) {  
            sb.append(HEX_DIGITS[(b[i] & 0xf0) >>> 4]);  
            sb.append(HEX_DIGITS[b[i] & 0x0f]);  
        }  
        return sb.toString();  
    }  
  
    public static String md5sum(String filename) {  
        InputStream fis;  
        byte[] buffer = new byte[1024];  
        int numRead = 0;  
        MessageDigest md5;  
        try{  
            fis = new FileInputStream(filename);  
            md5 = MessageDigest.getInstance("MD5");  
            while((numRead=fis.read(buffer)) > 0) {  
                md5.update(buffer,0,numRead);  
            }  
            fis.close();  
            return toHexString(md5.digest());     
        } catch (Exception e) {  
            System.out.println("error");  
            return null;  
        }  
    }
    
    /**
	 * 获取外置SD卡路径
	 * 
	 * @return
	 */
	public static List<String> getSDCardPaths() {
		List<String> sdcardPaths = new ArrayList<String>();
		String cmd = "cat /proc/mounts";
		Runtime run = Runtime.getRuntime();// 返回与当前 Java 应用程序相关的运行时对象
		try {
			Process p = run.exec(cmd);// 启动另一个进程来执行命令
			BufferedInputStream in = new BufferedInputStream(p.getInputStream());
			BufferedReader inBr = new BufferedReader(new InputStreamReader(in));

			String lineStr;
			while ((lineStr = inBr.readLine()) != null) {
				// 获得命令执行后在控制台的输出信息
				LogUtil.i("CommonUtil:getSDCardPath", lineStr);

				String[] temp = TextUtils.split(lineStr, " ");
				// 得到的输出的第二个空格后面是路径
				String result = temp[1];
				File file = new File(result);
				if (file.isDirectory() && file.canRead() && file.canWrite()) {
					LogUtil.d("directory can read can write:",
							file.getAbsolutePath());
					// 可读可写的文件夹未必是sdcard，我的手机的sdcard下的Android/obb文件夹也可以得到
					sdcardPaths.add(result);

				}

				// 检查命令是否执行失败。
				if (p.waitFor() != 0 && p.exitValue() == 1) {
					// p.exitValue()==0表示正常结束，1：非正常结束
					LogUtil.e("CommonUtil:getSDCardPath", "命令执行失败!");
				}
			}
			inBr.close();
			in.close();
		} catch (Exception e) {
			LogUtil.e("CommonUtil:getSDCardPath", e.toString());

			sdcardPaths.add(Environment.getExternalStorageDirectory()
					.getAbsolutePath());
		}

		optimize(sdcardPaths);
		for (Iterator iterator = sdcardPaths.iterator(); iterator.hasNext();) {
			String string = (String) iterator.next();
			LogUtil.v("清除过后", string);
		}
		return sdcardPaths;
	}

	private static void optimize(List<String> sdcaredPaths) {
                if (sdcaredPaths.size() == 0) {
                    return;
                 }
		int index = 0;
		while (true) {
			if (index >= sdcaredPaths.size() - 1) {
				String lastItem = sdcaredPaths.get(sdcaredPaths.size() - 1);
				for (int i = sdcaredPaths.size() - 2; i >= 0; i--) {
					if (sdcaredPaths.get(i).contains(lastItem)) {
						sdcaredPaths.remove(i);
					}
				}
				return;
			}

			String containsItem = sdcaredPaths.get(index);
			for (int i = index + 1; i < sdcaredPaths.size(); i++) {
				if (sdcaredPaths.get(i).contains(containsItem)) {
					sdcaredPaths.remove(i);
					i--;
				}
			}

			index++;
		}

	}
	
}