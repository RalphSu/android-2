package com.mixmusic.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import android.widget.Toast;

public class JsonUtils {

	/**
	 * 获取歌曲模板列表
	 * 
	 * @param jsonStr
	 * @return
	 */
	public static List<HashMap<String, Object>> getTempRingList(Context context, String jsonStr) {

		List<HashMap<String, Object>> RingList = new ArrayList<HashMap<String, Object>>();

		try {
			JSONObject jsonObj = new JSONObject(jsonStr);
			int status = jsonObj.getInt("status");
			String msg = jsonObj.getString("msg");
			if (status == 1) {
				// 获取items列表数据
				JSONArray jsonArray = jsonObj.getJSONArray("items");
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject obj = jsonArray.getJSONObject(i);
					HashMap<String, Object> map = new HashMap<String, Object>();
					map.put("id", obj.getString("id"));
					map.put("title", obj.getString("title"));
					RingList.add(map);
				}
				System.out.println(">>>>-------getTempRingList:" + RingList.toString());
				return RingList;
			} else {
				DialogUtil.getInstance().ShowToast(context, msg);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 合成歌曲列表
	 * 
	 * @param context
	 * @param jsonStr
	 * @return
	 */
	public static List<HashMap<String, Object>> getMixSongList(Context context, String jsonStr) {

		List<HashMap<String, Object>> RingList = new ArrayList<HashMap<String, Object>>();

		try {
			JSONObject jsonObj = new JSONObject(jsonStr);
			int status = jsonObj.getInt("status");
			String msg = jsonObj.getString("msg");
			// int totalCount = jsonObj.getInt("totalCount");
			int pageSize = jsonObj.getInt("pageSize");
			int currentPage = jsonObj.getInt("currentPage");
			if (status == 1) {
				// 获取items列表数据
				JSONArray jsonArray = jsonObj.getJSONArray("result");
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject obj = jsonArray.getJSONObject(i);
					HashMap<String, Object> map = new HashMap<String, Object>();
					map.put("id", obj.getString("id"));
					map.put("songName", obj.getString("songName"));
					map.put("description", obj.getString("description"));
					map.put("clickNum", obj.getString("clickNum"));
					map.put("ringtoneNum", obj.getString("ringtoneNum"));
					map.put("colorRingToneNum", obj.getString("colorRingToneNum"));
					map.put("songUrl", obj.getString("songUrl"));
					map.put("createDate", obj.getString("createDate"));
					map.put("songType", obj.getString("songType"));
					map.put("contentType", obj.getString("contentType"));
					RingList.add(map);
				}
				System.out.println(">>>>>>>>>>合成歌曲列表------getMixSongList:" + RingList.toString());
				return RingList;
			} else {
				DialogUtil.getInstance().ShowToast(context, msg);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 曲库列表
	 * 
	 * @param context
	 * @param jsonStr
	 * @return
	 */
	public static List<HashMap<String, Object>> getRingList(Context context, String jsonStr) {

		List<HashMap<String, Object>> RingList = new ArrayList<HashMap<String, Object>>();

		try {
			JSONObject jsonObj = new JSONObject(jsonStr);
			int status = jsonObj.getInt("status");
			String msg = jsonObj.getString("msg");
			if (status == 1) {
				// 获取items列表数据
				JSONArray jsonArray = jsonObj.getJSONArray("result");
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject obj = jsonArray.getJSONObject(i);
					HashMap<String, Object> map = new HashMap<String, Object>();
					map.put("id", obj.getString("id"));
					map.put("musicName", obj.getString("musicName"));
					map.put("description", obj.getString("description"));
					map.put("musicUrl", obj.getString("musicUrl"));
					map.put("createDate", obj.getString("createDate"));
					map.put("musicType", obj.getString("musicType"));
					map.put("contentType", obj.getString("contentType"));
					RingList.add(map);
				}
				return RingList;
			} else {
				DialogUtil.getInstance().ShowToast(context, msg);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
