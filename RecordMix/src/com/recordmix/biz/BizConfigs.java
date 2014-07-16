package com.recordmix.biz;

import com.recordmix.utils.MD5;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public class BizConfigs {

	private Context context;

	public BizConfigs(Context mContext) {
		context = mContext;
	}

	protected String appid() {
		return "9sky0001";
	}

	protected String appkey() {
		return "27f2acb90f4b5147074b331db1c61cc7";
	}

	protected String apiLink() {
		return "http://muse.yun.fm/api/";
	}
	protected String loginLink()
	{
		return "http://218.244.128.50:8080/app/phone/getUuidAndMusicId?phoneKey=";
	}
	protected String requestLink()
	{
		return "http://218.244.128.50:8080/app/appSong/";
	}
	protected String uuid() {
		return getUUID();
	}

	protected String getMac() {
		try {
			WifiManager wifi = (WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);

			WifiInfo info = wifi.getConnectionInfo();

			return info.getMacAddress();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取用户唯一值
	 * 
	 * @return
	 */
	protected String getUUID() {
		return MD5.Md5(getMac());
	}

}
