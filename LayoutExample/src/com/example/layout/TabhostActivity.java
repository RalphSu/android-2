package com.example.layout;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabHost;

public class TabhostActivity extends TabActivity {
	private TabHost mHost;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tabhost);

		mHost = getTabHost();

		mHost.addTab(mHost.newTabSpec("Çú¿â").setIndicator("Çú¿â1").setContent(new Intent(getApplicationContext(), WebViewActivity.class)));
		mHost.addTab(mHost.newTabSpec("Çú¿â").setIndicator("Çú¿â2").setContent(new Intent(getApplicationContext(), WebViewActivity.class)));
		mHost.addTab(mHost.newTabSpec("Çú¿â").setIndicator("Çú¿â3").setContent(new Intent(getApplicationContext(), WebViewActivity.class)));
		mHost.addTab(mHost.newTabSpec("Çú¿â").setIndicator("Çú¿â4").setContent(new Intent(getApplicationContext(), WebViewActivity.class)));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.tabhost, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private TabHost.TabSpec buildTabSpec(String tag, int resLabel, int resIcon, final Intent content) {
		return this.mHost.newTabSpec(tag)
				.setIndicator(getString(resLabel), getResources().getDrawable(resIcon))
				.setContent(content);
	}
}
