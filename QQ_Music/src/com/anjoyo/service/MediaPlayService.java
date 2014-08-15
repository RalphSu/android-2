package com.anjoyo.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.anjoyo.qq_music.MainPlaying;
import com.anjoyo.qq_music.R;
import com.anjoyo.qq_music.TabHostActivity;
import com.anjoyo.resource.GetMusicData;
import com.anjoyo.resource.MusicUtils;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Binder;
import android.os.IBinder;
import android.widget.RemoteViews;
import android.widget.RemoteViews.RemoteView;
import android.widget.Toast;

public class MediaPlayService extends Service {
	MediaPlayer mediaPlayer = new MediaPlayer();
	IBinder binder = new mBinder();
	String url;
	int pesition, duration, current, progress = -1;
	int mode=3;
	List<Map<String, Object>> list;
	SharedPreferences preferences;
	NotificationManager manager;
	public class mBinder extends Binder {
		public MediaPlayService getService() {
			return MediaPlayService.this;
		}
	}

	public IBinder onBind(Intent intent) {
		Toast.makeText(this, "onbind", Toast.LENGTH_SHORT).show();
		return binder;
	}

	public boolean onUnbind(Intent intent) {
		Toast.makeText(this, "onunbind", Toast.LENGTH_SHORT).show();
		return true;
	}

	public void music_pause() {
		mediaPlayer.pause();
	}

	public void music_play() {
		mediaPlayer.start();
	}

	public void onCreate() {
		super.onCreate();
		Toast.makeText(this, "create", Toast.LENGTH_SHORT).show();
	}

	

	public void onRebind(Intent intent) {

		super.onRebind(intent);
		mediaPlayer.start();
	}

	public void onStart(Intent intent, int startId) {

		super.onStart(intent, startId);
		list = GetMusicData.GetMusicData(this);
		String what = intent.getStringExtra("what");
		pesition = intent.getIntExtra("pesition", -1);
		progress = intent.getIntExtra("seekto", 0);
		url = list.get(pesition).get("URL").toString();
		if (what.equals("play")) {
			play(url);
		} else if (what.equals("pause")) {
			pause();
		} else if (what.equals("restart")) {
			reStart();
		} else if (what.equals("seekto")) {
			seekTo(url, progress);
		}
		playMode_all();

		// 第一次返回播放状态
		new Thread(new Runnable() {

			@Override
			public void run() {
				broadCastState();
				broadCastInfo();
				//mode1();
			}
		}).start();
		// 进度条
		new Thread(new Runnable() {
			public void run() {
				while (mediaPlayer.isPlaying()) {
					Intent intent = new Intent();
					intent.setAction("current");
					current = mediaPlayer.getCurrentPosition();
					intent.putExtra("current", current);
					sendBroadcast(intent);
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}).start();
		
		
		manager=(NotificationManager) getSystemService(this.NOTIFICATION_SERVICE);
		new Thread(new Runnable() {			
		
			public void run() {
				Notification notification=new Notification();
				notification.icon=R.drawable.icon_notification;
				RemoteViews views=new RemoteViews(getPackageName(), R.layout.notification);
				views.setImageViewResource(R.id.notification_image, R.drawable.icon_notification);
				views.setTextViewText(R.id.notification_title,list.get(pesition).get("TITLE").toString());
				views.setTextViewText(R.id.notification_arties, list.get(pesition).get("SINGER").toString());
				notification.contentView=views;
				notification.flags=Notification.FLAG_NO_CLEAR;
				Intent intent=new Intent(MediaPlayService.this, MainPlaying.class);
				PendingIntent pendingIntent=PendingIntent.getActivity(MediaPlayService.this, 0, intent,PendingIntent.FLAG_UPDATE_CURRENT);
				notification.contentIntent=pendingIntent;				
				manager.notify(10, notification);
				
			}
		}).start();
		
		
		// 过滤并且接受广播
		IntentFilter filter = new IntentFilter();
		filter.addAction("UI");
		filter.addAction("UI2");
		filter.addAction("MODE");
		registerReceiver(receiver, filter);

	}

	//接受页面发来的广播重新跟新UI
	BroadcastReceiver receiver = new BroadcastReceiver() {

		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals("UI")) {				
				// 第二次返回播放状态
				broadCastState();
				broadCastInfo();
				
			}
			if (intent.getAction().equals("UI2")) {
				broadCastState();
				broadCastInfo();
				mode1();
				
			}
			if(intent.getAction().equals("MODE")){
				mode=intent.getIntExtra("m1", 0);
				Intent intent2=new Intent();					
				intent.setAction("BTNMODE");
				if(mode==1){					
					intent.putExtra("m", 1);					
				}if(mode==3){
					intent.putExtra("m", 3);
				}if(mode==2){
					intent.putExtra("m", 2);
				}
				sendBroadcast(intent);
			}

		}
	};

	public void play(String url) {

		mediaPlayer.reset();
		try {
			mediaPlayer.setDataSource(url);
			mediaPlayer.prepare();
			mediaPlayer.start();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void pause() {
		mediaPlayer.pause();
	}

	public void reStart() {
		mediaPlayer.start();
	}
	

	public void seekTo(String url, int progress) {

		try {
			mediaPlayer.reset();
			mediaPlayer.setDataSource(url);
			mediaPlayer.prepare();
			mediaPlayer.seekTo(progress);
			mediaPlayer.start();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void broadCastState() {
		if (mediaPlayer.isPlaying()) {
			Intent intent = new Intent();
			intent.setAction("isplay");
			intent.putExtra("state", 1);
			sendBroadcast(intent);
		} else {
			Intent intent = new Intent();
			intent.setAction("isplay");
			intent.putExtra("state", 0);
			sendBroadcast(intent);
		}

	}

	public void broadCastInfo() {
		Intent intent = new Intent();
		intent.setAction("info");
		duration = mediaPlayer.getDuration();
		intent.putExtra("pesition", pesition);
		intent.putExtra("duration", duration);
		sendBroadcast(intent);
	}
	
	public void playMode_all(){
		mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

			public void onCompletion(MediaPlayer mp) {
				if(mode==3){
					pesition++;
					if(pesition==list.size()-1){
						pesition=0;
					}
					play(list.get(pesition).get("URL").toString());	
				}
				if(mode==1){
					play(list.get(pesition).get("URL").toString());	
				}
				if(mode==2){
					Random random=new Random();
					pesition=random.nextInt(list.size()-1);
					play(list.get(pesition).get("URL").toString());	
				}				
				broadCastState();
				broadCastInfo();
				
			}
		});
	}
	public void mode1() {
		Intent intent = new Intent();
		intent.setAction("BTNMODE");		
		intent.putExtra("m",mode);
		sendBroadcast(intent);
		
	}
	public void onDestroy() {

		super.onDestroy();
		mediaPlayer.release();
		NotificationManager manager=(NotificationManager) getSystemService(this.NOTIFICATION_SERVICE);
		manager.cancel(10);
	}

}
