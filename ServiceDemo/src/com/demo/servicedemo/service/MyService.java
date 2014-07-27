package com.demo.servicedemo.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class MyService extends Service {
	
	private String Tag="MyServiceTag";

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		Log.i(Tag,"====================>MyService onCreate()");
		super.onCreate();
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		Log.i(Tag,"====================>MyService onStart()");
		super.onStart(intent, startId);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(Tag,"====================>MyService onStartCommand()");
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public void onDestroy() {
		Log.i(Tag,"====================>MyService onDestroy()");
		super.onDestroy();
	}
	
}
