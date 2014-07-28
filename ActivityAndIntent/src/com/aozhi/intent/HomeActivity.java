package com.aozhi.intent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class HomeActivity extends Activity {
	private Context mContext;
	private static final int REQUESTCODE = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		mContext = this;

	}

	public void onButtonClick(View view) {
		int id = view.getId();
		switch (id) {
		case R.id.button11:
			Intent intent = new Intent(mContext, SecondActivity.class);
			intent.putExtra("test", "123456");
			startActivityForResult(intent, REQUESTCODE);
			break;
		default:
			break;
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (REQUESTCODE == requestCode) {
			if (resultCode == 1) {
				Bundle bundle = data.getExtras();
				String result = bundle.getString("password");
				Toast.makeText(mContext, result, Toast.LENGTH_LONG).show();
			}
		}
	}

}
