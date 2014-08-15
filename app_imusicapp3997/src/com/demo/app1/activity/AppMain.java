package com.demo.app1.activity;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.demo.app1.R;
import com.umeng.analytics.MobclickAgent;

public class AppMain extends Activity {
	private Context mContext;
	private TextView textView;
	private TextView textView1;
	private TextView textView2;
	private static String TAG = "3997tag";
	private TelephonyManager tm;
	private Handler resultHandler1;
	private Handler resultHandler2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		mContext = this;
		textView = (TextView) findViewById(R.id.result_imei);
		textView1 = (TextView) findViewById(R.id.result1);
		textView2 = (TextView) findViewById(R.id.result2);
		textView.setText(getDeviceInfo(mContext));
		tm = (android.telephony.TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
		// 使用普通测试流程，打开调试模式
		MobclickAgent.setDebugMode(true);
		String imsi = tm.getSubscriberId();
		String appid = "3997";
		initHandle();
		initData(mContext, imsi, appid, resultHandler1, resultHandler2);

	}

	private void initHandle() {
		resultHandler1 = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				if (msg.what == 1) {

					String result = (String) msg.obj;
					textView1.setText(URLDecoder.decode(result));
				} else {

				}
			}
		};

		resultHandler2 = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				if (msg.what == 1) {

					String result = (String) msg.obj;
					textView2.setText(HttpUtils.ascii2Native(result));
				} else {

				}
			}
		};
	}

	private void initData(final Context context, final String imsi, final String appid, final Handler handler1,
			final Handler handler2) {
		new Thread(new Runnable() {

			@Override
			public void run() {

				if (HttpUtils.isOpenNetwork(context) == true) {
					// http://app.imusicapp.cn/fengchao/rpc_ajax?appid=5883&op=getversion&IMSI=873366511151048
					String url = "http://app.imusicapp.cn/fengchao/rpc_ajax?appid=" + appid + "&op=getversion&IMSI=" + imsi;
					List<BasicNameValuePair> list = new ArrayList<BasicNameValuePair>();
					// list.add(new BasicNameValuePair("appid", "3997"));
					// list.add(new BasicNameValuePair("op", "getversion"));
					// list.add(new BasicNameValuePair("IMSI", imsi));
					String result = HttpUtils.postRequest(url, list);
					try {
						JSONObject jsonObj = new JSONObject(result);
						//{"client_version":"1.0.0","client_versioncode":"1","client_feature":""}
						String version=jsonObj.getString("client_version");
						String versioncode=jsonObj.getString("client_versioncode");
						String feature=jsonObj.getString("client_feature");
						
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					handler1.obtainMessage(1, result).sendToTarget();
				} else {
					handler1.obtainMessage(0, null).sendToTarget();
				}

			}
		}).start();

		new Thread(new Runnable() {

			@Override
			public void run() {

				if (HttpUtils.isOpenNetwork(context) == true) {

					String url = "http://app.imusicapp.cn/fengchao/preindex";
					Map<String, String> map = new HashMap<String, String>();
					map.put("appid", appid);
					map.put("IMSI", imsi);
					map.put("versioncode", "1");
					String result = HttpUtils.getRequest(url, map);
					handler2.obtainMessage(1, result).sendToTarget();
				} else {
					handler2.obtainMessage(0, null).sendToTarget();
				}

			}
		}).start();
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
			Intent intent = new Intent(mContext, AppcloudActivity.class);
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
			// String numer = tm.getLine1Number(); // 手机号码，有的可得，有的不可得
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
			if (imsi != null) {
				json.put("IMSI", imsi);
			}
			Log.i(TAG, "===>" + json.toString());
			return json.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
