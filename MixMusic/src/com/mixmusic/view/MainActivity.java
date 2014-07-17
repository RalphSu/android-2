package com.mixmusic.view;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.mixmusic.R;
import com.mixmusic.biz.ApiConfigs;
import com.mixmusic.biz.AppConstant;
import com.mixmusic.biz.BizManager;
import com.mixmusic.record.AudioFileFunc;
import com.mixmusic.record.ErrorCode;
import com.mixmusic.record.MediaRecordFunc;
import com.mixmusic.service.PlayerService;
import com.mixmusic.utils.DialogUtil;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener {

	private final static int FLAG_WAV = 0;
	private final static int FLAG_AMR = 1;
	private int mState = -1; // -1:û��¼�ƣ�0��¼��wav��1��¼��amr
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
	private String playUrl;// ���ŵ�ַ
	private BizManager biz = BizManager.getInstance();
	private Date startTime, endTime;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH��mm-ss");
	private Intent playerIntent;
	public static String mixId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mContext = MainActivity.this;
		initView();
		initListeners();
		initHandler();
		initData();
	}

	/**
	 * ��ʼ���ؼ�
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
	 * ��ʼ���¼�
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
	 * ��ʼ������
	 */
	void initData() {
		biz.getLogin(mContext, loginHandler);

	}

	private void initHandler() {
		uiHandler = new UIHandler();

		loginHandler = new Handler() {
			@SuppressLint("HandlerLeak")
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == 1) {
					DialogUtil.getInstance().ShowToast(mContext, "��ӭʹ��¼���ϳ����");
				}
				super.handleMessage(msg);
			}
		};
		mixHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {

				if (msg.what == 1) {
					playUrl = msg.obj.toString();
				} else if (msg.what == 18) {
					DialogUtil.getInstance().ShowToast(mContext, "����18�������ºϳ�");
				} else {
					DialogUtil.getInstance().ShowToast(mContext,
							msg.obj.toString());
				}
				DialogUtil.getInstance().ProgressDialog(mContext, false);
				super.handleMessage(msg);
			}
		};

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
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
			// ��ʾ�ϳɽ���
			DialogUtil.getInstance().ProgressDialog(mContext, true);
			// ������������ϳ�����
//			biz.updateFileToMix(mContext, ApiConfigs.selectId, audioFile, "amr",
//					mixHandler);
			break;
		case R.id.btn_mixStart:
			// ���źϳ�����"http://118.85.203.37:9495/res/1645/mp3/00/02/41/1645000241001600.mp3"
			// http://218.244.128.50:8080/app/upload/music/2014/5/28/ac928617-68be-4c2e-a691-f95a5f10c24a.mp3
			// http://218.244.128.50:8080/app/upload/music/2014/5/28/bdf5e93f-a038-4b82-b5c7-5eae8c0b0d17.mp3
			playMusic(playUrl, AppConstant.PlayerMsg.PLAY_MSG);
			btnMixStart.setEnabled(false);
			btnMixPause.setEnabled(true);
			btnMixStop.setEnabled(true);
			break;
		case R.id.btn_mixPause:
			// ��ͣ�ϳ�����
			playMusic(playUrl, AppConstant.PlayerMsg.PAUSE_MSG);
			btnMixStart.setEnabled(true);
			btnMixPause.setEnabled(false);
			btnMixStop.setEnabled(true);
			break;
		case R.id.btn_mixStop:
			// ֹͣ��������
			playMusic(playUrl, AppConstant.PlayerMsg.STOP_MSG);
			btnMixStart.setEnabled(true);
			btnMixPause.setEnabled(false);
			btnMixStop.setEnabled(false);
			btn_reMix.setEnabled(true);
			break;
		}
	}

	public void playMusic(String playUrl, int action) {
		playerIntent = new Intent();
		playerIntent.putExtra("PLAY_URL", playUrl);
		playerIntent.putExtra("PLAY_MSG", action);
		playerIntent.setClass(MainActivity.this, PlayerService.class);
		startService(playerIntent);
	}

	/**
	 * ��ʼ¼��
	 * 
	 * @param mFlag
	 *            ��0��¼��wav��ʽ��1��¼��amr��ʽ
	 */
	private void record(int mFlag) {
		if (mState != -1) {
			Message msg = new Message();
			Bundle b = new Bundle();// �������
			b.putInt("cmd", CMD_RECORDFAIL);
			b.putInt("msg", ErrorCode.E_STATE_RECODING);
			msg.setData(b);

			uiHandler.sendMessage(msg); // ��Handler������Ϣ,����UI
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
			Bundle b = new Bundle();// �������
			b.putInt("cmd", CMD_RECORDFAIL);
			b.putInt("msg", mResult);
			msg.setData(b);

			uiHandler.sendMessage(msg); // ��Handler������Ϣ,����UI
		}
	}

	/**
	 * ֹͣ¼��
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
			Bundle b = new Bundle();// �������
			b.putInt("cmd", CMD_STOP);
			b.putInt("msg", mState);
			msg.setData(b);
			uiHandler.sendMessageDelayed(msg, 1000); // ��Handler������Ϣ,����UI
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
				textview_msg.setText("����¼���У���¼�ƣ�" + vTime + " s");
				break;
			case CMD_RECORDFAIL:
				int vErrorCode = b.getInt("msg");
				String vMsg = ErrorCode.getErrorInfo(mContext, vErrorCode);
				textview_msg.setText("¼��ʧ�ܣ�" + vMsg);
				break;
			case CMD_STOP:
				int vFileType = b.getInt("msg");
				switch (vFileType) {
				case FLAG_AMR:
					MediaRecordFunc mRecord_2 = MediaRecordFunc.getInstance();
					long mSize = mRecord_2.getRecordFileSize();
					textview_msg.setText("¼����ֹͣ.¼���ļ�:"
							+ AudioFileFunc.getAMRFilePath() + "\n�ļ���С��"
							+ mSize);
					audioFile = mRecord_2.getAudioFile();
					// ��¼�����ʱ���ã���RecordTask��onPostExecute�����
//					biz.updateFileToMix(mContext, ApiConfigs.musicId,
//							audioFile, "amr", mixHandler);
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
					// ˯��1�룬����ʱ��
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				mTimeMill++;
				Log.d("thread", "mThread........" + mTimeMill);
				Message msg = new Message();
				Bundle b = new Bundle();// �������
				b.putInt("cmd", CMD_RECORDING_TIME);
				b.putInt("msg", mTimeMill);
				msg.setData(b);

				uiHandler.sendMessage(msg); // ��Handler������Ϣ,����UI
			}
		}
	}

}
