package com.example.phone;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {
	private EditText editText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		editText = (EditText) findViewById(R.id.editText);
		Button button = (Button) findViewById(R.id.sendButton);
		button.setOnClickListener(new sendMessageOnClickListener());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void callPhone(View view) {
		Intent intent = new Intent();
		String number = editText.getText().toString();
		intent.setAction("android.intent.action.CALL");
		intent.setData(Uri.parse("tel:" + number));
		startActivity(intent);// 方法内部会自动为Intent添加类别：android.intent.category.DEFAULT
	}
	
	public void sendMessage(View view) {
		Intent intent=new Intent(this, SendMessageActivity.class);
		startActivity(intent);
	}

	private class sendMessageOnClickListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			try {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.putExtra("sms_body", "default content");
				intent.setType("vnd.android-dir/mms-sms");
				startActivity(intent);

			} catch (Exception e) {
				Toast.makeText(getApplicationContext(),
						"SMS faild, please try again later!", Toast.LENGTH_LONG)
						.show();
				e.printStackTrace(); 
			}
		}
	}
}
