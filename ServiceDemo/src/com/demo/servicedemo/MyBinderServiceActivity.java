package com.demo.servicedemo;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.demo.servicedemo.service.MyBinderService;
import com.demo.servicedemo.service.MyBinderService.MyBinder;

public class MyBinderServiceActivity extends Activity {

	Button btnStartMyService, btnStopMyService;
	private boolean isConnected=false;
	private MyBinderService myService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_service);
		btnStartMyService = (Button) findViewById(R.id.start_btn);
		btnStopMyService = (Button) findViewById(R.id.stop_btn);

		btnStartMyService.setOnClickListener(myListener);
		btnStopMyService.setOnClickListener(myListener);
		
		bindService();

	}

	private OnClickListener myListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.start_btn:
				myService.sayHello();
				break;
			case R.id.stop_btn:
				unbindService();
				break;
			default:
				break;
			}

		}

		
	};

	private void unbindService() {
		if (isConnected) {
			unbindService(connection);
		}

	}

	private void bindService() {
		Intent intent = new Intent(MyBinderServiceActivity.this, MyBinderService.class);
		bindService(intent, connection, Context.BIND_AUTO_CREATE);

	}
	ServiceConnection connection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			isConnected=false;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			MyBinder myBinder = (MyBinder) service;
			myService = myBinder.getBinderService();
			isConnected=true;
		}
	};
}
