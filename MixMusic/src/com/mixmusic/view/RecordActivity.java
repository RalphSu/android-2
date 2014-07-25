package com.mixmusic.view;

import java.io.File;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Chronometer;
import android.widget.Chronometer.OnChronometerTickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mixmusic.R;
import com.mixmusic.biz.ApiConfigs;
import com.mixmusic.biz.AppConstant;
import com.mixmusic.biz.BizManager;
import com.mixmusic.record.MediaRecordFunc;
import com.mixmusic.service.PlayerService;
import com.mixmusic.utils.DialogUtil;
import com.mixmusic.utils.OnTabActivityResultListener;

public class RecordActivity extends Activity implements OnClickListener, OnChronometerTickListener,
		OnTabActivityResultListener {

	private Context mContext;
	private AnimationDrawable animationDrawable;
	private Chronometer record_chronometer; // ��ʱ���
	private ProgressBar progressBar;
	private LinearLayout layout_tools;
	private RelativeLayout layout_changed, layout_record, layout_action, layout_reset, layout_zring, layout_save;
	private ImageView imageview_animation, imageview_play, imageview_pause;
	private TextView textview_action, textview_changed, textview_music_name;
	public static Handler loginHandler, uploadHandler, mixHandler, saveReNameHandler;
	private MediaRecordFunc mRecord = MediaRecordFunc.getInstance();
	private File audioFile; // ��ǰ¼���ļ�
	private String playUrl; // �ϳ����ֲ��ŵ�ַ
	private int mState = -1; // -1:û��¼�ƣ�0��¼��wav��1��¼��amr
	private final static int CMD_RECORDING_TIME = 2000;
	private final static int CMD_RECORDFAIL = 2001;
	private final static int CMD_STOP = 2002;
	private boolean isRecording = false;
	private boolean isPlaying = false;
	private boolean isMaxed = false;
	private boolean isExit; // �ж��Ƿ��˳�
	private boolean isReMix = false;

	private BizManager biz = BizManager.getInstance();

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.record_main);
		mContext = RecordActivity.this;
		initView();
		initEvent();
		initHandler();
		initData();
		IntentFilter filter = new IntentFilter();
		filter.addAction("MusicChange");
		mContext.registerReceiver(onlineBroadcastReceiver, filter);
	}

	/**
	 * ���ý���
	 */
	private void initView() {
		textview_changed = (TextView) findViewById(R.id.textview_changed);// ��������
		textview_action = (TextView) findViewById(R.id.textview_action); // ��ʾ��
		textview_music_name = (TextView) findViewById(R.id.textview_music_name); // ��������

		imageview_animation = (ImageView) findViewById(R.id.imageview_animation);// ����
		imageview_play = (ImageView) findViewById(R.id.imageview_play); // ����
		imageview_pause = (ImageView) findViewById(R.id.imageview_pause); // ��ͣ

		layout_changed = (RelativeLayout) findViewById(R.id.layout_changed); // ����
		layout_record = (RelativeLayout) findViewById(R.id.layout_record); // ������
		layout_action = (RelativeLayout) findViewById(R.id.layout_action); // ��ʾ
		layout_reset = (RelativeLayout) findViewById(R.id.layout_reset); // ����
		layout_zring = (RelativeLayout) findViewById(R.id.layout_zring);// ����
		layout_save = (RelativeLayout) findViewById(R.id.layout_save);// ����

		layout_tools = (LinearLayout) findViewById(R.id.layout_tools); // ������

		record_chronometer = (Chronometer) findViewById(R.id.record_chronometer); // ��ʱ��
		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		// �õ�����
		animationDrawable = (AnimationDrawable) imageview_animation.getDrawable();
		animationDrawable.stop();
	}

	/**
	 * ���շ�������
	 */
	@Override
	public void onTabActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == Activity.RESULT_OK) {
			ApiConfigs.selectId = data.getStringExtra("selectId");
			ApiConfigs.selectName = data.getStringExtra("selectName");
			textview_music_name.setText("������" + ApiConfigs.selectName);
			// �ж��Ƿ��Ѿ��ϳɹ�
			if (isMaxed) {
				play(AppConstant.PlayerMsg.STOP_MSG);
				imageview_play.setVisibility(View.VISIBLE);
				imageview_pause.setVisibility(View.GONE);
				textview_action.setText("������ºϳ�");

				isReMix = true;
			}

		}

	}

	/**
	 * �����¼�
	 */
	private void initEvent() {

		textview_changed.setOnClickListener(this);
		textview_action.setOnClickListener(this);
		layout_record.setOnClickListener(this);
		layout_tools.setOnClickListener(this);
		imageview_play.setOnClickListener(this);
		imageview_pause.setOnClickListener(this);

		layout_reset.setOnClickListener(this);
		layout_zring.setOnClickListener(this);
		layout_save.setOnClickListener(this);
		record_chronometer.setOnChronometerTickListener(this);

		layout_changed.setVisibility(View.GONE);
		textview_music_name.setVisibility(View.VISIBLE);
		progressBar.setVisibility(View.GONE);
		imageview_pause.setVisibility(View.GONE);
		layout_tools.setVisibility(View.GONE);
		record_chronometer.setVisibility(View.VISIBLE);

	}

	/**
	 * ��ʼ������
	 */
	void initData() {
		biz.getLogin(mContext, loginHandler);
	}

	/**
	 * ����Handler
	 */
	@SuppressLint("HandlerLeak")
	private void initHandler() {
		loginHandler = new Handler() {
			@SuppressLint("HandlerLeak")
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == 1) {
					textview_music_name.setText("������" + ApiConfigs.selectName);
					DialogUtil.getInstance().ShowToast(mContext, "��ӭʹ�û����ϳ�");
				} else {
					DialogUtil.getInstance().ShowToast(mContext, "�͹۱𼱣��������������");
				}
				super.handleMessage(msg);
			}
		};
		uploadHandler = new Handler() {
			@SuppressLint("HandlerLeak")
			@Override
			public void handleMessage(Message msg) {
				progressBar.setVisibility(View.GONE);
				if (msg.what == 1) {
					textview_action.setText("���ںϳ�...");
					animationDrawable.start();
					BizManager.getInstance().checkMixMusic(mContext, msg.obj.toString(), "amr", mixHandler);
				} else {
					DialogUtil.getInstance().ShowToast(mContext, msg.obj.toString());
					reSet();
				}
				pauseControl(false);
				super.handleMessage(msg);
			}
		};

		mixHandler = new Handler() {
			@SuppressLint("HandlerLeak")
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == 1) {
					// ��ò��ŵ�ַ
					playUrl = msg.obj.toString();
					mixed();
				} else {
					reSet();
					if (isReMix) {
						textview_action.setText("�ϳ�ʧ�ܣ��������");
					} else {
						textview_action.setText("�ϴ�ʧ�ܣ��������¼��");
					}
				}
				pauseControl(true);
				super.handleMessage(msg);
			}
		};
		saveReNameHandler = new Handler() {
			@SuppressLint("HandlerLeak")
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == 1) {
					// ��ò��ŵ�ַ
					DialogUtil.getInstance().ShowToast(mContext, "����ɹ�");
				} else {
					DialogUtil.getInstance().ShowToast(mContext, "����ʧ�ܣ�������");

				}
				pauseControl(true);
				super.handleMessage(msg);
			}
		};
	}

	@Override
	public void onChronometerTick(Chronometer arg0) {

		String time = arg0.getText().toString();
		if ("00:45".equals(time)) {
			// ��ʾ����
			stopRecord();
		}
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.textview_changed:
			Intent intent = new Intent(mContext, ChangedActivity.class);
			getParent().startActivityForResult(intent, 1);
			break;
		case R.id.layout_record:
			if (isReMix) {
				reMix();
			} else if (isMaxed) {
				play(AppConstant.PlayerMsg.PLAY_MSG);
			} else {
				play(AppConstant.PlayerMsg.STOP_MSG);
				record();
			}
			break;
		case R.id.imageview_play:
			if (isReMix) {
				reMix();
			} else if (isMaxed) {
				play(AppConstant.PlayerMsg.PLAY_MSG);

			} else {
				play(AppConstant.PlayerMsg.STOP_MSG);
				record();
			}
			break;
		case R.id.imageview_pause:
			if (isMaxed) {
				play(AppConstant.PlayerMsg.STOP_MSG);
				textview_action.setText("��ֹͣ����");
			} else {
				stopRecord();
			}
			break;
		case R.id.textview_action:
			if (isReMix) {
				reMix();
			} else if (isMaxed) {
				// play();
			} else {
				record();
			}
			break;
		case R.id.layout_reset:
			if (isPlaying) {
				play(AppConstant.PlayerMsg.STOP_MSG);
			}
			reSet();
			break;
		case R.id.layout_zring:
			DialogUtil.getInstance().ShowToast(mContext, "Coming Soon");
			break;
		case R.id.layout_save:
			DialogUtil.getInstance().ShowAlertDialog(mContext, saveReNameHandler);
			break;
		}
	}

	/**
	 * ��������
	 * 
	 */
	protected void play(int action) {
		if (action == AppConstant.PlayerMsg.PLAY_MSG) {
			isPlaying = true;
		} else {
			isPlaying = false;
		}
		isPlaying = true;
		imageview_play.setVisibility(View.GONE);
		imageview_pause.setVisibility(View.VISIBLE);
		record_chronometer.setVisibility(View.GONE);

		Intent playerIntent = new Intent();
		playerIntent.putExtra("PLAY_URL", playUrl);
		playerIntent.putExtra("PLAY_MSG", action);
		playerIntent.setClass(mContext, PlayerService.class);
		startService(playerIntent);
		if (action == AppConstant.PlayerMsg.PLAY_MSG) {
			textview_action.setText("����������...");
		}

	}

	/**
	 * ��ʼ¼��
	 * 
	 */
	private void record() {
		isRecording = true;
		animationDrawable.start();
		imageview_play.setVisibility(View.GONE);
		imageview_pause.setVisibility(View.VISIBLE);
		// ��ʼ��ʱ
		record_chronometer.setVisibility(View.VISIBLE);
		record_chronometer.setBase(SystemClock.elapsedRealtime());
		record_chronometer.start();
		layout_record.setEnabled(false);
		textview_action.setText("¼����...");
		mRecord.startRecordAndFile();
		pauseControl(false);
	}

	/**
	 * ֹͣ¼��
	 * 
	 */
	private void stopRecord() {
		isRecording = false;
		animationDrawable.stop();
		record_chronometer.stop();
		textview_action.setText("����ִ���ϴ�...");
		mRecord.stopRecordAndFile();
		audioFile = mRecord.getAudioFile();

		progressBar.setVisibility(View.VISIBLE);
		// ��¼�����ʱ���ã���RecordTask��onPostExecute�����
		biz.updateFileToMix(mContext, progressBar, ApiConfigs.selectId, audioFile, "amr", uploadHandler);
	}

	/**
	 * �ϳɹ��ܺ������
	 */
	private void mixed() {

		textview_action.setText("�ϳɳɹ�������ɲ���");
		animationDrawable.stop();
		imageview_play.setVisibility(View.VISIBLE);
		imageview_pause.setVisibility(View.GONE);
		layout_tools.setVisibility(View.VISIBLE);
		layout_changed.setVisibility(View.VISIBLE);
		pauseControl(true);
		isMaxed = true;
		isReMix = false;
	}

	private void reMix() {
		animationDrawable.start();
		layout_tools.setVisibility(View.GONE);
		layout_changed.setVisibility(View.GONE);
		biz.reMixMusic(mContext, mixHandler);
		textview_action.setText("���ºϳ���...");
		pauseControl(false);
	}

	/**
	 * ���ÿؼ�
	 */
	private void reSet() {
		animationDrawable.stop();
		record_chronometer.setVisibility(View.VISIBLE);
		record_chronometer.setBase(SystemClock.elapsedRealtime());
		imageview_play.setVisibility(View.VISIBLE);
		imageview_pause.setVisibility(View.GONE);
		layout_changed.setVisibility(View.GONE);
		layout_tools.setVisibility(View.GONE);
		textview_action.setText("�����ʼ¼��");
		pauseControl(true);
		isMaxed = false;
		isReMix = false;
	}

	/**
	 * ¼�����̽�ֹ�����ؼ�ִ��
	 */
	private void pauseControl(boolean isPause) {
		if (isPause) {
			layout_record.setEnabled(true);
			textview_action.setEnabled(true);
			record_chronometer.setEnabled(true);
		} else {

			layout_record.setEnabled(false);
			textview_action.setEnabled(false);
			record_chronometer.setEnabled(false);
		}

	}

	@Override
	protected void onDestroy() {

		Intent playerIntent = new Intent();
		playerIntent.putExtra("PLAY_MSG", AppConstant.PlayerMsg.STOP_MSG);
		playerIntent.setClass(mContext, PlayerService.class);

		System.out.println("------------stopService");
		stopService(playerIntent);
		super.onDestroy();
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			exit();
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	/* �ж��˳����� */
	public void exit() {
		if (isExit == false) {
			isExit = true;
			Toast.makeText(getApplicationContext(), "�ٰ�һ���˳�����", Toast.LENGTH_LONG).show();
			mHandler.sendEmptyMessageDelayed(0, 3000);
		} else {

			Intent playerIntent = new Intent();
			playerIntent.putExtra("PLAY_MSG", AppConstant.PlayerMsg.STOP_MSG);
			playerIntent.setClass(mContext, PlayerService.class);
			System.out.println("------------stopService");
			stopService(playerIntent);

			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_HOME);
			startActivity(intent);
			System.exit(0);
		}
	}

	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			isExit = false;
		}

	};
	private final BroadcastReceiver onlineBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			textview_music_name.setText("������" + ApiConfigs.selectName);

		}
	};
}
