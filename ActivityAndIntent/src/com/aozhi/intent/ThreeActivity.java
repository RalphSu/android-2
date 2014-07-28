package com.aozhi.intent;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class ThreeActivity extends Activity {
	private Context mContext;
	private TextView textView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.three);
		mContext = this;
		
		textView=(TextView)findViewById(R.id.result3);
		//textView.setText("test2");
		IntentFilter filter=new IntentFilter();
		filter.addAction("CHANG");
		mContext.registerReceiver(receiver, filter);
	}



	public void onButtonClick(View view) {
		int id = view.getId();
		switch (id) {
		case R.id.button31:
			Intent intent=new Intent(mContext, HomeActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}

	}

	private BroadcastReceiver receiver=new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle bundle=intent.getExtras();
			Log.i("tag", "===============>onReceive "+bundle.containsKey("test2")+" "+bundle.getString("test2"));
			textView.setText(bundle.getString("test2"));
			
		}
	};
	
}
