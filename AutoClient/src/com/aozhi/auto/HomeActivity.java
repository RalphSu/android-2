package com.aozhi.auto;

import java.util.HashMap;
import java.util.Map;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;
import com.umeng.analytics.MobclickAgent;

public class HomeActivity extends Activity {
	private Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
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
		case R.id.umeng_example_analytics_online_config:
			// 组件->在线参数 中新增一个 key=mydemo value=123456789
			String onlineParams = MobclickAgent.getConfigParams(mContext, "abc");
			if (onlineParams.equals("")) {
				Toast.makeText(mContext, "没有获取到在线参数!", Toast.LENGTH_SHORT).show();
			} else
				Toast.makeText(mContext, "在线参数值为:" + onlineParams, Toast.LENGTH_SHORT).show();
			break;
		case R.id.umeng_example_analytics_event:
			MobclickAgent.onEvent(mContext, "click");
			MobclickAgent.onEvent(mContext, "click", "button");
			break;
		case R.id.umeng_example_analytics_ekv:
			Map<String, String> map_ekv = new HashMap<String, String>();
			map_ekv.put("name", "flyxxx");
			map_ekv.put("password", "123456");

			MobclickAgent.onEvent(mContext, "music", map_ekv);
			break;
		case R.id.umeng_example_analytics_event_begin:
			//when the events start
			MobclickAgent.onEventBegin(mContext, "music");
			
			MobclickAgent.onEventBegin(mContext, "music", "one");
			
			Map<String, String> map = new HashMap<String, String>();
			map.put("type", "popular");
			map.put("artist", "JJLin");
			
			MobclickAgent.onKVEventBegin(mContext, "music", map, "flag0");
			break;
		case R.id.umeng_example_analytics_event_end:
			
			MobclickAgent.onEventEnd(mContext, "music");
			MobclickAgent.onEventEnd(mContext, "music", "one");	
			
			MobclickAgent.onKVEventEnd(mContext, "music", "flag0");
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

			return json.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
