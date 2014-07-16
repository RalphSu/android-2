package com.example.phone;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class SendMessageActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_send_message);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.send_message, menu);
		return true;
	}

	public void sendMessage(View view) {

		EditText textPhoneNo = (EditText) findViewById(R.id.editTextPhoneNo);
		EditText textSMS = (EditText) findViewById(R.id.editTextSMS);

		String phoneNo = textPhoneNo.getText().toString();
		String sms = textSMS.getText().toString();

		try {
			SmsManager smsManager = SmsManager.getDefault();
			List<String> list=smsManager.divideMessage(sms);
			for (String string : list) {
				smsManager.sendTextMessage(phoneNo, null, string, null, null);
			}
			Toast.makeText(getApplicationContext(), "¶ÌÐÅ·¢ËÍ!", Toast.LENGTH_LONG)
					.show();
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(),
					"SMS faild, please try again later!", Toast.LENGTH_LONG)
					.show();
			e.printStackTrace();
		}

	}

}
