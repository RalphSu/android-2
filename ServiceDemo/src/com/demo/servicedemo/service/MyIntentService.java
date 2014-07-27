package com.demo.servicedemo.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class MyIntentService extends IntentService {
	private static final String TAG=MyIntentService.class.getName();
public MyIntentService() {
	super("MyIntentService");
}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Log.i(TAG, Thread.currentThread().getId()+" onHandleIntent");
	}

}
