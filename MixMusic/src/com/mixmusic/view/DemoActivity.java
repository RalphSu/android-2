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
	 * 录制wav格式文件
	 * 
	 * @param path
	 *            : 文件路径
	 */
	public static File recordChat(ExtAudioRecorder extAudioRecorder,
			String savePath, String fileName) {
		File dir = new File(savePath);
		// 如果该目录没有存在，则新建目录
		if (dir.list() == null) {
			dir.mkdirs();
		}
		// 获取录音文件
		File file = new File(savePath + fileName);
		// 设置输出文件
		extAudioRecorder.setOutputFile(savePath + fileName);
		extAudioRecorder.prepare();
		// 开始录音
		extAudioRecorder.start();
		return file;
	}

	/**
	 * 停止录音
	 * 
	 * @param mediaRecorder
	 *            待停止的录音机
	 * @return 返回
	 */
	public static void stopRecord(final ExtAudioRecorder extAudioRecorder) {
		extAudioRecorder.stop();
		extAudioRecorder.release();
	}
}
