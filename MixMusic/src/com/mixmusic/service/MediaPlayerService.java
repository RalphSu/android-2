package com.mixmusic.service;

import java.io.IOException;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import com.mixmusic.biz.PlayerConfigs;

public class MediaPlayerService extends Service implements Runnable, MediaPlayer.OnCompletionListener {
	/* 定于一个多媒体对象 */
	public static MediaPlayer mMediaPlayer = null;
	// 是否单曲循环
	private static boolean isLoop = false;
	// 用户操作
	private int PLAY_MSG;
	private String PLAY_URL;

	@Override
	public IBinder onBind(Intent intent) {
		return null;// 这里的绑定没的用，上篇我贴出了如何将activity与service绑定的代码
	}

	@Override
	public void onCreate() {
		super.onCreate();
		if (mMediaPlayer != null) {
			mMediaPlayer.reset();
			mMediaPlayer.release();
			mMediaPlayer = null;
		}
		mMediaPlayer = new MediaPlayer();
		/* 监听播放是否完成 */
		mMediaPlayer.setOnCompletionListener(this);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		if (mMediaPlayer != null) {
			mMediaPlayer.stop();
			mMediaPlayer.release();
			mMediaPlayer = null;
		}
	}

	/* 启动service时执行的方法 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		/* 得到从startService传来的动作，后是默认参数，这里是我自定义的常量 */
		PLAY_MSG = intent.getIntExtra("PLAY_MSG", PlayerConfigs.PLAY_MSG);
		PLAY_URL = intent.getStringExtra("PLAY_URL");

		if (PLAY_MSG == PlayerConfigs.PLAY_MSG) {
			playMusic(PLAY_URL);
		}
		if (PLAY_MSG == PlayerConfigs.PAUSE_MSG) {
			if (mMediaPlayer.isPlaying()) {// 正在播放
				mMediaPlayer.pause();// 暂停
			} else {// 没有播放
				mMediaPlayer.start();
			}
		}
		if (PLAY_MSG == PlayerConfigs.STOP_MSG) {
			mMediaPlayer.stop();
		}
		return super.onStartCommand(intent, flags, startId);
	}

	public void playMusic(String playUrl) {
		try {
			/* 重置多媒体 */
			mMediaPlayer.reset();
			/* 读取mp3文件 */
			mMediaPlayer.setDataSource(MediaPlayerService.this,
					Uri.parse("http://dl.118100.cn:9495/res/1388/mp3/00/21/29/1388002129000800.mp3"));
			/* 准备播放 --异步缓存 */
			mMediaPlayer.prepareAsync();
			/* 开始播放 */
			mMediaPlayer.start();
			/* 是否单曲循环 */
			mMediaPlayer.setLooping(isLoop);
			new Thread(this).start();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	// 刷新进度条
	@Override
	public void run() {
		int CurrentPosition = 0;// 设置默认进度条当前位置
		int total = mMediaPlayer.getDuration();//
		while (mMediaPlayer != null && CurrentPosition < total) {
			try {
				Thread.sleep(1000);
				if (mMediaPlayer != null) {
					CurrentPosition = mMediaPlayer.getCurrentPosition();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
	}
}