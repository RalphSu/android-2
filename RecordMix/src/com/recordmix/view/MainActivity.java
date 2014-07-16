package com.recordmix.view;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.recordmix.R;
import com.recordmix.biz.PlayerConfigs;
import com.recordmix.biz.RecordMixManager;
import com.recordmix.services.PlayerService;
import com.recordmix.utils.DialogUtil;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener {

	private Context mContext;
	private TextView stateView, txt_time;
	private Spinner spinner;
	private Button btnStart, btnStop, btnPlay, btnFinish, btnMixStart,
			btnMixPause, btnMixStop, btn_reMix;
	private ArrayAdapter<String> adapter;
	private List<String> nameList = new ArrayList<String>();
	private List<String> idList = new ArrayList<String>();
	private RecordTask recorder;
	private PlayTask player;
	private Handler mHandler, resultHandler;
	private File audioFile;
	private String selectId = "1";
	private boolean isRecording = true, isPlaying = false; // 标记

	private int frequence = 16000; // 录制频率，单位hz.这里的值注意了，写的不好，可能实例化AudioRecord对象的时候，会出错。我开始写成11025就不行。这取决于硬件设备
	@SuppressWarnings("deprecation")
	private int channelConfig = AudioFormat.CHANNEL_CONFIGURATION_MONO;
	private int audioEncoding = AudioFormat.ENCODING_PCM_8BIT;
	private int bufferSize = 0;
	private String playUrl;// 播放地址
	private Intent playerIntent; // 播放器Intent

	private Date startTime, endTime;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH—mm-ss");

	public static String mixId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mContext = MainActivity.this;
		initView();
		initHandler();
		initData();

	}

	void initView() {
		stateView = (TextView) findViewById(R.id.view_state);
		stateView.setText("准备开始");
		txt_time = (TextView) findViewById(R.id.txt_time);
		spinner = (Spinner) findViewById(R.id.spinner);
		btnStart = (Button) findViewById(R.id.btn_start);
		btnStop = (Button) findViewById(R.id.btn_stop);
		btnPlay = (Button) findViewById(R.id.btn_play);
		btnFinish = (Button) findViewById(R.id.btn_finish);
		btnMixStart = (Button) findViewById(R.id.btn_mixStart);
		btnMixPause = (Button) findViewById(R.id.btn_mixPause);
		btnMixStop = (Button) findViewById(R.id.btn_mixStop);
		btn_reMix = (Button) findViewById(R.id.btn_reMix);
		btnFinish.setText("停止播放");

		btnStop.setEnabled(false);
		btnPlay.setEnabled(false);
		btnFinish.setEnabled(false);
		btnMixStart.setEnabled(false);
		btnMixPause.setEnabled(false);
		btnMixStop.setEnabled(false);
		btn_reMix.setEnabled(false);

		btnStart.setOnClickListener(this);
		btnStop.setOnClickListener(this);
		btnPlay.setOnClickListener(this);
		btnFinish.setOnClickListener(this);
		btnMixStart.setOnClickListener(this);
		btnMixPause.setOnClickListener(this);
		btnMixStop.setOnClickListener(this);
		btn_reMix.setOnClickListener(this);

	}

	void initData() {
		RecordMixManager.getInstance().getTempRingList(mContext, mHandler);

	}

	void initHandler() {
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == 1) {
					List<HashMap<String, Object>> ringList = (List<HashMap<String, Object>>) msg.obj;
					for (HashMap<String, Object> hashMap : ringList) {
						nameList.add(hashMap.get("title").toString());
						idList.add(hashMap.get("id").toString());
					}
					initDataAdapter(nameList);
				} else {
					DialogUtil.getInstance()
							.ShowToast(mContext, "客观别急，请先连接网络吧");
					btnStart.setEnabled(false);
				}
				super.handleMessage(msg);
			}
		};
		resultHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				DialogUtil.getInstance().ProgressDialog(mContext, false);
				if (msg.what == 1) {
					playUrl = msg.obj.toString();
					DialogUtil.getInstance().ShowAlertDialog(mContext,
							msg.toString());
					btnMixStart.setEnabled(true);
					endTime = new Date();
					long between = endTime.getTime() - startTime.getTime();
					long secondsInMilli = 1000;
					long minutesInMilli = secondsInMilli * 60;
					long hoursInMilli = minutesInMilli * 60;
					long daysInMilli = hoursInMilli * 24;
					long s = between / secondsInMilli;
					txt_time.setText(Long.toString(s));
					btn_reMix.setEnabled(true);
				} else if (msg.what == 16) {
					DialogUtil.getInstance()
							.ShowToast(mContext, "用户语音文件不存在或异常");
				} else if (msg.what == 17) {
					DialogUtil.getInstance().ShowToast(mContext, "歌曲模板不存在或异常");
				} else if (msg.what == 30) {
					DialogUtil.getInstance().ShowToast(mContext, "服务器繁忙，请稍后再试");
				}
				super.handleMessage(msg);
			}
		};
	}

	/**
	 * 加载数据填充adapter
	 * 
	 * @param list
	 */
	void initDataAdapter(final List<String> list) {

		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, list);
		spinner.setAdapter(adapter);

		spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int location, long arg3) {
				// 保存选中的合成音乐模板
				selectId = idList.get(location).toString();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.btn_start:
			// 开始录制
			stateView.setText("正在录制");
			// 这里启动录制任务
			recorder = new RecordTask();
			recorder.execute();

			break;
		case R.id.btn_stop:
			// 停止录制
			this.isRecording = false;

			Date record = new Date();
			long between = record.getTime() - startTime.getTime();
			long secondsInMilli = 1000;
			long minutesInMilli = secondsInMilli * 60;
			long hoursInMilli = minutesInMilli * 60;
			long daysInMilli = hoursInMilli * 24;
			long s = between / secondsInMilli;
			stateView.setText("录音用时：" + Long.toString(s) + "秒");

			// 在录制完成时设置，在RecordTask的onPostExecute中完成
			RecordMixManager.getInstance().updateFileToMix(mContext, selectId,
					audioFile,"raw", resultHandler);

			// 保存文件到数据库
			// RecordMixManager.getInstance().SaveRecordToDB(mContext,audioFile.getName(),audioFile.getPath());
			break;
		case R.id.btn_play:
			player = new PlayTask();
			player.execute();
			break;
		case R.id.btn_finish:
			// 完成播放
			this.isPlaying = false;
			break;
		case R.id.btn_reMix:
			// 显示合成界面
			DialogUtil.getInstance().ProgressDialog(mContext, true);
			// 向服务器发出合成命令
			startTime = new Date();
			RecordMixManager.getInstance().addMix(mContext, mixId, selectId,
					resultHandler);
			break;
		case R.id.btn_mixStart:
			// 播放合成音乐
			playerIntent = new Intent();
			playerIntent.setClass(mContext, PlayerService.class);
			playerIntent.putExtra("PLAY_URL", playUrl);
			playerIntent.putExtra("PLAY_MSG", PlayerConfigs.PLAY_MSG);
			startService(playerIntent);
			btnMixStart.setEnabled(false);
			btnMixPause.setEnabled(true);
			btnMixStop.setEnabled(true);
			break;
		case R.id.btn_mixPause:
			// 暂停合成音乐
			playerIntent = new Intent();
			playerIntent.setClass(mContext, PlayerService.class);
			playerIntent.putExtra("PLAY_URL", playUrl);
			playerIntent.putExtra("PLAY_MSG", PlayerConfigs.PAUSE_MSG);
			startService(playerIntent);
			btnMixStart.setEnabled(true);
			btnMixPause.setEnabled(false);
			btnMixStop.setEnabled(true);
			break;
		case R.id.btn_mixStop:
			// 停止播放音乐
			playerIntent = new Intent();
			playerIntent.setClass(mContext, PlayerService.class);
			playerIntent.putExtra("PLAY_URL", playUrl);
			playerIntent.putExtra("PLAY_MSG", PlayerConfigs.STOP_MSG);
			startService(playerIntent);
			btnMixStart.setEnabled(true);
			btnMixPause.setEnabled(false);
			btnMixStop.setEnabled(false);
			break;
		}
	}

	/**
	 * 录制音频
	 */
	class RecordTask extends AsyncTask<Void, Integer, Void> {
		@Override
		protected Void doInBackground(Void... arg0) {
			isRecording = true;
			try {
				startTime = new Date();
				audioFile = RecordMixManager.getInstance().CreateFilePath("pcm");
				// 开通输出流到指定的文件
				DataOutputStream dos = new DataOutputStream(
						new BufferedOutputStream(
								new FileOutputStream(audioFile)));
				// 根据定义好的几个配置，来获取合适的缓冲大小
				bufferSize = AudioRecord.getMinBufferSize(frequence,
						channelConfig, audioEncoding);
				// 实例化AudioRecord
				AudioRecord record = new AudioRecord(
						MediaRecorder.AudioSource.MIC, frequence,
						channelConfig, audioEncoding, bufferSize);
				// 定义缓冲
				short[] buffer = new short[bufferSize];
				int bufResult = 0;

				// 开始录制
				record.startRecording();

				int r = 0; // 存储录制进度
				// 定义循环，根据isRecording的值来判断是否继续录制
				while (isRecording) {
					// 从bufferSize中读取字节，返回读取的short个数
					// 这里老是出现buffer overflow，不知道是什么原因，试了好几个值，都没用，TODO：待解决
					int bufferReadResult = record
							.read(buffer, 0, buffer.length);
					// 循环将buffer中的音频数据写入到OutputStream中
					for (int i = 0; i < bufferReadResult; i++) {
						dos.writeShort(buffer[i]);
					}
					publishProgress(new Integer(r)); // 向UI线程报告当前进度
					r++; // 自增进度值
				}
				// 录制结束
				record.stop();
				Log.v("The DOS available:", "::" + audioFile.length());
				dos.close();

			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			return null;
		}

		// 当在上面方法中调用publishProgress时，该方法触发,该方法在UI线程中被执行
		protected void onProgressUpdate(Integer... progress) {
		}

		protected void onPostExecute(Void result) {
			btnStop.setEnabled(false);
			btnStart.setEnabled(true);
			btnPlay.setEnabled(true);
			btnFinish.setEnabled(false);
		}

		protected void onPreExecute() {
			btnStart.setEnabled(false);
			btnPlay.setEnabled(false);
			btnFinish.setEnabled(false);
			btnStop.setEnabled(true);
		}

	}

	/**
	 * 播放录制音频
	 */
	class PlayTask extends AsyncTask<Void, Integer, Void> {
		@Override
		protected Void doInBackground(Void... arg0) {
			isPlaying = true;
			bufferSize = AudioTrack.getMinBufferSize(frequence, channelConfig,
					audioEncoding);
			short[] buffer = new short[bufferSize / 4];
			try {
				// 定义输入流，将音频写入到AudioTrack类中，实现播放
				DataInputStream dis = new DataInputStream(
						new BufferedInputStream(new FileInputStream(audioFile)));
				// 实例AudioTrack
				AudioTrack track = new AudioTrack(AudioManager.STREAM_MUSIC,
						frequence, channelConfig, audioEncoding, bufferSize,
						AudioTrack.MODE_STREAM);
				// 开始播放
				track.play();
				// 由于AudioTrack播放的是流，所以，我们需要一边播放一边读取
				while (isPlaying && dis.available() > 0) {
					int i = 0;
					while (dis.available() > 0 && i < buffer.length) {
						buffer[i] = dis.readShort();
						i++;
					}
					// 然后将数据写入到AudioTrack中
					track.write(buffer, 0, buffer.length);

				}

				// 播放结束
				track.stop();
				dis.close();
			} catch (Exception e) {
				// TODO: handle exception
			}
			return null;
		}

		protected void onPostExecute(Void result) {
			btnPlay.setEnabled(true);
			btnFinish.setEnabled(false);
			btnStart.setEnabled(true);
			btnStop.setEnabled(false);
		}

		protected void onPreExecute() {

			// stateView.setText("正在播放");
			btnStart.setEnabled(false);
			btnStop.setEnabled(false);
			btnPlay.setEnabled(false);
			btnFinish.setEnabled(true);
		}

	}

}
