package com.aozhi.myplayer;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;

public class MyPlayerActivity extends TabActivity {

	private TabHost tabHost;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.player_home);

		final TabHost tabHost = getTabHost();

		tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator("¸èÇú").setContent(new Intent(this, MyMusicList.class)));
		tabHost.addTab(tabHost.newTabSpec("tab2").setIndicator("³ªÆ¬").setContent(new Intent(this, MyMusicList.class)));
		
	}

}
