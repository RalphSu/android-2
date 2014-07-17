package com.mixmusic.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.mixmusic.biz.PlayerConfigs;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;

public class MediaPlayerService extends Service implements Runnable,
		MediaPlayer.OnCompletionListener {
	/* ����һ����ý����� */
	public static MediaPlayer mMediaPlayer = null;
	// �Ƿ���ѭ��
	private static boolean isLoop = false;
	// �û�����
	private int PLAY_MSG;
	private String PLAY_URL;

	@Override
	public IBinder onBind(Intent intent) {
		return null;// ����İ�û���ã���ƪ����������ν�activity��service�󶨵Ĵ���
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
		/* ���������Ƿ���� */
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

		System.out.println("service onDestroy");
	}

	/* ����serviceʱִ�еķ��� */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		/* �õ���startService�����Ķ���������Ĭ�ϲ��������������Զ���ĳ��� */
		PLAY_MSG = intent.getIntExtra("PLAY_MSG", PlayerConfigs.PLAY_MSG);
		PLAY_URL = intent.getStringExtra("PLAY_URL");

		if (PLAY_MSG == PlayerConfigs.PLAY_MSG) {
			playMusic(PLAY_URL);
		}
		if (PLAY_MSG == PlayerConfigs.PAUSE_MSG) {
			if (mMediaPlayer.isPlaying()) {// ���ڲ���
				mMediaPlayer.pause();// ��ͣ
			} else {// û�в���
				mMediaPlayer.start();
			}
		}
		if (PLAY_MSG == PlayerConfigs.STOP_MSG) 
		{
			mMediaPlayer.stop();
		}
		return super.onStartCommand(intent, flags, startId);
	}

	@SuppressWarnings("static-access")
	public void playMusic(String playUrl) {
		try {
			/* ���ö�ý�� */
			mMediaPlayer.reset();
			/* ��ȡmp3�ļ� */
			mMediaPlayer.setDataSource(MediaPlayerService.this,
					Uri.parse("http://dl.118100.cn:9495/res/1388/mp3/00/21/29/1388002129000800.mp3"));
//			mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			
			/* ׼������ --�첽����*/
			mMediaPlayer.prepareAsync();  
//			File file = new File(playUrl); 
//			FileInputStream fis = new FileInputStream(file); 
//			mMediaPlayer.setDataSource(fis.getFD()); 
//			mMediaPlayer.prepare();
			/* ��ʼ���� */
			mMediaPlayer.start();
			/* �Ƿ���ѭ�� */
			mMediaPlayer.setLooping(isLoop);
			// ���ý��������ֵ
			// TestMediaPlayer.audioSeekBar.setMax(PlayerService.mMediaPlayer
			// .getDuration());
			new Thread(this).start();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	// ˢ�½�����
	@Override
	public void run() {
		int CurrentPosition = 0;// ����Ĭ�Ͻ�������ǰλ��
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
			// TestMediaPlayer.audioSeekBar.setProgress(CurrentPosition);
		}

	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		/* �����굱ǰ�������Զ�������һ�� */

		// if (++TestMediaPlayer.currentListItme >= TestMediaPlayer.mMusicList
		// .size()) {
		// Toast.makeText(PlayerService.this, "�ѵ����һ�׸���", Toast.LENGTH_SHORT)
		// .show();
		// TestMediaPlayer.currentListItme--;
		// TestMediaPlayer.audioSeekBar.setMax(0);
		// } else {
		// playMusic();
		// }
	}
}