package com.recordmix.view;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.recordmix.R;
import com.recordmix.biz.ApiConfigs;
import com.recordmix.biz.BizManager;
import com.recordmix.biz.PlayerConfigs;
import com.recordmix.biz.RecordMixManager;
import com.recordmix.record.AudioFileFunc;
import com.recordmix.record.AudioRecordFunc;
import com.recordmix.record.ErrorCode;
import com.recordmix.record.MediaRecordFunc;
import com.recordmix.services.MediaPlayerService;
import com.recordmix.services.PlayerService;
import com.recordmix.utils.DialogUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
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

public class RecordMixActivity extends Activity implements OnClickListener {
	private final static int FLAG_WAV = 0;
	private final static int FLAG_AMR = 1;
	private int mState = -1; // -1:没再录制，0：录制wav，1：录制amr
	private Button btn_record_amr, btn_stop, btnMixStart, btnMixPause,
			btnMixStop, btn_reMix;
	private TextView textview_msg, txt_time;
	private ArrayAdapter<String> adapter;
	private List<String> nameList = new ArrayList<String>();
	private List<String> idList = new ArrayList<String>();
	private UIHandler uiHandler;
	private UIThread uiThread;
	private Context mContext;
	private File audioFile;
	private String selectId = "1";
	private Handler loginHandler, mixHandler;
	private String playUrl;// 播放地址
	private Intent playerIntent; // 播放器Intent
	private BizManager biz = BizManager.getInstance();
	private Date startTime, endTime;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH—mm-ss");

	public static String mixId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recordmix_main);
		mContext = RecordMixActivity.this;
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
		btn_record_amr = (Button) this.findViewById(R.id.btn_record_amr);
		btn_stop = (Button) this.findViewById(R.id.btn_stop);

		textview_msg = (TextView) this.findViewById(R.id.text);
		txt_time = (TextView) this.findViewById(R.id.txt_time);
		btnMixStart = (Button) findViewById(R.id.btn_mixStart);
		btnMixPause = (Button) findViewById(R.id.btn_mixPause);
		btnMixStop = (Button) findViewById(R.id.btn_mixStop);
		btn_reMix = (Button) findViewById(R.id.btn_reMix);

		btn_stop.setEnabled(false);
		btnMixStart.setEnabled(true);
		btnMixPause.setEnabled(false);
		btnMixStop.setEnabled(false);
		btn_reMix.setEnabled(false);
	}

	/**
	 * 初始化事件
	 */
	private void initListeners() {
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
		biz.getLogin(mContext, loginHandler);
	}

	@SuppressLint("HandlerLeak")
	private void initHandler() {
		uiHandler = new UIHandler();

		loginHandler = new Handler() {
			@SuppressLint("HandlerLeak")
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == 1) {
					DialogUtil.getInstance().ShowToast(mContext, "欢迎使用录音合成软件");
				}
				super.handleMessage(msg);
			}
		};
		mixHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
			
				if (msg.what == 1) {
					playUrl = msg.obj.toString();
				}
				else if (msg.what == 18) {
					DialogUtil.getInstance().ShowToast(mContext, "返回18，请重新合成");
				}
				else
				{
					DialogUtil.getInstance().ShowToast(mContext, msg.obj.toString());
				}
				DialogUtil.getInstance().ProgressDialog(mContext, false);
				super.handleMessage(msg);
			}
		};

	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.btn_record_amr:
			startTime = new Date();
			btn_stop.setEnabled(true);
			btn_record_amr.setEnabled(false);
			record(FLAG_AMR);
			break;
		case R.id.btn_stop:
			stop();
			btn_record_amr.setEnabled(true);
			btnMixStart.setEnabled(true);
			btn_reMix.setEnabled(true);
			
			break;
		case R.id.btn_reMix:
			// 显示合成界面
			DialogUtil.getInstance().ProgressDialog(mContext, true);
			// 向服务器发出合成命令
			biz.updateFileToMix(mContext, ApiConfigs.musicId,
					audioFile, "amr", mixHandler);
			break;
		case R.id.btn_mixStart:
			// 播放合成音乐"http://118.85.203.37:9495/res/1645/mp3/00/02/41/1645000241001600.mp3"
			 playMusic("http://muse.yun.fm/api/remixdl?remixfile=589fa180a4223e9a2ff0fc93edafcec6.1.mp3",PlayerConfigs.PLAY_MSG);  
			btnMixStart.setEnabled(false);
			btnMixPause.setEnabled(true);
			btnMixStop.setEnabled(true);
			break;
		case R.id.btn_mixPause:
			// 暂停合成音乐

			btnMixStart.setEnabled(true);
			btnMixPause.setEnabled(false);
			btnMixStop.setEnabled(true);
			break;
		case R.id.btn_mixStop:
			// 停止播放音乐
			btnMixStart.setEnabled(true);
			btnMixPause.setEnabled(false);
			btnMixStop.setEnabled(false);
			btn_reMix.setEnabled(true);
			break;
		}
	}
	
	 public void playMusic(String playUrl,int action) {    
	        Intent intent = new Intent();  
	        intent.putExtra("PLAY_URL", playUrl);    
	        intent.putExtra("MSG", action);    
	        intent.setClass(mContext, MediaPlayerService.class);    
	        startService(intent);    
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
				case FLAG_AMR:
					MediaRecordFunc mRecord_2 = MediaRecordFunc.getInstance();
					long mSize = mRecord_2.getRecordFileSize();
					textview_msg.setText("录音已停止.录音文件:"
							+ AudioFileFunc.getAMRFilePath() + "\n文件大小："
							+ mSize);
					audioFile = mRecord_2.getAudioFile();
					// 在录制完成时设置，在RecordTask的onPostExecute中完成
					biz.updateFileToMix(mContext, ApiConfigs.musicId,
							audioFile, "amr", mixHandler);
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