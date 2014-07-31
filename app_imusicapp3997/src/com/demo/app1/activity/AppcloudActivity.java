package com.demo.app1.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.demo.app1.R;
import com.umeng.analytics.MobclickAgent;

public class AppcloudActivity extends Activity {
	private Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.appcloud);
		mContext = this;
		// 使用普通测试流程，打开调试模式
		MobclickAgent.setDebugMode(true);

	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(mContext);
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(mContext);
	}

	public void onButtonClick(View view) {
		int id = view.getId();
		switch (id) {
		case R.id.returnMain:
			Intent intent = new Intent(mContext, AppMain.class);
			startActivity(intent);
			break;
		default:
			break;
		}

	}

}
