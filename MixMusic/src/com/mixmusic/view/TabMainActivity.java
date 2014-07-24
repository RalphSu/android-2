package com.mixmusic.view;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;

import com.mixmusic.R;
import com.mixmusic.biz.AppConstant;
import com.mixmusic.service.PlayerService;
import com.mixmusic.utils.OnTabActivityResultListener;

public class TabMainActivity extends TabActivity implements TabHost.OnTabChangeListener {

	private TabHost mTabHost;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.tabhost_main);
		mTabHost = getTabHost();
		mTabHost.setOnTabChangedListener(this);
		setupAppTab();
		mTabHost.setCurrentTab(0);
	}

	private void setupAppTab() {

		buildTab("����", RecordActivity.class);
		buildTab("�� ��", LibraryActivity.class);
		buildTab("�� ��", FindActivity.class);
		buildTab("�� ��", MineActivity.class);
	}

	private void buildTab(String label, Class class1) {
		Intent intent = new Intent(this, class1);

		RelativeLayout tabStyle1 = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.tab_main_style, null);
		TextView text1 = (TextView) tabStyle1.findViewById(R.id.tab_label);
		text1.setText(label);

		TabHost.TabSpec tabSpec = mTabHost.newTabSpec(label).setIndicator(tabStyle1).setContent(intent);
		mTabHost.addTab(tabSpec);
	}

	@Override
	public void onTabChanged(String tabId) {
		mTabHost.setCurrentTabByTag(tabId);

		updateTab(mTabHost);

		@SuppressWarnings("deprecation")
		Activity activity = getLocalActivityManager().getActivity(tabId);
		if (activity != null) {
			Intent i = new Intent();
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			i.putExtra("PLAY_MSG", AppConstant.PlayerMsg.STOP_MSG);
			i.setClass(activity, PlayerService.class);
			activity.startService(i);

			activity.onWindowFocusChanged(true);
		}

	}

	/**
	 * �л�tabʱ��ѡ��tab��������ɫ
	 * 
	 * @param currentTab
	 */
	private void updateTab(TabHost currentTab) {

		for (int i = 0; i < currentTab.getTabWidget().getChildCount(); i++) {
			View tabView = currentTab.getTabWidget().getChildAt(i);
			TextView text = (TextView) tabView.findViewById(R.id.tab_label);

			if (currentTab.getCurrentTab() == i) {
				// ѡ�к�ı���
				text.setTextColor(getResources().getColor(R.color.tab_text_selector));
			} else {
				// ��ѡ��ı���
				text.setTextColor(getResources().getColor(R.color.tab_text_select));
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		// ��ȡ��ǰ���Activityʵ��
		Activity subActivity = getLocalActivityManager().getCurrentActivity();
		// �ж��Ƿ�ʵ�ַ���ֵ�ӿ�
		if (subActivity instanceof OnTabActivityResultListener) {
			// ��ȡ����ֵ�ӿ�ʵ��
			OnTabActivityResultListener listener = (OnTabActivityResultListener) subActivity;
			// ת��������Activity
			listener.onTabActivityResult(requestCode, resultCode, data);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
