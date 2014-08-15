package com.anjoyo.netmusic;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.anjoyo.qq_music.R;
import com.anjoyo.qq_music.TabHostActivity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.RemoteViews;
import android.widget.Toast;

public class DownLoadService extends Service {
	// 通知栏
		private NotificationManager updateNotificationManager = null;
		private Notification updateNotification = null;
		/*
		 * http://kongweile.iteye.com/blog/1133013
		 * http://blog.csdn.net/wdaming1986/article/details/7038402
		 * 
		 * RemoteView描述一个view,而这个view是在另外一个进程显示的。
		 * 它inflate于layout资源文件。并且提供了可以修改过view内容的一些简单基础的操作。
		 */
		RemoteViews view = null;
		// 通知栏跳转Intent
		private Intent updateIntent = null;
		private PendingIntent updatePendingIntent = null;

		MusicModel musicHs;

		@Override
		public IBinder onBind(Intent intent) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void onCreate() {
			// TODO Auto-generated method stub
			super.onCreate();
		}

		@Override
		public int onStartCommand(Intent intent, int flags, int startId) {
			musicHs = (MusicModel) intent.getSerializableExtra("musicInfo");
			
			// 得到序列化对象
			// 获得系统通知管理服务updateNotificationManager
			this.updateNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			// 创建一个Notification
			this.updateNotification = new Notification();

			// 设置下载过程中，点击通知栏，回到主界面
			updateIntent = new Intent(this, TabHostActivity.class);
			// Activity的启动模式：
			// http://www.open-open.com/lib/view/open1345173373350.html
			updateIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			updatePendingIntent = PendingIntent.getActivity(this, 0, updateIntent,
					0);
			// 设置通知图标
			updateNotification.icon = R.drawable.ic_launcher;
			/*
			 * notification.contentIntent:一个PendingIntent对象，当用户点击了状态栏上的图标时，
			 * 该Intent会被触发 * notification.contentView:我们可以不在状态栏放图标而是放一个view *
			 * notification.deleteIntent 当当前notification被移除时执行的intent *
			 * notification.vibrate 当手机震动时，震动周期设置
			 * 
			 * http://www.cnblogs.com/zenfly/archive/2012/02/09/2343923.html
			 */
			updateNotification.contentIntent = updatePendingIntent;

			view = new RemoteViews(getPackageName(), R.layout.dowanload_notification);
			view.setProgressBar(R.id.pb, 100, 0, false);
			view.setTextViewText(R.id.persent, "下载0%");
			view.setTextViewText(R.id.mname, "");
			updateNotification.contentView = view;

			// 设置通知栏显示内容
			updateNotification.tickerText = "正在下载：" + musicHs.getMp3Name();
			
			// 发出通知
			updateNotificationManager.notify(0, updateNotification);

			new Thread(runn).start();// 下载的线程开启

			return super.onStartCommand(intent, flags, startId);
		}

		Handler downHandler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 0:
					if (msg.arg1 == 0) {
						updateNotificationManager.cancel(0);
						Toast.makeText(getApplicationContext(), "下载完成", 3000)
								.show();
						// 停止服务
						DownLoadService.this.stopSelf();
					} else if (msg.arg1 == 1) {
						updateNotificationManager.cancel(0);
						Toast.makeText(getApplicationContext(), "文件已存在，无需下载", 3000)
								.show();
					}

					break;

				case 1:
					view.setProgressBar(R.id.pb, 100, msg.arg1, false);
					view.setTextViewText(R.id.persent, msg.arg1 + "%");

					// 获得当前正在下载的文件名
					String name = musicHs.getMp3Name();
					view.setTextViewText(R.id.mname, name);
					updateNotification.contentIntent = updatePendingIntent;
					updateNotificationManager.notify(0, updateNotification);
					break;

				default:
					break;
				}

			};

		};

		Runnable runn = new Runnable() {

			@Override
			public void run() {
				/************************ 下载歌曲 *****************************/
				String downPath = "http://192.168.133.211:9999/music/songs/"
						+ musicHs.getMp3FileName();

				System.out.println("路径----：" +"http://192.168.133.211:9999/music/songs/" + "歌名-----："
						+ musicHs.getMp3FileName());
				// 创建一个空文件（或自命名一个实名文件夹，eg:"down"）夹存放下载歌曲
				int reuslt = DownloadFile(downPath, "", musicHs.getMp3FileName());
				System.out.println("00dasd000---------" + musicHs);
				System.out.println("000000---------" + reuslt);
				Message msg = downHandler.obtainMessage();
				msg.what = 0;
				msg.arg1 = reuslt;
				msg.sendToTarget();

			}
		};

		boolean isstart = true;

		/**
		 * 注意：如果需要写入sd卡，必须在主配置文件下声明权限 下载步骤 1.获得或者是确定下载的网路路径
		 * 2.获得字节流（url，HttpURLConnection，inputStream） 3.确定下载路径（创建你所需要的文件夹）
		 * 4.创建空文件（空文件必须和服务端的文件格式匹配，后缀名必须一样） 5.需要用一个循环去读取第二步获得的字节流
		 * 并将读取的内容写入第四步创建的空文件里
		 * 
		 * @param fileName
		 * @return
		 */
		public int DownloadFile(String filel, String path, String fileName) {
			InputStream inputStream = null;
			OutputStream outputStream = null;
			File file = null;

			try {
				// 实例化SD卡操作类
				FileToSdcard fileUtils = new FileToSdcard();
				// 判断文件是否存在
				if (fileUtils.IsFileExists(path + "/" + fileName)) {

					return 1;
				}
				// 2、得到字节流对象
				URL url = new URL(filel);
				HttpURLConnection httpconn = (HttpURLConnection) url
						.openConnection();
				inputStream = httpconn.getInputStream();

				// 3、在SD卡上创建文件夹
				fileUtils.CreateSDDir(path);

				// 4、在SD卡上创建文件
				file = fileUtils.CreateSDFile(path + "/" + fileName);

				outputStream = new FileOutputStream(file);
				// 5、读取流并设置读取速率
				byte buffer[] = new byte[10 * 1024];
				int length = 0;
				int tol = 0;
				int totalSize = httpconn.getContentLength();
				while ((length = inputStream.read(buffer)) != -1) {
					tol += length;
					// 写
					outputStream.write(buffer, 0, length);

					int downPer = tol * 100 / totalSize;

					if (downPer % 2 == 0 && isstart) {
						Message msg = downHandler.obtainMessage();
						msg.arg1 = downPer;
						msg.what = 1;
						msg.sendToTarget();
						isstart = false;
					}
					if (downPer % 2 == 1) {
						isstart = true;
					}

					Thread.sleep(300);

				}
				// 将缓存区的数据发送出去
				outputStream.flush();
			} catch (Exception e) {
				e.printStackTrace();
				return -1;
			} finally {
				try {
					// 关闭流对象
					inputStream.close();
					outputStream.close();
				} catch (Exception e2) {
					// TODO: handle exception
					e2.printStackTrace();
				}
			}
			return 0;

		}


}
