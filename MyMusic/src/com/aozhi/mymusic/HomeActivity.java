package com.aozhi.mymusic;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;

public class HomeActivity extends TabActivity implements OnTabChangeListener {
	private TabHost tabHost;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
		tabHost = getTabHost();
		tabHost.setOnTabChangedListener(this);
		tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator("t", getResources().getDrawable(R.drawable.share_wx)).setContent(new Intent(this, List1.class)));
		tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator("test2").setContent(new Intent(this, List1.class)));
		tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator("test3").setContent(new Intent(this, List1.class)));
	}

	@Override
	public void onTabChanged(String tabId) {
		// TODO Auto-generated method stub

	}

}
