package com.aozhi.intent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class SecondActivity extends Activity {
	private Context mContext;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.second);
		mContext = this;
		
		Intent intent=getIntent();
		TextView textView=(TextView) findViewById(R.id.result2);
		textView.setText(intent.getExtras().getString("test"));
	}

	public void onButtonClick(View view) {
		int id = view.getId();
		switch (id) {
		case R.id.button21:
			Intent intent0=new Intent();
			intent0.putExtra("test2", "56789");
			intent0.setAction("CHANG");
			mContext.sendBroadcast(intent0);
			
			Intent intent = new Intent(mContext, ThreeActivity.class);
			startActivity(intent);
			break;
		case R.id.button22:
			Intent intent2 = new Intent();
			intent2.putExtra("password", "654321");
			setResult(1, intent2);
			finish();
			break;
		default:
			break;
		}

	}

	
}
