package com.recordmix.services;

import java.io.IOException;

import com.recordmix.biz.PlayerConfigs;
import com.recordmix.utils.DialogUtil;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.IBinder;

public class PlayerService extends Service {

	private boolean isPlaying, isPause, isResleased = false;
	private MediaPlayer mediaPlayer;

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

//		MediaPlayer m  = MediaPlayer.create(this, Uri.parse("http://118.85.203.37:9495/res/1645/mp3/00/02/41/1645000241001600.mp3"));
//		m.start();
//		 try {
//			 MediaPlayer  mediaPlayer = new MediaPlayer();
//             // 设置指定的流媒体地址
//             mediaPlayer.setDataSource("http://118.85.203.37:9495/res/1645/mp3/00/02/41/1645000241001600.mp3");
//             // 设置音频流的类型
//             mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//
//             // 通过异步的方式装载媒体资源
//             mediaPlayer.prepareAsync();
//             mediaPlayer.start();
//           
//		} catch (Exception e) {
//			// TODO: handle exception
//			e.printStackTrace();
//		}
//	
		
		String PLAY_URL = intent.getStringExtra("PLAY_URL");
		int PLAY_MSG = intent.getIntExtra("PLAY_MSG", 0);

		System.out.println("------PLAY_URL:"+PLAY_URL+"------PLAY_MSG:"+PLAY_MSG);
		// 判断播放连接是否为空
		if (PLAY_URL != null) {
			// 判断播放状态
			if (PLAY_MSG == PlayerConfigs.PLAY_MSG) {
				play(PLAY_URL);

			} else {
				if (PLAY_MSG == PlayerConfigs.PAUSE_MSG) {
					pause();
				} else if (PLAY_MSG == PlayerConfigs.STOP_MSG) {
					stop();
				}
			}
		}
		return super.onStartCommand(intent, flags, startId);
	}
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		mediaPlayer.release();
		super.onDestroy();
	}

	/**
	 * 播放音乐
	 * 
	 * @param url
	 */
	private void play(String url) {
		System.out.println("-----url----"+url);

		mediaPlayer = MediaPlayer.create(this, Uri.parse(url));
		mediaPlayer.setLooping(false);
		mediaPlayer.start();
		isPlaying = true;
		isResleased = false;
		isPause = false;
		 
	}


	/**
	 * 暂停播放
	 */
	private void pause() {
		if (mediaPlayer != null) {
			if (!isResleased) {
				if (!isPause) {
					mediaPlayer.pause();
					isPause = true;
					isPlaying = false;
					isResleased = false;
				} else {
					mediaPlayer.start();
					isPlaying = true;
					isPause = false;
					isResleased = false;
				}
			}
		}
	}

	/**
	 * 停止播放 
	 */
	private void stop() {
		if (mediaPlayer != null) {
			if (isPlaying) {
				if (!isResleased) {
					mediaPlayer.stop();
					// 释放资源
					mediaPlayer.release();
					isPlaying = false;
					isPause = false;
					isResleased = true;
				}
			}
		}
	}
}
