package com.anjoyo.qq_music;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends Activity {
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		final Intent intent = new Intent(MainActivity.this, TabHostActivity.class);
		new Thread(new Runnable() {
			public void run() {
				try {
					Thread.sleep(2000);
					startActivity(intent);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();

	}

	/*
	 * public boolean onCreateOptionsMenu(Menu menu) { getMenuInflater().inflate(R.menu.main, menu); return true; }
	 */

	protected void onPause() {
		super.onPause();
		finish();
	}

}
