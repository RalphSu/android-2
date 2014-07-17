package com.mixmusic.view;

import java.util.List;

import com.mixmusic.R;
import com.mixmusic.biz.AppConstant;
import com.mixmusic.service.PlayerService;
import com.mixmusic.utils.OnTabActivityResultListener;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

public class TabMainActivity extends TabActivity implements
		TabHost.OnTabChangeListener {

	private TabHost mTabHost;
	private static final int TAB_INDEX_RECORD = 0;
	private static final int TAB_INDEX_MINE = 1;
	private static final int TAB_INDEX_LIBRARY = 2;
	private static final int TAB_INDEX_FIND = 3;
	private boolean isExit; // 判断是否退出
	private Context mContext;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tabhost_main);
		mTabHost = getTabHost();
		mTabHost.setOnTabChangedListener(this);
		mContext = TabMainActivity.this;
		setupRecordTab();
		setupLibraryTab();
		setupFindTab();
		setupMineTab();

		mTabHost.setCurrentTab(0);
	}

	/**
	 * 我的
	 */
	private void setupRecordTab() {
		Intent intent = new Intent();
		intent.setClass(this, RecordActivity.class);

		RelativeLayout tabStyle1 = (RelativeLayout) LayoutInflater.from(this)
				.inflate(R.layout.tab_main_style, null);
		TextView text1 = (TextView) tabStyle1.findViewById(R.id.tab_label);
		text1.setText("幻 音");

		mTabHost.addTab(mTabHost.newTabSpec("幻 音").setIndicator(tabStyle1)
				.setContent(intent));
	}

	/**
	 * 曲库
	 */
	private void setupLibraryTab() {
		Intent intent = new Intent();
		intent.setClass(this, LibraryActivity.class);

		RelativeLayout tabStyle1 = (RelativeLayout) LayoutInflater.from(this)
				.inflate(R.layout.tab_main_style, null);
		TextView text1 = (TextView) tabStyle1.findViewById(R.id.tab_label);
		text1.setText("曲 库");

		mTabHost.addTab(mTabHost.newTabSpec("曲 库").setIndicator(tabStyle1)
				.setContent(intent));

	}

	/**
	 * 发现
	 */
	private void setupFindTab() {
		Intent intent = new Intent();
		intent.setClass(this, FindActivity.class);

		RelativeLayout tabStyle1 = (RelativeLayout) LayoutInflater.from(this)
				.inflate(R.layout.tab_main_style, null);
		TextView text1 = (TextView) tabStyle1.findViewById(R.id.tab_label);
		text1.setText("发 现");

		mTabHost.addTab(mTabHost.newTabSpec("发 现").setIndicator(tabStyle1)
				.setContent(intent));

	}

	/**
	 * 我的
	 */
	private void setupMineTab() {
		Intent intent = new Intent();
		intent.setClass(this, MineActivity.class);

		RelativeLayout tabStyle1 = (RelativeLayout) LayoutInflater.from(this)
				.inflate(R.layout.tab_main_style, null);
		TextView text1 = (TextView) tabStyle1.findViewById(R.id.tab_label);
		text1.setText("我 的");

		mTabHost.addTab(mTabHost.newTabSpec("我 的").setIndicator(tabStyle1)
				.setContent(intent));

	}

	@Override
	public void onTabChanged(String tabId) {
		mTabHost.setCurrentTabByTag(tabId);

		updateTab(mTabHost);
		// TODO Auto-generated method stub
		@SuppressWarnings("deprecation")
		Activity activity = getLocalActivityManager().getActivity(tabId);
		if (activity != null) {
			Intent i = new Intent();
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			i.putExtra("PLAY_MSG", AppConstant.PlayerMsg.STOP_MSG);
			i.setClass(activity, PlayerService.class);
			activity.startService(i);

			System.out.println("-----onTabChanged-------stopService");
			activity.onWindowFocusChanged(true);
		}

	}

	/**
	 * 显示隐藏图标
	 * 
	 * @param currentTab
	 */
	private void updateTab(TabHost currentTab) {
		// TODO Auto-generated method stub
		for (int i = 0; i < currentTab.getTabWidget().getChildCount(); i++) {
			View tabView = currentTab.getTabWidget().getChildAt(i);
			TextView text = (TextView) tabView.findViewById(R.id.tab_label);

			// ImageView img =(ImageView)
			// tabView.findViewById(R.id.imageview_selected);
			if (currentTab.getCurrentTab() == i) {
				// 选中后的背景
				// img.setVisibility(View.VISIBLE);
				text.setTextColor(getResources().getColor(
						R.color.tab_text_selector));
			} else {
				// 非选择的背景
				// img.setVisibility(View.GONE);
				text.setTextColor(getResources().getColor(
						R.color.tab_text_select));
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub

		// 获取当前活动的Activity实例
		Activity subActivity = getLocalActivityManager().getCurrentActivity();
		// 判断是否实现返回值接口
		if (subActivity instanceof OnTabActivityResultListener) {
			// 获取返回值接口实例
			OnTabActivityResultListener listener = (OnTabActivityResultListener) subActivity;
			// 转发请求到子Activity
			listener.onTabActivityResult(requestCode, resultCode, data);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public static boolean isServiceRunning(Context mContext, String className) {

		boolean isRunning = false;
		ActivityManager activityManager = (ActivityManager) mContext
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> serviceList = activityManager
				.getRunningServices(30);

		if (!(serviceList.size() > 0)) {
			return false;
		}

		for (int i = 0; i < serviceList.size(); i++) {
			if (serviceList.get(i).service.getClassName().equals(className) == true) {
				isRunning = true;
				break;
			}
		}
		return isRunning;
	}
}
