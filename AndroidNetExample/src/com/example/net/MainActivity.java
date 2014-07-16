package com.example.net;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void image(View view) {
		Intent intent = new Intent(this, ImageActivity.class);
		startActivity(intent);
	}
	
	public void html(View view) {
		Intent intent=new Intent(this,HtmlActivity.class);
		startActivity(intent);
	}
	public void doGet(View view) {
		Intent intent = new Intent(this, GETandPOSTActivity.class);
		startActivity(intent);
	}
	/**
	 * 打开其他应用
	 * @param view
	 */
	public void openActivity(View view) {
		Intent intent = new Intent(this, GETandPOSTActivity.class);
		intent.setClassName("com.example.data", "com.example.data.MainActivity");
		//com.example.android.MainActivity
		startActivity(intent);
	}

}
