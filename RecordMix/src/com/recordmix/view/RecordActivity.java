package com.recordmix.view;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.recordmix.R;
import com.recordmix.biz.PlayerConfigs;
import com.recordmix.biz.RecordMixManager;
import com.recordmix.record.AudioFileFunc;
import com.recordmix.record.AudioRecordFunc;
import com.recordmix.record.ErrorCode;
import com.recordmix.record.MediaRecordFunc;
import com.recordmix.services.PlayerService;
import com.recordmix.utils.DialogUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class RecordActivity extends Activity implements OnClickListener {
	private final static int FLAG_WAV = 0;
	private final static int FLAG_AMR = 1;
	private int mState = -1; // -1:没再录制，0：录制wav，1：录制amr
	private Button btn_record_wav, btn_record_amr, btn_stop, btnMixStart,
			btnMixPause, btnMixStop, btn_reMix;
	private Spinner spinner;
	private TextView textview_msg, txt_time;
	private ArrayAdapter<String> adapter;
	private List<String> nameList = new ArrayList<String>();
	private List<String> idList = new ArrayList<String>();
	private UIHandler uiHandler;
	private UIThread uiThread;
	private Context mContext;
	private File audioFile;
	private String selectId = "1";
	private Handler mHandler, resultHandler;
	private String playUrl;// 播放地址
	private Intent playerIntent; // 播放器Intent

	private Date startTime, endTime;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH—mm-ss");

	public static String mixId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.record_main);
		mContext = RecordActivity.this;
		initView();
		initListeners();
		initHandler();
		initData();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.record_main, menu);
		return true;
	}

	/**
	 * 初始化控件
	 */
	private void initView() {
		spinner = (Spinner) findViewById(R.id.spinner);
		btn_record_wav = (Button) this.findViewById(R.id.btn_record_wav);
		btn_record_amr = (Button) this.findViewById(R.id.btn_record_amr);
		btn_stop = (Button) this.findViewById(R.id.btn_stop);

		textview_msg = (TextView) this.findViewById(R.id.text);
		txt_time= (TextView) this.findViewById(R.id.txt_time);
		btnMixStart = (Button) findViewById(R.id.btn_mixStart);
		btnMixPause = (Button) findViewById(R.id.btn_mixPause);
		btnMixStop = (Button) findViewById(R.id.btn_mixStop);
		btn_reMix = (Button) findViewById(R.id.btn_reMix);

		btn_stop.setEnabled(false);
		btnMixStart.setEnabled(false);
		btnMixPause.setEnabled(false);
		btnMixStop.setEnabled(false);
		btn_reMix.setEnabled(false);
	}

	/**
	 * 初始化事件
	 */
	private void initListeners() {
		btn_record_wav.setOnClickListener(this);
		btn_record_amr.setOnClickListener(this);
		btn_stop.setOnClickListener(this);
		btnMixStart.setOnClickListener(this);
		btnMixPause.setOnClickListener(this);
		btnMixStop.setOnClickListener(this);
		btn_reMix.setOnClickListener(this);
	}

	/**
	 * 初始化数据
	 */
	void initData() {
		RecordMixManager.getInstance().getTempRingList(mContext, mHandler);

	}

	private void initHandler() {
		uiHandler = new UIHandler();

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
					endTime = new Date();
					long between = endTime.getTime() - startTime.getTime();
					long secondsInMilli = 1000;
					long minutesInMilli = secondsInMilli * 60;
					long hoursInMilli = minutesInMilli * 60;
					long daysInMilli = hoursInMilli * 24;
					long s = between / secondsInMilli;
					txt_time.setText(Long.toString(s));

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
		case R.id.btn_record_wav:
			startTime = new Date();
			btn_stop.setEnabled(true);
			btn_record_amr.setEnabled(false);
			btn_record_wav.setEnabled(false);
			record(FLAG_WAV);
			break;
		case R.id.btn_record_amr:
			startTime = new Date();
			btn_stop.setEnabled(true);
			btn_record_amr.setEnabled(false);
			btn_record_wav.setEnabled(false);
			record(FLAG_AMR);
			break;
		case R.id.btn_stop:
			stop();
			btn_record_amr.setEnabled(true);
			btn_record_wav.setEnabled(true);
			btnMixStart.setEnabled(true);
			btn_reMix.setEnabled(false);
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
			btn_reMix.setEnabled(true);
			break;
		}
	}

	/**
	 * 开始录音
	 * 
	 * @param mFlag
	 *            ，0：录制wav格式，1：录音amr格式
	 */
	private void record(int mFlag) {
		if (mState != -1) {
			Message msg = new Message();
			Bundle b = new Bundle();// 存放数据
			b.putInt("cmd", CMD_RECORDFAIL);
			b.putInt("msg", ErrorCode.E_STATE_RECODING);
			msg.setData(b);

			uiHandler.sendMessage(msg); // 向Handler发送消息,更新UI
			return;
		}
		int mResult = -1;
		switch (mFlag) {
		case FLAG_WAV:
			AudioRecordFunc mRecord_1 = AudioRecordFunc.getInstance();
			mResult = mRecord_1.startRecordAndFile();
			break;
		case FLAG_AMR:
			MediaRecordFunc mRecord_2 = MediaRecordFunc.getInstance();
			mResult = mRecord_2.startRecordAndFile();
			break;
		}
		if (mResult == ErrorCode.SUCCESS) {
			uiThread = new UIThread();
			new Thread(uiThread).start();
			mState = mFlag;
		} else {
			Message msg = new Message();
			Bundle b = new Bundle();// 存放数据
			b.putInt("cmd", CMD_RECORDFAIL);
			b.putInt("msg", mResult);
			msg.setData(b);

			uiHandler.sendMessage(msg); // 向Handler发送消息,更新UI
		}
	}

	/**
	 * 停止录音
	 */
	private void stop() {
		if (mState != -1) {
			switch (mState) {
			case FLAG_WAV:
				AudioRecordFunc mRecord_1 = AudioRecordFunc.getInstance();
				mRecord_1.stopRecordAndFile();
				break;
			case FLAG_AMR:
				MediaRecordFunc mRecord_2 = MediaRecordFunc.getInstance();
				mRecord_2.stopRecordAndFile();
				break;
			}
			if (uiThread != null) {
				uiThread.stopThread();
			}
			if (uiHandler != null)
				uiHandler.removeCallbacks(uiThread);
			Message msg = new Message();
			Bundle b = new Bundle();// 存放数据
			b.putInt("cmd", CMD_STOP);
			b.putInt("msg", mState);
			msg.setData(b);
			uiHandler.sendMessageDelayed(msg, 1000); // 向Handler发送消息,更新UI
			mState = -1;
		}
	}

	private final static int CMD_RECORDING_TIME = 2000;
	private final static int CMD_RECORDFAIL = 2001;
	private final static int CMD_STOP = 2002;

	class UIHandler extends Handler {
		public UIHandler() {
		}

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			Log.d("MyHandler", "handleMessage......");
			super.handleMessage(msg);
			Bundle b = msg.getData();
			int vCmd = b.getInt("cmd");
			switch (vCmd) {
			case CMD_RECORDING_TIME:
				int vTime = b.getInt("msg");
				textview_msg.setText("正在录音中，已录制：" + vTime + " s");
				break;
			case CMD_RECORDFAIL:
				int vErrorCode = b.getInt("msg");
				String vMsg = ErrorCode.getErrorInfo(mContext, vErrorCode);
				textview_msg.setText("录音失败：" + vMsg);
				break;
			case CMD_STOP:
				int vFileType = b.getInt("msg");
				switch (vFileType) {
				case FLAG_WAV:
					AudioRecordFunc mRecord_1 = AudioRecordFunc.getInstance();
					long mSize = mRecord_1.getRecordFileSize();
					textview_msg.setText("录音已停止.录音文件:"
							+ AudioFileFunc.getWavFilePath() + "\n文件大小："
							+ mSize);
					audioFile = mRecord_1.getAudioFile();
					// 在录制完成时设置，在RecordTask的onPostExecute中完成
					RecordMixManager.getInstance().updateFileToMix(mContext,
							selectId, audioFile, "wav", resultHandler);
					break;
				case FLAG_AMR:
					MediaRecordFunc mRecord_2 = MediaRecordFunc.getInstance();
					mSize = mRecord_2.getRecordFileSize();
					textview_msg.setText("录音已停止.录音文件:"
							+ AudioFileFunc.getAMRFilePath() + "\n文件大小："
							+ mSize);
					audioFile = mRecord_2.getAudioFile();
					// 在录制完成时设置，在RecordTask的onPostExecute中完成
					RecordMixManager.getInstance().updateFileToMix(mContext,
							selectId, audioFile, "amr", resultHandler);
					break;
				}
				break;
			default:
				break;
			}
		}
	};

	class UIThread implements Runnable {
		int mTimeMill = 0;
		boolean vRun = true;

		public void stopThread() {
			vRun = false;
		}

		public void run() {
			while (vRun) {
				try {
					// 睡眠1秒，计算时间
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				mTimeMill++;
				Log.d("thread", "mThread........" + mTimeMill);
				Message msg = new Message();
				Bundle b = new Bundle();// 存放数据
				b.putInt("cmd", CMD_RECORDING_TIME);
				b.putInt("msg", mTimeMill);
				msg.setData(b);

				uiHandler.sendMessage(msg); // 向Handler发送消息,更新UI
			}
		}
	}

}