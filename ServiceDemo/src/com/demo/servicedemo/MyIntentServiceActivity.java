package com.demo.servicedemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.demo.servicedemo.service.MyIntentService;


public class MyIntentServiceActivity extends Activity {
	private static final String TAG=MyIntentService.class.getName();
	Button btnStartMyService,btnStopMyService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.my_service);
    	btnStartMyService=(Button)findViewById(R.id.start_btn);
    	btnStopMyService=(Button)findViewById(R.id.stop_btn);
    	
    	btnStartMyService.setOnClickListener(myListener);
    	btnStopMyService.setOnClickListener(myListener);
    	
    }
    
   private OnClickListener myListener=new OnClickListener() {
	
	@Override
	public void onClick(View v) {
		Intent intent=new Intent(MyIntentServiceActivity.this, MyIntentService.class);
		switch (v.getId()) {
		case R.id.start_btn:
			startService(intent);
			Log.i(TAG, Thread.currentThread().getId()+" onClick");
			break;
		case R.id.stop_btn:
			stopService(intent);
			break;
		default:
			break;
		}
		
	}
};
}
