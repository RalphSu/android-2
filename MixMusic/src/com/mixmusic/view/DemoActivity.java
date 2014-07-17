package com.mixmusic.view;

import java.io.File;

import com.mixmusic.R;
import com.mixmusic.record.AudioFileFunc;
import com.mixmusic.record.ExtAudioRecorder;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class DemoActivity extends Activity implements OnClickListener {

	private Context mContext;
	private Button btn_start, btn_stop;

	ExtAudioRecorder extAudioRecorder = ExtAudioRecorder.getInstanse(false);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.demo);
		mContext = DemoActivity.this;
		btn_start = (Button) findViewById(R.id.btn_start);
		btn_stop = (Button) findViewById(R.id.btn_stop);

		btn_start.setOnClickListener(this);
		btn_stop.setOnClickListener(this);

	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.btn_start:
			String filePath = Environment.getExternalStorageDirectory()
					.getAbsolutePath() + "/" ;
			String fileName= "demo.wav";
			recordChat(extAudioRecorder,filePath,fileName);
			break;

		case R.id.btn_stop:
			stopRecord(extAudioRecorder);
			break;
		}
	}

	/**
	 * ¼��wav��ʽ�ļ�
	 * 
	 * @param path
	 *            : �ļ�·��
	 */
	public static File recordChat(ExtAudioRecorder extAudioRecorder,
			String savePath, String fileName) {
		File dir = new File(savePath);
		// �����Ŀ¼û�д��ڣ����½�Ŀ¼
		if (dir.list() == null) {
			dir.mkdirs();
		}
		// ��ȡ¼���ļ�
		File file = new File(savePath + fileName);
		// ��������ļ�
		extAudioRecorder.setOutputFile(savePath + fileName);
		extAudioRecorder.prepare();
		// ��ʼ¼��
		extAudioRecorder.start();
		return file;
	}

	/**
	 * ֹͣ¼��
	 * 
	 * @param mediaRecorder
	 *            ��ֹͣ��¼����
	 * @return ����
	 */
	public static void stopRecord(final ExtAudioRecorder extAudioRecorder) {
		extAudioRecorder.stop();
		extAudioRecorder.release();
	}
}
