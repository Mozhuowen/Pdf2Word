package com.superpdf2word.net;

import org.apache.http.Header;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.superpdf2word.MainActivity;
import com.superpdf2word.models.ResCheckStat;
import com.superpdf2word.models.ResponseVersion;
import com.superpdf2word.tools.LogUtil;

public class CheckUpdate
{
	private static final String URL = "http://121.40.223.10:8080/superpdf2word/checkversion";
//	private static final String URL = "http://192.168.199.200/superpdf2word/checkversion";
	
	public static void check(final MainActivity context) {
		
		AsyncHttpClient client = new AsyncHttpClient();
		
		client.get(URL, null, new  AsyncHttpResponseHandler(){

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] responsebyte,
					Throwable arg3) {
				if(responsebyte != null) {
					String json = new String(responsebyte);
					LogUtil.v("CheckUpdate response fail info: ", json);
				} else {
					
				}
			}

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] responsebyte) {
				String json = new String(responsebyte);
				LogUtil.v("CheckTaskStat response info: ", json);
				ResponseVersion res = new Gson().fromJson(json, ResponseVersion.class);
				context.onUpdate(res.getVersion());
			}

		});
	}
}