package com.superpdf2word;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import com.superpdf2word.db.beans.PWFile;
import com.superpdf2word.net.CheckTaskStat;
import com.superpdf2word.net.DownloadDocx;
import com.superpdf2word.tools.LogUtil;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;

public class PWService extends Service
{
	private static final long WAIT_TIME = 5 * 1000;
	private int currentCheckPos = 0;
	private long lastFinishCheckTime = Calendar.getInstance().getTimeInMillis();
	private boolean shouldrunning = true;
	private PWBinder binder = new PWBinder();
	private List<PWFile> tasks = new ArrayList<PWFile>();
	
	public class PWBinder extends Binder
	{
		public MainActivity context;
		public void addTask(PWFile object) {
			LogUtil.v("PWService info: ", "one task add!");
			addOneTask(object);
		}
		
		public void addAllTask(List<PWFile> tasks) {
			addTasks(tasks);
		}
		
		public void finishTask(PWFile object) {
			LogUtil.v("PWService info: ", "one task finished!");
			if (context != null ){
				context.onStatChanged(object);
			}				
		}
	}
	/**增加一个任务*/
	private void addOneTask(PWFile object) {
		this.tasks.add(object);
	}
	/**增加多个任务，用于在启动时一次性导入任务*/
	private void addTasks(List<PWFile> tasks) {
		for (int i=0;i<tasks.size();i++){
			PWFile object = tasks.get(i);
			if (object.getStat() != 4)
				this.tasks.add(object);
		}
		startMonitor();
	}
	/**移除一个任务*/
	private void removeTask(String filecode) {
		Iterator<PWFile> it = this.tasks.iterator();
		while(it.hasNext()) {
			PWFile object = it.next();
			if (filecode.equals(object.getFilecode()))
				it.remove();
		}
	}
	/**开始下一个任务*/
	private void startCheckNextTask() {
		LogUtil.d("PWService startTask info: 	", "currentCheckPos: "+currentCheckPos+" tasksize: "+tasks.size() + " shouldrunning: "+this.shouldrunning);
		if (!this.shouldrunning)
			return;
			
		if (this.currentCheckPos < this.tasks.size()) {
			CheckTaskStat.check(this.tasks.get(currentCheckPos).getFilecode(), PWService.this);			
		} else {
			LogUtil.d("PWService info: ", "finish one scan, take a rest!");
			currentCheckPos = 0;
			this.lastFinishCheckTime = Calendar.getInstance().getTimeInMillis();
			new Handler().postDelayed(new Runnable(){
				@Override
				public void run() {
					startCheckNextTask();
				}
				
			}, WAIT_TIME);
		}
	}
	/**检查状态网络返回*/
	public void onCheckStat(String filecode,int stat) {
		LogUtil.v("PWService info: ", "finish check one task! start next!");
		if (stat == 2) {	//转换完成，等待下载
			downLoadFile(filecode);
		} else if (stat == 6){	//转换失败
			LogUtil.v("PWService info: ", "convert fail! filecode: "+filecode);
			PWFile object = getTask(filecode);
			object.setStat(6);
			this.binder.context.onStatChanged(object);
			this.removeTask(filecode);
		}
		
		this.currentCheckPos++;
		startCheckNextTask();
	}
	/**开始监控任务状态入口*/
	public void startMonitor() {
		startCheckNextTask();
	}
	/**下载一个文件*/
	public void downLoadFile(String filecode) {
		File targetfile = prepareDown(filecode);
		DownloadDocx.down(filecode, this, targetfile);
		
		PWFile object = getTask(filecode);
		object.setStat(3);	//正在下载
		this.binder.context.onStatChanged(object);
	}
	/**返回即将写入本地的文件*/
	public File prepareDown(String filecode) {
		File file = PWApplication.getContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
		LogUtil.v("PWService dir info: ", file.getPath());
		
		if (!file.exists())
			file.mkdirs();
		PWFile task = this.getTask(filecode);
		String path = file.getPath() + "/" + task.getFilename() + ".docx";
		task.setWordpath(path);
		return new File(path);
	}
	/**下载文件完成返回*/
	public void onFinishDownload(String filecode,boolean stat) {
		if (stat) {
			LogUtil.v("PWService info: ", "finish a task "+filecode);
			PWFile object = getTask(filecode);
			object.setStat(4);	//下载成功
			this.binder.context.onStatChanged(object);
			this.removeTask(filecode);
		} else {
			PWFile object = getTask(filecode);
			object.setStat(5);	//下载失败
			this.binder.context.onStatChanged(object);
			this.removeTask(filecode);
		}
		
	}
	
	/**根据filecode返回一个tasks*/
	public PWFile getTask(String filecode) {
		PWFile object = null;
		for (int i=0;i<tasks.size();i++){
			PWFile f = tasks.get(i);
			if (filecode.equals(tasks.get(i).getFilecode()))
				object = f;
		}
		return object;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		System.out.println("Service onBind!");
		return binder;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		System.out.println("Service onCreate!");
	}

	@Override
	public boolean onUnbind(Intent intent) {
		this.shouldrunning = false;
		this.tasks.removeAll(tasks);
		return false;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
}