package com.demo.servicedemo;

import com.demo.servicedemo.service.MyService;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


public class MyServiceActivity extends Activity {

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
		Intent intent=new Intent(MyServiceActivity.this, MyService.class);
		switch (v.getId()) {
		case R.id.start_btn:
			startService(intent);
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
