package com.superpdf2word.net;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.superpdf2word.PWService;
import com.superpdf2word.models.ResCheckStat;
import com.superpdf2word.tools.LogUtil;

import org.apache.http.Header;

public class CheckTaskStat
{
	private static final String URL = "http://121.40.223.10:8080/superpdf2word/checkstat";
	private String filecode;
	private PWService service;
	public CheckTaskStat(String filecode,PWService service) {
		this.filecode = filecode;
		this.service = service;
	}
	
	
	public static void check(final String filecode,final PWService service) {
		AsyncHttpClient client = new AsyncHttpClient();  
		RequestParams params = new RequestParams(); 
		params.add("filecode", filecode);
		
		LogUtil.v("CheckTaskStat info:", "start to checktaskstat! filecode: "+filecode);
		
		
		client.get(URL, params, new  AsyncHttpResponseHandler(){

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] responsebyte,
					Throwable arg3) {
				if(responsebyte != null) {
					String json = new String(responsebyte);
					LogUtil.v("CheckTaskStat response fail info: ", json);
				} else {
					
				}
			}

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] responsebyte) {
				String json = new String(responsebyte);
				LogUtil.v("CheckTaskStat response info: ", json);
				ResCheckStat res = new Gson().fromJson(json, ResCheckStat.class);
				service.onCheckStat(filecode, res.getStatcode());
			}

		});
	}
	
}