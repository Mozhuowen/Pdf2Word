package com.superpdf2word;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import lecho.lib.filechooser.FilechooserActivity;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.superpdf2word.PWService.PWBinder;
import com.superpdf2word.adapter.MainAdapter;
import com.superpdf2word.db.DataBaseUtil;
import com.superpdf2word.models.ModelAppUpdate;
import com.superpdf2word.models.ResNewTask;
import com.superpdf2word.net.CheckUpdate;
import com.superpdf2word.net.NetFailStr;
import com.superpdf2word.net.PWFileUpload;
import com.superpdf2word.tools.FileIO;
import com.superpdf2word.tools.LogUtil;
import com.superpdf2word.tools.SystemUtil;
import com.superpdf2word.db.beans.PWFile;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Service;
import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.DialogInterface.OnKeyListener;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.View.OnClickListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity implements OnTouchListener {
	private View viewadd;
	private ListView listview;
	private View settingicon;
	private View settingview;
	private View wifiview;
	private View aboutview;
	private MainAdapter mainadapter;
	Dialog dialog2;
	/**上传对话框的进度条*/
	ProgressBar progressbar;
	private Handler messhandler;
	
	private String currentFilePath;
	private PWBinder servicebinder;
	private SystemBarTintManager mTintManager;
	private long mLastBacktime = 0;	//记录上次点击返回键时间，用于判断是否退出
	private ModelAppUpdate m;	//升级信息

	private ServiceConnection serviceconn = new ServiceConnection(){
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			servicebinder = (PWBinder) service;
			servicebinder.context = MainActivity.this;
			servicebinder.addAllTask(DataBaseUtil.getallTask());
			LogUtil.v("MainActivity info: ", "bind service success!");
		}
		@Override
		public void onServiceDisconnected(ComponentName name) {}		
	};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);       
        
  		
  		bindSevice();
  		initViews(); 		
  		checkUpdate();  	
  		this.setOverflowShowingAlways();
    }
    
    public void initViews() {
//    	this.viewadd = findViewById(R.id.activity_main_actionadd);
    	this.listview = (ListView)findViewById(R.id.activity_main_listview);
//    	settingicon = findViewById(R.id.activity_main_settingicon);
    	this.settingview = findViewById(R.id.activity_main_setting);
    	this.wifiview = findViewById(R.id.activity_main_wifi);
    	this.aboutview = findViewById(R.id.activity_main_about);
    	
    	this.listview.setOnTouchListener(this);
    	
    	if (this.settingicon != null)
	    	this.settingicon.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					if (settingview.getVisibility() == View.GONE)
						settingview.setVisibility(View.VISIBLE);
					else
						settingview.setVisibility(View.GONE);
				}    		
	    	});
    	this.wifiview.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				openWifi();
				settingview.setVisibility(View.GONE);
			}    		
    	});
    	this.aboutview.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				MainActivity.this.startActivity(new Intent(MainActivity.this,AboutActivity.class));
				settingview.setVisibility(View.GONE);
			}   		
    	});
    	
    	List<PWFile> tasks = DataBaseUtil.getallTask();
    	this.mainadapter = new MainAdapter(this,tasks);
    	this.listview.setAdapter(mainadapter);
    	
    	messhandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == 200) {	//网络活动失败
					Toast.makeText(MainActivity.this, "网络错误，请检查网络连接是否正常", Toast.LENGTH_SHORT).show();
				} else if (msg.what == 300){
					
				} else if (msg.what == 400){	//更新进度条
					int progress = (int)msg.arg1;
					progressbar.setProgress(progress);
				} else {
					String mess = (String)msg.obj;
					Toast.makeText(MainActivity.this, mess, Toast.LENGTH_SHORT).show();
				}
			}
		};
    	
    }
    
    public void alterUploadProgress() {
    	Builder builder = new AlertDialog.Builder(this,AlertDialog.THEME_HOLO_LIGHT);	
		View view = LayoutInflater.from(this).inflate(R.layout.dialog_progressbar, null);
		progressbar = (ProgressBar)view.findViewById(R.id.dialog_progressbar_bar);
		builder.setView(view);
		builder.setOnKeyListener(new OnKeyListener(){
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK  
                        && event.getRepeatCount() == 0) {  
					
                }  
				return false;
			}			
		});
		dialog2 = builder.create();
		dialog2.setCanceledOnTouchOutside(false);
		dialog2.show();
    }
    
    public void bindSevice(){
    	Intent intent = new Intent();
    	intent.setAction("com.superpdf2word.PWService");
    	intent = SystemUtil.getExplicitIntent(this, intent);
    	bindService(intent,serviceconn,Service.BIND_AUTO_CREATE);
    }
    
    private void showFileChooser() {
    	/*Intent intent = new Intent(this, FilechooserActivity.class);
        intent.putExtra(FilechooserActivity.BUNDLE_ITEM_TYPE, ItemType.FILE);
        intent.putExtra(FilechooserActivity.BUNDLE_SELECTION_MODE, SelectionMode.SINGLE_ITEM);
        startActivityForResult(intent, 200);*/
    	Intent intent = new Intent(this,FilechooserActivity.class);
    	startActivityForResult(intent,200);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 200:   
                if (resultCode == RESULT_OK) {
                    final Uri uri = data.getData();                
                    String path  = null;                   
                    // Get the File path from the Uri
                    ArrayList<String> paths = data.getStringArrayListExtra(FilechooserActivity.BUNDLE_SELECTED_PATHS);
    				if (paths.size() == 1 )
    					path = paths.get(0);
    				//check upload filesize
    				if (!FileIO.checkFileSize(path)) {
    					Toast.makeText(this, R.string.toast_uploadfilesize, Toast.LENGTH_SHORT).show();
    					return;
    				}
    				//check repeat file
    				if (!DataBaseUtil.checkRepeatFile(FileIO.getMd5(path))) {
    					Toast.makeText(this, R.string.toast_uploadrepeat_file, Toast.LENGTH_SHORT).show();
    					return;
    				}
    				this.currentFilePath = path;
    				LogUtil.v("MainActivity info: ", "get file path: "+path);
    				if (PWApplication.getIsalterupload())
    					openAlterUploadDialog(path);
    				else
    					upload(path);
                }
                break;
        }
    }
    /**调用上传组件*/
    public void upload(String filepath) {
    	this.alterUploadProgress();
    	try {
			PWFileUpload.uploadFile(filepath, progressbar,this);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    /**完成一次文件上传，并添加一个任务*/
    public void onFinishUpload(ResNewTask res) {
    	this.dialog2.dismiss();
    	this.dialog2 = null;
    	if (!res.getStat()) {	//处理上传失败
    		Toast.makeText(this, NetFailStr.getReson(res.getErrcode()), Toast.LENGTH_SHORT).show();
    		return;
    	}
    	
    	addTask(this.currentFilePath,res.getFilecode());
    }
    public void onUploadFail() {
    	this.dialog2.dismiss();
    	this.dialog2 = null;
    	Toast.makeText(this, "网络不给力", Toast.LENGTH_SHORT).show();
    }
    /**添加任务的具体实现方法*/
    public void addTask(String filepath,String filecode) {
    	int filesize = FileIO.getFileSize(filepath);
    	String filemd5 = FileIO.getMd5(filepath);
    	File file = new File(filepath);
    	String filename = file.getName().substring(0,file.getName().length()-4);
    	
    	PWFile object = DataBaseUtil.addFile(filename,filepath, filemd5, filesize, 1, Calendar.getInstance().getTimeInMillis(), filecode);
    	this.mainadapter.addOneTask(object);
    	this.servicebinder.addTask(object);
    }
    /**后台service通知任务状态改变*/
    public void onStatChanged(PWFile object) {
    	DataBaseUtil.updateObject(object);
    	this.mainadapter.updataTaskStat(object);
    }
    /**打开各种对话框*/
    public void openAlterUploadDialog(final String filepath) {
    	Builder builder = new AlertDialog.Builder(this,AlertDialog.THEME_HOLO_LIGHT);	
		View view = LayoutInflater.from(this).inflate(R.layout.dialog_upload, null);
		CheckBox checkbox = (CheckBox)view.findViewById(R.id.dialog_upload_checkbox);
		View btnok = view.findViewById(R.id.dialog_upload_btnok);
		View btncancel = view.findViewById(R.id.dialog_upload_btncancel);
		checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				PWApplication.setIsalterupload(!isChecked);
			}
		});
		btnok.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				dialog2.dismiss();
				dialog2 = null;
				upload(filepath);
			}			
		});
		btncancel.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				dialog2.dismiss();
				dialog2 = null;
			}			
		});
		
		builder.setView(view);
		this.dialog2 = builder.create();
		dialog2.setCanceledOnTouchOutside(true);
		dialog2.show();
    }
    /**打开wifi文件管理*/
    public void openWifi() {
    	Intent intent = new Intent(this,WifiManager.class);
    	this.startActivity(intent);
    }
    /**检查升级*/
	public void checkUpdate() {
		CheckUpdate.check(this);
	}
    /**检查升级网络返回
	 * @throws NameNotFoundException */
	public void onUpdate(ModelAppUpdate m) {
		int currVersionCode = 0;
		try {
			currVersionCode = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		if(currVersionCode > 0 && m.getVersioncode() > currVersionCode) {
			this.m = m;
			alterUpdateDialog();
		} else {
			//已经是最近版本不用处理
//			Toast.makeText(this, "已经是最新版本", Toast.LENGTH_SHORT).show();
		}
	}
	/**弹出升级对话框*/
	public void alterUpdateDialog() {
		Builder builder = new AlertDialog.Builder(this,AlertDialog.THEME_HOLO_LIGHT);	
		View view = LayoutInflater.from(this).inflate(R.layout.dialog_update, null);
		View update = view.findViewById(R.id.dialog_update_update);
		View cancel = view.findViewById(R.id.dialog_update_cancel);
		TextView info = (TextView)view.findViewById(R.id.dialog_update_info);
		info.setText(m.getUpdateinfo());
		update.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				dialog2.dismiss();
				dialog2 = null;
				Intent intent = new Intent(MainActivity.this,UpdateService.class);
				intent.putExtra("Key_App_Name","pdf转word");
				intent.putExtra("Key_Down_Url",m.getDownloadurl());						
				startService(intent);
			}			
		});
		cancel.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				dialog2.dismiss();
				dialog2 = null;
			}			
		});		
		builder.setView(view);
		dialog2 = builder.create();
		dialog2.setCanceledOnTouchOutside(false);
		dialog2.show();	
	}
    @Override
    public void onDestroy() {
    	super.onDestroy();
    	unbindService(serviceconn);
    }
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		this.settingview.setVisibility(View.GONE);
		return false;
	}	
	/**点两次退出*/
	@Override  
    public boolean onKeyDown(int keyCode, KeyEvent event)  
    {  
        if (keyCode == KeyEvent.KEYCODE_BACK )  
        {   
        	if (System.currentTimeMillis() - this.mLastBacktime < 2000){
        		LogUtil.v("MainActivity info: ", "System time: "+System.currentTimeMillis()+ " lastbacktime: "+this.mLastBacktime);
        		this.finish();
        		return false;
        	}
        	else{
        		LogUtil.v("MainActivity info: ", "System time: "+System.currentTimeMillis()+ " lastbacktime: "+this.mLastBacktime);
        		Toast.makeText(this, "再点一次退出", Toast.LENGTH_SHORT).show();
        		this.mLastBacktime = System.currentTimeMillis();
        		return true;
        	}
        } else           
        	return false;           
    }
	/**处理actionbar*/
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected (MenuItem item) {
		if (item.getItemId() == R.id.action_plus) {
			LogUtil.d("MainActivity Menu info: ", "action_plus called!");
			this.showFileChooser();
		}else if (item.getItemId() == R.id.action_ftp) {
			LogUtil.d("MainActivity Menu info: ", "action_ftp called!");
			this.openWifi();
		}else if (item.getItemId() == R.id.action_qa) {
			LogUtil.d("MainActivity Menu info: ", "action_album called!");
			startActivity(new Intent(this,FAQActivity.class));
		} else if (item.getItemId() == R.id.action_about) {
			startActivity(new Intent(MainActivity.this,AboutActivity.class));
		}
		return false;
	}
	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
//		if (featureId == Window.FEATURE_ACTION_BAR && menu != null) {
//			if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
//				try {
//					Method m = menu.getClass().getDeclaredMethod(
//							"setOptionalIconsVisible", Boolean.TYPE);
//					m.setAccessible(true);
//					m.invoke(menu, true);
//				} catch (Exception e) {
//				}
//			}
//		}
		return super.onMenuOpened(featureId, menu);
	}
	/*设置隐藏菜单按钮总是显示*/
	private void setOverflowShowingAlways() {
		try {
			ViewConfiguration config = ViewConfiguration.get(this);
			Field menuKeyField = ViewConfiguration.class
					.getDeclaredField("sHasPermanentMenuKey");
			menuKeyField.setAccessible(true);
			menuKeyField.setBoolean(config, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void testCode() {
		if (LogUtil.isDebug)
			Toast.makeText(this, "debug model", Toast.LENGTH_SHORT).show();
	}
}
