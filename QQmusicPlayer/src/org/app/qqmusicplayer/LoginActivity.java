package org.app.qqmusicplayer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class LoginActivity extends Activity {
/**等待时间的常量**/
private static final int WAITTIME=1500;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		/***用Handler的定时器方法设定一段时间并开启一个线程跳转到另一个Activity**/
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				Intent intent=new Intent(LoginActivity.this,TabHostMainActivity.class);
				startActivity(intent);
				finish();
				
			}
		}, WAITTIME);
		
	}



}
