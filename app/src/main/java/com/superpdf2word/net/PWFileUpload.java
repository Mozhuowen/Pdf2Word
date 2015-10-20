package com.superpdf2word.net;

import java.io.File;
import java.util.Calendar;

import org.apache.http.Header;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.superpdf2word.MainActivity;
import com.superpdf2word.PWApplication;
import com.superpdf2word.models.ResNewTask;
import com.superpdf2word.tools.FileIO;
import com.superpdf2word.tools.LogUtil;
import com.superpdf2word.tools.encrypt.MsgCrypt;

public class PWFileUpload
{
	public static final String BASE_URL = "http://121.40.223.10:8080/superpdf2word/";
//	public static final String BASE_URL = "http://www.baidu.com/";
	
	String filepath;
	public PWFileUpload(String filepath) {
		this.filepath = filepath;
	}
	
	public static String getAbsoluteUrl(String urlpath) {
		return BASE_URL + urlpath;
	}
	
	public static void uploadFile(String path,final ProgressBar progressbar,final MainActivity context) throws Exception {  
		File file = new File(path);  
		String filemd5 = FileIO.getMd5(path);
		String serviceid = ((TelephonyManager)PWApplication.getContext().getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
		String phonemodel = android.os.Build.MODEL;;
		String sv = android.os.Build.VERSION.RELEASE;
		String brand = android.os.Build.BRAND;
		long timestamp = Calendar.getInstance().getTimeInMillis();
		int softwareversion = PWApplication.getSoftWareVersion();
		LogUtil.v("PWFileUpload info: ", "file md5: "+filemd5+" timestamp: "+timestamp+" serviceid: "+serviceid);
		String tobeencrypt = String.valueOf(timestamp) + serviceid + filemd5 + softwareversion;
		LogUtil.v("tobeencrypt", tobeencrypt);
		String auth = MsgCrypt.encryptMsg(tobeencrypt);
		if (file.exists() && file.length() > 0) {  
		    AsyncHttpClient client = new AsyncHttpClient();  
		    RequestParams params = new RequestParams();  
		    params.put("upload", file);  
		    params.put("filemd5", filemd5);
		    params.put("serviceid", serviceid);
		    params.put("timestamp", timestamp);
		    params.put("phonemodel", phonemodel);
		    params.put("sv", sv);
		    params.put("brand", brand);
		    params.put("servicetype", 0);
		    params.put("auth", auth);
		    params.put("version",softwareversion);
		    // 上传文件  
		    client.post(getAbsoluteUrl("newtask"), params, new AsyncHttpResponseHandler() {  
		    	@Override  
		        public void onSuccess(int statusCode, Header[] headers,  
		                byte[] responseBody) {  
		    		String json = new String(responseBody);
		    		context.onFinishUpload(new Gson().fromJson(json, ResNewTask.class));
		            // 上传成功后要做的工作  
		            Toast.makeText(PWApplication.getContext(), "上传成功", Toast.LENGTH_LONG).show();  
		            LogUtil.v("PWFileUpload info: ", "after upload response: "+json);
//		            progress.setProgress(0);  
		        }  
		  
		        @Override  
		        public void onFailure(int statusCode, Header[] headers,  
		                byte[] responseBody, Throwable error) {  
		        	if (responseBody != null){
			        	String json = new String(responseBody);
			            // 上传失败后要做到工作  
			            Toast.makeText(PWApplication.getContext(), "上传失败", Toast.LENGTH_LONG).show(); 
			            LogUtil.v("PWFileUpload fail info:", statusCode+" "+json);
			            context.onFinishUpload(new Gson().fromJson(json, ResNewTask.class));
		        	} else {
		        		context.onUploadFail();
		        	}
		        } 
		  
		        @Override  
		        public void onProgress(int bytesWritten, int totalSize) {  
		            // TODO Auto-generated method stub  
		            super.onProgress(bytesWritten, totalSize);  
		            int count = (int) ((bytesWritten * 1.0 / totalSize) * 100);  
		            // 上传进度显示  
		            progressbar.setProgress(count);  
		            LogUtil.v("上传 Progress>>>>>", bytesWritten + " / " + totalSize);  
		        }  
		  
		        @Override  
		        public void onRetry(int retryNo) {  
		            // TODO Auto-generated method stub  
		            super.onRetry(retryNo);  
		            // 返回重试次数  
		            LogUtil.v("PWFileUpload fail info:", "uploading retry!");
		        }
		  
		    });  
		} else {  
//		    Toast.makeText(mContext, "文件不存在", Toast.LENGTH_LONG).show();  
		}  
	}
}