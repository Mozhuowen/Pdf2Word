package com.superpdf2word;

import java.net.InetAddress;

import be.ppareit.swiftp.FsService;
import be.ppareit.swiftp.FsSettings;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

public class WifiManager extends Activity
{
	private boolean isServStart = false;
	private TextView actionToggleService;
	private TextView hintview;
	private SystemBarTintManager mTintManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_manager);       
        ActionBar actionBar = getActionBar();  
        actionBar.setDisplayHomeAsUpEnabled(true);
  		initViews();
	}
	
	public void initViews() {
		this.hintview = (TextView) findViewById(R.id.activity_wifi_manager_hint);
		this.actionToggleService = (TextView) findViewById(R.id.activity_wifi_manager_toggleservice);
		isServStart = PWApplication.getftpstat();
		if (isServStart) {
			InetAddress address = FsService.getLocalInetAddress();
	        if (address != null ){
		        String addrtext = address.getHostAddress() + ":"
		                + FsSettings.getPortNumber() + "/";
		        String dirpath = PWApplication.getContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getPath();
		        this.actionToggleService.setText("停止服务");
		        this.hintview.setText(this.getString(R.string.wifihint, addrtext,dirpath));
		        this.hintview.setVisibility(View.VISIBLE);
	        } else {
	        	Toast.makeText(this, "未连接到局域网", Toast.LENGTH_SHORT).show();
	        }
		} else {
			this.actionToggleService.setText("开启服务");
	        this.hintview.setVisibility(View.GONE);
		}
		
		this.actionToggleService.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if (!isServStart){
					startServer();
					isServStart = true;
				} else {
					stopServer();
					isServStart = false;
				}
			}			
		});
	}
	
	private void startServer() {
        sendBroadcast(new Intent(FsService.ACTION_START_FTPSERVER));
        PWApplication.setftpstat(true);
        InetAddress address = FsService.getLocalInetAddress();
        if (address != null ){
	        String addrtext = address.getHostAddress() + ":"
	                + FsSettings.getPortNumber() + "/";
	        String dirpath = PWApplication.getContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getPath();
	        this.actionToggleService.setText("停止服务");
	        this.hintview.setText(this.getString(R.string.wifihint, addrtext,dirpath));
	        this.hintview.setVisibility(View.VISIBLE);
        } else {
        	Toast.makeText(this, "未连接到局域网", Toast.LENGTH_SHORT).show();
        }
    }
	
	private void stopServer() {
        sendBroadcast(new Intent(FsService.ACTION_STOP_FTPSERVER));
        PWApplication.setftpstat(false);
        this.actionToggleService.setText("开启服务");
        this.hintview.setVisibility(View.GONE);
    }
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {  
	    switch (item.getItemId()) {  
	    case android.R.id.home:  
	        Intent upIntent = NavUtils.getParentActivityIntent(this);  
	        if (NavUtils.shouldUpRecreateTask(this, upIntent)) {  
	            TaskStackBuilder.create(this)  
	                    .addNextIntentWithParentStack(upIntent)  
	                    .startActivities();  
	        } else {  
	            upIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  
	            NavUtils.navigateUpTo(this, upIntent);  
	        }  
	        return true; 
	        default:
	        	return true;
	    }  
	}
}