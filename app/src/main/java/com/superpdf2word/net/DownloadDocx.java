package com.superpdf2word.net;

import java.io.File;

import org.apache.http.Header;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.superpdf2word.PWService;
import com.superpdf2word.tools.LogUtil;

public class DownloadDocx
{
	private static final String URL = "http://121.40.223.10:8080/superpdf2word//downfinished";
	
	public static void down(final String filecode,final PWService service,File file) {
		AsyncHttpClient client = new AsyncHttpClient();  
		RequestParams params = new RequestParams(); 
		params.add("filecode", filecode);
		LogUtil.v("DownloadDocx info: ", "start to downlaod a file: "+filecode);
		client.get(URL, params, new FileAsyncHttpResponseHandler(file){

			@Override
			public void onFailure(int arg0, Header[] arg1, Throwable arg2,
					File arg3) {
				// TODO Auto-generated method stub
				service.onFinishDownload(filecode, false);
			}

			@Override
			public void onSuccess(int arg0, Header[] arg1, File arg2) {
				// TODO Auto-generated method stub
				service.onFinishDownload(filecode, true);
			}
			
		});
	}
}