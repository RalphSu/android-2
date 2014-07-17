package com.mixmusic.record;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.media.MediaRecorder;
import android.os.Environment;

public class AudioFileFunc {
	// ��Ƶ����-��˷�
	public final static int AUDIO_INPUT = MediaRecorder.AudioSource.MIC;

	// ����Ƶ��
	// 44100��Ŀǰ�ı�׼������ĳЩ�豸��Ȼ֧��22050��16000��11025
	public final static int AUDIO_SAMPLE_RATE = 8000; // 44.1KHz,�ձ�ʹ�õ�Ƶ��
	// ¼������ļ�
	private final static String AUDIO_RAW_FILENAME = "RawAudio.raw";
	private final static String AUDIO_WAV_FILENAME = getFileName("wav");
	public final static String AUDIO_AMR_FILENAME = getFileName("amr");

	/**
	 * ����¼���ļ�
	 * @param pfx
	 * @return
	 */
	public static String getFileName(String pfx) {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
		String savePath = sdf.format(date) + "." + pfx;
		return savePath;
	}

	/**
	 * �ж��Ƿ����ⲿ�洢�豸sdcard
	 * 
	 * @return true | false
	 */
	public static boolean isSdcardExit() {
		if (Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED))
			return true;
		else
			return false;
	}

	/**
	 * ��ȡ��˷������ԭʼ��Ƶ���ļ�·��
	 * 
	 * @return
	 */
	public static String getRawFilePath() {
		String mAudioRawPath = "";
		if (isSdcardExit()) {
			String fileBasePath = Environment.getExternalStorageDirectory()
					.getAbsolutePath();
			mAudioRawPath = fileBasePath + "/" + AUDIO_RAW_FILENAME;
		}

		return mAudioRawPath;
	}

	/**
	 * ��ȡ������WAV��ʽ��Ƶ�ļ�·��
	 * 
	 * @return
	 */
	public static String getWavFilePath() {
		String mAudioWavPath = "";
		if (isSdcardExit()) {
			String fileBasePath = Environment.getExternalStorageDirectory().getAbsolutePath()
					+ "/MixMusic/files";
			// ����Ŀ¼
			File savePathDir = new File(fileBasePath);
			if (!savePathDir.exists()) {
				savePathDir.mkdirs();
			}
			mAudioWavPath = fileBasePath + "/" + AUDIO_WAV_FILENAME;
		}
		return mAudioWavPath;
	}

	/**
	 * ��ȡ������AMR��ʽ��Ƶ�ļ�·��
	 * 
	 * @return
	 */
	public static String getAMRFilePath() {
		String mAudioAMRPath = "";
		if (isSdcardExit()) {
			String fileBasePath = Environment.getExternalStorageDirectory().getAbsolutePath()
					+ "/MixMusic/files";
			// ����Ŀ¼
			File savePathDir = new File(fileBasePath);
			if (!savePathDir.exists()) {
				savePathDir.mkdirs();
			}
			mAudioAMRPath = fileBasePath + "/" + AUDIO_AMR_FILENAME;
		}
		return mAudioAMRPath;
	}

	/**
	 * ��ȡ�ļ���С
	 * 
	 * @param path
	 *            ,�ļ��ľ���·��
	 * @return
	 */
	public static long getFileSize(String path) {
		File mFile = new File(path);
		if (!mFile.exists())
			return -1;
		return mFile.length();
	}

}