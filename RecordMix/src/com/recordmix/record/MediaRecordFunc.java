package com.recordmix.record;

import java.io.File;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.media.MediaRecorder;

public class MediaRecordFunc {
	private boolean isRecord = false;

	private MediaRecorder mMediaRecorder;
	private File audioFile;

	private MediaRecordFunc() {
	}

	private static MediaRecordFunc mInstance;

	public synchronized static MediaRecordFunc getInstance() {
		if (mInstance == null)
			mInstance = new MediaRecordFunc();
		return mInstance;
	}

	public int startRecordAndFile() {
		// 判断是否有外部存储设备sdcard
		if (AudioFileFunc.isSdcardExit()) {
			if (isRecord) {
				return ErrorCode.E_STATE_RECODING;
			} else {
				if (mMediaRecorder == null)
					createMediaRecord();

				try {
					mMediaRecorder.prepare();
					mMediaRecorder.start();
					// 让录制状态为true
					isRecord = true;
					return ErrorCode.SUCCESS;
				} catch (IOException ex) {
					ex.printStackTrace();
					return ErrorCode.E_UNKOWN;
				}
			}

		} else {
			return ErrorCode.E_NOSDCARD;
		}
	}

	public void stopRecordAndFile() {
		close();
	}

	public long getRecordFileSize() {
		return AudioFileFunc.getFileSize(AudioFileFunc.getAMRFilePath());
	}

	@SuppressLint("InlinedApi")
	private void createMediaRecord() {
		/* ①Initial：实例化MediaRecorder对象 */
		mMediaRecorder = new MediaRecorder();

		/* setAudioSource/setVedioSource */
		mMediaRecorder.setAudioSource(AudioFileFunc.AUDIO_INPUT);// 设置麦克风

		/*
		 * 设置输出文件的格式：THREE_GPP/MPEG-4/RAW_AMR/Default
		 * THREE_GPP(3gp格式，H263视频/ARM音频编码)、MPEG-4、RAW_AMR(只支持音频且音频编码要求为AMR_NB)
		 */
		mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
		/*
		 * 设置采样率
		 */
		mMediaRecorder.setAudioSamplingRate(16000);
		/* 设置音频文件的编码：AAC/AMR_NB/AMR_MB/Default */
		mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);

		/* 设置输出文件的路径 */
		audioFile = new File(AudioFileFunc.getAMRFilePath());

		mMediaRecorder.setOutputFile(AudioFileFunc.getAMRFilePath());
	}

	/**
	 * 关闭录制
	 */
	private void close() {
		if (mMediaRecorder != null) {
			System.out.println("stopRecord");
			isRecord = false;
			mMediaRecorder.stop();
			mMediaRecorder.release();
			mMediaRecorder = null;
		}
	}

	/**
	 * 获取音频文件
	 * 
	 * @return
	 */
	public File getAudioFile() {
		return audioFile;
	}
}