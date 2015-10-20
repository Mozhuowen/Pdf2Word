package com.superpdf2word;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.superpdf2word.tools.FileUtil;
import com.superpdf2word.tools.LogUtil;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;
import android.widget.Toast;

public class UpdateService extends Service {

	public static final String Install_Apk = "Install_Apk";
	/********download progress step*********/
	private static final int down_step_custom = 3;

	private static final int TIMEOUT = 10 * 1000;// ��ʱ
	private static String down_url;
	private static final int DOWN_OK = 1;
	private static final int DOWN_ERROR = 0;

	private String app_name;

	private NotificationManager notificationManager;
	private Notification notification;
	private Intent updateIntent;
	private PendingIntent pendingIntent;
	private RemoteViews contentView;


	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		app_name = intent.getStringExtra("Key_App_Name");
		down_url = intent.getStringExtra("Key_Down_Url");

		FileUtil.createFile(this,app_name);

		if(FileUtil.isCreateFileSucess == true){
			createNotification();
			createThread();
		}else{
			Toast.makeText(this, "存储空间不足", Toast.LENGTH_SHORT).show();
			/***************stop service************/
			stopSelf();

		}

		return super.onStartCommand(intent, flags, startId);
	}



	/********* update UI******/
	private final Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case DOWN_OK:

					Uri uri = Uri.fromFile(FileUtil.updateFile);
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setDataAndType(uri,"application/vnd.android.package-archive");
					pendingIntent = PendingIntent.getActivity(UpdateService.this, 0, intent, 0);

					notification.flags = Notification.FLAG_AUTO_CANCEL;
//				notification.setLatestEventInfo(UpdateService.this,app_name, getString(R.string.down_sucess), pendingIntent);
					Notification.Builder builder = new Notification.Builder(UpdateService.this);
					builder.setContentTitle(app_name);
					builder.setContentText(getString(R.string.down_sucess));
					builder.setContentIntent(pendingIntent);
					builder.setPublicVersion(notification);
					//notification.setLatestEventInfo(UpdateService.this,app_name, app_name + getString(R.string.down_sucess), null);
					notificationManager.notify(R.layout.notification_item, notification);

					installApk();

					//stopService(updateIntent);
					/***stop service*****/
					stopSelf();
					break;

				case DOWN_ERROR:
					notification.flags = Notification.FLAG_AUTO_CANCEL;
					//notification.setLatestEventInfo(UpdateService.this,app_name, getString(R.string.down_fail), pendingIntent);
//				notification.setLatestEventInfo(UpdateService.this,app_name, getString(R.string.down_fail), null);
					Notification.Builder buildererr = new Notification.Builder(UpdateService.this);
					buildererr.setContentTitle(app_name);
					buildererr.setContentText(getString(R.string.down_fail));
					buildererr.setPublicVersion(notification);

					/***stop service*****/
					//onDestroy();
					stopSelf();
					break;

				default:
					//stopService(updateIntent);
					/******Stop service******/
					//stopService(intentname)
					//stopSelf();
					break;
			}
		}
	};

	private void installApk() {
		// TODO Auto-generated method stub
		Uri uri = Uri.fromFile(FileUtil.updateFile);
		Intent intent = new Intent(Intent.ACTION_VIEW);

		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		intent.setDataAndType(uri,"application/vnd.android.package-archive");
		UpdateService.this.startActivity(intent);
	}

	public void createThread() {
		new DownLoadThread().start();
	}


	private class DownLoadThread extends Thread{
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Message message = new Message();
			try {
				long downloadSize = downloadUpdateFile(down_url,FileUtil.updateFile.toString());
				if (downloadSize > 0) {
					// down success
					message.what = DOWN_OK;
					handler.sendMessage(message);
				}
			} catch (Exception e) {
				e.printStackTrace();
				message.what = DOWN_ERROR;
				handler.sendMessage(message);
			}
		}
	}



	public void createNotification() {
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
		mBuilder.setContentTitle("边城")//设置通知栏标题
				.setContentText("测试内容") //设置通知栏显示内容
//	    .setContentIntent(getDefalutIntent(Notification.FLAG_AUTO_CANCEL)) //设置通知栏点击意图
						//  .setNumber(number) //设置通知集合的数量
				.setTicker("边城正在升级") //通知首次出现在通知栏，带上升动画效果的
				.setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
				.setPriority(Notification.PRIORITY_DEFAULT) //设置该通知优先级
//	    .setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
				.setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
//	    .setDefaults(Notification.DEFAULT_VIBRATE)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
						//Notification.DEFAULT_ALL  Notification.DEFAULT_SOUND 添加声音 // requires VIBRATE permission
				.setSmallIcon(R.drawable.ic_logo);//设置通知小ICON

		notification = mBuilder.build();

		notification.flags = Notification.FLAG_ONGOING_EVENT;
		//notification.flags = Notification.FLAG_AUTO_CANCEL;

		/*** 自定义  Notification 的显示****/
		contentView = new RemoteViews(getPackageName(),R.layout.notification_item);
		contentView.setTextViewText(R.id.notificationTitle, app_name + getString(R.string.is_downing));
		contentView.setTextViewText(R.id.notificationPercent, "0%");
		contentView.setProgressBar(R.id.notificationProgress, 100, 0, false);
		notification.contentView = contentView;


		notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(R.layout.notification_item, notification);
	}

	public long downloadUpdateFile(String down_url, String file)throws Exception {
		LogUtil.v("Begin to update: ", down_url);

		int down_step = down_step_custom;
		int totalSize;
		int downloadCount = 0;
		int updateCount = 0;

		InputStream inputStream;
		OutputStream outputStream;

		URL url = new URL(down_url);
		HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
		httpURLConnection.setConnectTimeout(TIMEOUT);
		httpURLConnection.setReadTimeout(TIMEOUT);
		totalSize = httpURLConnection.getContentLength();

		if (httpURLConnection.getResponseCode() == 404) {
			LogUtil.v("UpdateService info:", "download 404");
			throw new Exception("fail!");
		}

		inputStream = httpURLConnection.getInputStream();
		outputStream = new FileOutputStream(file, false);// �ļ������򸲸ǵ�

		byte buffer[] = new byte[1024];
		int readsize = 0;

		while ((readsize = inputStream.read(buffer)) != -1) {

			outputStream.write(buffer, 0, readsize);
			downloadCount += readsize;
			if (updateCount == 0 || ((downloadCount * 100 / totalSize) - down_step) >= updateCount) {
				updateCount += down_step;
				contentView.setTextViewText(R.id.notificationPercent,updateCount + "%");
				contentView.setProgressBar(R.id.notificationProgress, 100,updateCount, false);
				notification.contentView = contentView;
				notificationManager.notify(R.layout.notification_item, notification);
			}
		}
		if (httpURLConnection != null) {
			httpURLConnection.disconnect();
		}
		inputStream.close();
		outputStream.close();

		return downloadCount;
	}

}