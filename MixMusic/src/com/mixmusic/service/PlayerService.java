package com.mixmusic.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import com.mixmusic.biz.AppConstant;

public class PlayerService extends Service {

	private boolean isPlaying, isPause, isResleased = false;
	private MediaPlayer mediaPlayer = null;

	@Override
	public IBinder onBind(Intent arg0) {

		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		String PLAY_URL = intent.getStringExtra("PLAY_URL");
		int PLAY_MSG = intent.getIntExtra("PLAY_MSG", 0);

		if (PLAY_URL != null) {
			if (PLAY_MSG == AppConstant.PlayerMsg.PLAY_MSG) {
				play(PLAY_URL);

			} else {
				if (PLAY_MSG == AppConstant.PlayerMsg.PAUSE_MSG) {
					pause();
				} else if (PLAY_MSG == AppConstant.PlayerMsg.STOP_MSG) {
					stop();
				}
			}
		} else if (PLAY_MSG == 3) {
			stop();
		}
		return super.onStartCommand(intent, flags, startId);
	}

	private void play(String url) {
		if (isPlaying) {
			if (!isResleased) {
				mediaPlayer.stop();
				// 释放资源
				mediaPlayer.release();
			}
		}
		mediaPlayer = MediaPlayer.create(this, Uri.parse(url));
		
		mediaPlayer.setLooping(true);
		mediaPlayer.start();
		isPlaying = true;
		isResleased = false;
		isPause = false;
	}

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

	@Override
	public void onDestroy() {

		super.onDestroy();
	}

}
