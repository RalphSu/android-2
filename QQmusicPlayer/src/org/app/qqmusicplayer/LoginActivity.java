package org.app.qqmusicplayer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class LoginActivity extends Activity {
/**�ȴ�ʱ��ĳ���**/
private static final int WAITTIME=1500;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		/***��Handler�Ķ�ʱ�������趨һ��ʱ�䲢����һ���߳���ת����һ��Activity**/
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
