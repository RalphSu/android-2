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
	private Chronometer record_chronometer; // 计时组件
	private ProgressBar progressBar;
	private LinearLayout layout_tools;
	private RelativeLayout layout_changed, layout_record, layout_action, layout_reset, layout_zring, layout_save;
	private ImageView imageview_animation, imageview_play, imageview_pause;
	private TextView textview_action, textview_changed, textview_music_name;
	public static Handler loginHandler, uploadHandler, mixHandler, saveReNameHandler;
	private MediaRecordFunc mRecord = MediaRecordFunc.getInstance();
	private File audioFile; // 当前录音文件
	private String playUrl; // 合成音乐播放地址
	private int mState = -1; // -1:没再录制，0：录制wav，1：录制amr
	private final static int CMD_RECORDING_TIME = 2000;
	private final static int CMD_RECORDFAIL = 2001;
	private final static int CMD_STOP = 2002;
	private boolean isRecording = false;
	private boolean isPlaying = false;
	private boolean isMaxed = false;
	private boolean isExit; // 判断是否退出
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
	 * 配置界面
	 */
	private void initView() {
		textview_changed = (TextView) findViewById(R.id.textview_changed);// 换歌试试
		textview_action = (TextView) findViewById(R.id.textview_action); // 提示语
		textview_music_name = (TextView) findViewById(R.id.textview_music_name); // 音乐名称

		imageview_animation = (ImageView) findViewById(R.id.imageview_animation);// 动画
		imageview_play = (ImageView) findViewById(R.id.imageview_play); // 播放
		imageview_pause = (ImageView) findViewById(R.id.imageview_pause); // 暂停

		layout_changed = (RelativeLayout) findViewById(R.id.layout_changed); // 顶层
		layout_record = (RelativeLayout) findViewById(R.id.layout_record); // 动画层
		layout_action = (RelativeLayout) findViewById(R.id.layout_action); // 提示
		layout_reset = (RelativeLayout) findViewById(R.id.layout_reset); // 重试
		layout_zring = (RelativeLayout) findViewById(R.id.layout_zring);// 振铃
		layout_save = (RelativeLayout) findViewById(R.id.layout_save);// 保存

		layout_tools = (LinearLayout) findViewById(R.id.layout_tools); // 工具栏

		record_chronometer = (Chronometer) findViewById(R.id.record_chronometer); // 计时器
		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		// 拿到动画
		animationDrawable = (AnimationDrawable) imageview_animation.getDrawable();
		animationDrawable.stop();
	}

	/**
	 * 接收返回数据
	 */
	@Override
	public void onTabActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == Activity.RESULT_OK) {
			ApiConfigs.selectId = data.getStringExtra("selectId");
			ApiConfigs.selectName = data.getStringExtra("selectName");
			textview_music_name.setText("歌曲：" + ApiConfigs.selectName);
			// 判断是否已经合成过
			if (isMaxed) {
				play(AppConstant.PlayerMsg.STOP_MSG);
				imageview_play.setVisibility(View.VISIBLE);
				imageview_pause.setVisibility(View.GONE);
				textview_action.setText("点击重新合成");

				isReMix = true;
			}

		}

	}

	/**
	 * 配置事件
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
	 * 初始化数据
	 */
	void initData() {
		biz.getLogin(mContext, loginHandler);
	}

	/**
	 * 配置Handler
	 */
	@SuppressLint("HandlerLeak")
	private void initHandler() {
		loginHandler = new Handler() {
			@SuppressLint("HandlerLeak")
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == 1) {
					textview_music_name.setText("歌曲：" + ApiConfigs.selectName);
					DialogUtil.getInstance().ShowToast(mContext, "欢迎使用幻音合成");
				} else {
					DialogUtil.getInstance().ShowToast(mContext, "客观别急，请先连接网络吧");
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
					textview_action.setText("正在合成...");
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
					// 获得播放地址
					playUrl = msg.obj.toString();
					mixed();
				} else {
					reSet();
					if (isReMix) {
						textview_action.setText("合成失败，点击重试");
					} else {
						textview_action.setText("上传失败，点击重新录制");
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
					// 获得播放地址
					DialogUtil.getInstance().ShowToast(mContext, "保存成功");
				} else {
					DialogUtil.getInstance().ShowToast(mContext, "保存失败，请重试");

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
			// 显示布局
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
				textview_action.setText("已停止播放");
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
	 * 播放音乐
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
			textview_action.setText("幻音播放中...");
		}

	}

	/**
	 * 开始录音
	 * 
	 */
	private void record() {
		isRecording = true;
		animationDrawable.start();
		imageview_play.setVisibility(View.GONE);
		imageview_pause.setVisibility(View.VISIBLE);
		// 开始计时
		record_chronometer.setVisibility(View.VISIBLE);
		record_chronometer.setBase(SystemClock.elapsedRealtime());
		record_chronometer.start();
		layout_record.setEnabled(false);
		textview_action.setText("录音中...");
		mRecord.startRecordAndFile();
		pauseControl(false);
	}

	/**
	 * 停止录音
	 * 
	 */
	private void stopRecord() {
		isRecording = false;
		animationDrawable.stop();
		record_chronometer.stop();
		textview_action.setText("正在执行上传...");
		mRecord.stopRecordAndFile();
		audioFile = mRecord.getAudioFile();

		progressBar.setVisibility(View.VISIBLE);
		// 在录制完成时设置，在RecordTask的onPostExecute中完成
		biz.updateFileToMix(mContext, progressBar, ApiConfigs.selectId, audioFile, "amr", uploadHandler);
	}

	/**
	 * 合成功能后的设置
	 */
	private void mixed() {

		textview_action.setText("合成成功，点击可播放");
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
		textview_action.setText("重新合成中...");
		pauseControl(false);
	}

	/**
	 * 重置控件
	 */
	private void reSet() {
		animationDrawable.stop();
		record_chronometer.setVisibility(View.VISIBLE);
		record_chronometer.setBase(SystemClock.elapsedRealtime());
		imageview_play.setVisibility(View.VISIBLE);
		imageview_pause.setVisibility(View.GONE);
		layout_changed.setVisibility(View.GONE);
		layout_tools.setVisibility(View.GONE);
		textview_action.setText("点击开始录音");
		pauseControl(true);
		isMaxed = false;
		isReMix = false;
	}

	/**
	 * 录音过程禁止其它控件执行
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

	/* 判断退出操作 */
	public void exit() {
		if (isExit == false) {
			isExit = true;
			Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_LONG).show();
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

			textview_music_name.setText("歌曲：" + ApiConfigs.selectName);

		}
	};
}
