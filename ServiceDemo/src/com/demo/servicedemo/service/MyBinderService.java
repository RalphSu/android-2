package com.demo.servicedemo.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class MyBinderService extends Service {

	private MyBinder myBinder=new MyBinder();
	@Override
	public IBinder onBind(Intent intent) {
		return myBinder;
	}

	
	public class MyBinder extends Binder{
		
		public  MyBinderService getBinderService() {
			return MyBinderService.this;
		}
	}
	
	public void sayHello() {
		Log.i("MyBinderServiceTag", "hello !");
	}
}
