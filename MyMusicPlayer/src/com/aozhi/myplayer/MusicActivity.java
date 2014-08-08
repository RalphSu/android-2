package com.aozhi.myplayer;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import com.aozhi.myplayer.t2.List1;

public class MusicActivity extends TabActivity {

	private TabHost tabHost;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.player_home);

		final TabHost tabHost = getTabHost();

		tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator("∏Ë«˙").setContent(new Intent(this, List1.class)));
		tabHost.addTab(tabHost.newTabSpec("tab2").setIndicator("“’ ıº“").setContent(new Intent(this, List1.class)));
		
	}

}
