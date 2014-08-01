package com.demo.app1.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.demo.app1.R;
import com.umeng.analytics.MobclickAgent;

public class AppMain extends Activity {
	private Context mContext;
	private TextView textView;
	private static String TAG="myapptag";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		mContext = this;
		textView=(TextView)findViewById(R.id.result_imei);
		textView.setText(getDeviceInfo(mContext));
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
		case R.id.toAppcloudActivity:
			MobclickAgent.onEvent(mContext, "test");
			MobclickAgent.onEvent(mContext, "test", "button");
			Intent intent=new Intent(mContext,AppcloudActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}

	}

	public static String getDeviceInfo(Context context) {
		try {
			org.json.JSONObject json = new org.json.JSONObject();
			android.telephony.TelephonyManager tm = (android.telephony.TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);

             String imsi = tm.getSubscriberId();    
             String mtype = android.os.Build.MODEL; // 手机型号    
             //String numer = tm.getLine1Number(); // 手机号码，有的可得，有的不可得   
			String device_id = tm.getDeviceId();

			android.net.wifi.WifiManager wifi = (android.net.wifi.WifiManager) context.getSystemService(Context.WIFI_SERVICE);

			String mac = wifi.getConnectionInfo().getMacAddress();
			json.put("mac", mac);

			if (TextUtils.isEmpty(device_id)) {
				device_id = mac;
			}

			if (TextUtils.isEmpty(device_id)) {
				device_id = android.provider.Settings.Secure.getString(context.getContentResolver(),
						android.provider.Settings.Secure.ANDROID_ID);
			}

			json.put("device_id", device_id);
			json.put("手机型号", mtype);
			if (imsi!=null) {
				json.put("IMSI", imsi);
			}
			Log.i(TAG, "===>"+json.toString());
			return json.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
