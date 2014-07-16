package com.recordmix.biz;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.recordmix.utils.DatabaseUtil;
import com.recordmix.utils.DialogUtil;
import com.recordmix.utils.HttpMultipartPost;
import com.recordmix.utils.HttpUploadPost;
import com.recordmix.utils.HttpUtils;
import com.recordmix.utils.JsonUtils;
import com.recordmix.utils.MD5;

import android.R.integer;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.Handler;

public class BizManager {

	private List<HashMap<String, Object>> ringList;
	private List<HashMap<String, Object>> mixSongList;
	/**
	 * 单例模式
	 */
	private static BizManager instance;

	public static BizManager getInstance() {
		if (instance == null) {
			instance = new BizManager();
		}
		return instance;
	}

	/**
	 * 创建文件
	 * 
	 * @param fileName
	 * @return
	 */
	public File CreateFilePath(String pfx) {
		try {
			// 创建文件路径
			String dirPath = GetRootPath();
			// 创建目录
			File savePathDir = new File(dirPath);
			if (!savePathDir.exists()) {
				savePathDir.mkdirs();
			}
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH—mm-ss");
			String savePath = dirPath + "/" + sdf.format(date) + "." + pfx;
			File filePath = new File(savePath);
			boolean res = filePath.createNewFile();
			if (res) {
				return filePath;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return null;

	}

	/**
	 * 保存信息到数据库
	 * 
	 * @param fileName
	 */
	public void SaveRecordToDB(Context mContext, String fileName,
			String savePath) {

		DatabaseUtil dbUtil = new DatabaseUtil(mContext);
		dbUtil.open();
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
		dbUtil.createRecord(fileName, savePath, sdf.format(date));
		dbUtil.close();
	}

	/**
	 * 获取保存路径
	 * 
	 * @return
	 */
	public String GetRootPath() {

		return Environment.getExternalStorageDirectory().getAbsolutePath()
				+ "/RecordMix/files";
	}

	/**
	 * 获取网卡地址
	 * 
	 * @param context
	 * @return
	 */
	protected String getMac(final Context context) {
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
	protected String getPhoneKey(final Context context) {
		return MD5.Md5(getMac(context));
	}

	/**
	 * 进入程序时执行的操作
	 * 
	 * @param context
	 * @param handler
	 */
	public void getLogin(final Context context, final Handler handler) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub

				Map<String, String> params = new HashMap<String, String>();
				params.put("phoneKey", getPhoneKey(context));

				if (HttpUtils.isOpenNetwork(context) == true) {

					String jsonStr = HttpUtils.getRequest(ApiConfigs.loginLink
							,params);
					if (jsonStr.length() > 0) {
						System.out.println(">>>>>>>>>>getLogin:" + jsonStr);
						JSONObject jsonObj;
						try {
							jsonObj = new JSONObject(jsonStr);
							int status = jsonObj.getInt("status");
							String msg = jsonObj.getString("msg");
							if (status == 1) {
//								ApiConfigs.uuid = jsonObj.getString("uuid");
								ApiConfigs.musicId = jsonObj
										.getString("musicId");
							}
							handler.obtainMessage(status, msg).sendToTarget();
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							DialogUtil.getInstance().ShowToast(context,
									"数据请求出现问题");
						}
					} else {
						DialogUtil.getInstance().ShowToast(context, "数据请求出现问题");
					}
				}
			}
		}).start();

	}

	/**
	 * 用户变音歌曲列表
	 * 
	 * @param context
	 * @param page
	 * @param size
	 * @param type
	 *            默认type=0，创建时间降序；type=1，根据点赞数的降序
	 * @param handler
	 */
	public void getMixSongList(final Context context, final int page,
			final int size, final int type, final Handler handler) {

		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Map<String, String> params = new HashMap<String, String>();
				params.put("phoneKey", getPhoneKey(context));
				params.put("currentPage", Integer.toString(page));
				params.put("pageSize", Integer.toString(size));
				params.put("type", Integer.toString(type));

				if (HttpUtils.isOpenNetwork(context) == true) {

					String jsonStr = HttpUtils.getRequest(
							ApiConfigs.requestLink + "getSongJsonList", params);
					if (jsonStr.length() > 0) {
						mixSongList = JsonUtils
								.getMixSongList(context, jsonStr);
					}
					handler.obtainMessage(1, mixSongList).sendToTarget();
				} else {
					handler.obtainMessage(0, null).sendToTarget();
				}

			}
		}).start();

	}

	/**
	 * 曲库列表
	 * 
	 * @param context
	 * @param page
	 * @param size
	 * @param type
	 *            默认传递type=0，获取所有；type=1是经典；type=2是流行
	 * @param handler
	 */
	public void getRingList(final Context context, final int page,
			final int size, final int type, final Handler handler) {

		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Map<String, String> params = new HashMap<String, String>();
				params.put("phoneKey", getPhoneKey(context));
				params.put("currentPage", Integer.toString(page));
				params.put("pageSize", Integer.toString(size));
				params.put("type", Integer.toString(type));

				if (HttpUtils.isOpenNetwork(context) == true) {

					String jsonStr = HttpUtils
							.getRequest(ApiConfigs.requestLink
									+ "getMusicJsonList", params);
					if (jsonStr.length() > 0) {
						ringList = JsonUtils.getRingList(context, jsonStr);
					}
					handler.obtainMessage(1, ringList).sendToTarget();
				} else {
					handler.obtainMessage(0, null).sendToTarget();
				}

			}
		}).start();

	}

	/**
	 * 上传录音文件
	 * 
	 * @param context
	 * @param tempId
	 * @param audioFile
	 */
	public void updateFileToMix(Context context, String id, File audioFile,
			String speechfmt, Handler handler) {

		String urlStr = ApiConfigs.requestLink + "compoundSong";
		String phoneKey = getPhoneKey(context);
		String appid = ApiConfigs.appid;
		String appkey = ApiConfigs.appkey;
		HttpUploadPost post = new HttpUploadPost(context, urlStr, appid,
				appkey, speechfmt, id, phoneKey, audioFile, handler);
		post.execute();
	}

	/**
	 * 变音歌曲合成状态查询
	 * 
	 * @param context
	 * @param songId
	 * @param handler
	 */
	public void checkMixMusic(final Context context, final String songId,
			final String speechfmt, final Handler handler) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Map<String, String> params = new HashMap<String, String>();
				params.put("songId", songId);
				params.put("appid", ApiConfigs.appid);
				params.put("appkey", ApiConfigs.appkey);
				params.put("phoneKey", getPhoneKey(context));
				if (HttpUtils.isOpenNetwork(context) == true) {

					String jsonStr = HttpUtils.getRequest(
							ApiConfigs.requestLink + "getSongResStatus", params);
					if (jsonStr.length() > 0) {
						JSONObject jsonObj;
						System.out.println(">>>>>>>>>>---------checkMixMusic合成音乐查询："+jsonStr);
						try {
							jsonObj = new JSONObject(jsonStr);
							int status = jsonObj.getInt("status");
							String msg = jsonObj.getString("msg");
							if (status == 1) {
								String songUrl = jsonObj.getString("songUrl");
								handler.obtainMessage(1, songUrl)
										.sendToTarget();
							} else if(status==19) {
								Thread.sleep(500);
								checkMixMusic(context,songId,speechfmt,handler);
							}
							else
							{
								handler.obtainMessage(status, msg).sendToTarget();
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							DialogUtil.getInstance().ShowToast(context,
									"歌曲合成失败");
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

				} else {
					handler.obtainMessage(0, null).sendToTarget();
				}

			}
		}).start();

	}

	public void setPraise(final Context context, final int songId,
			final Handler handler) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub

				Map<String, String> params = new HashMap<String, String>();
				params.put("id", Integer.toString(songId));

				if (HttpUtils.isOpenNetwork(context) == true) {

					String jsonStr = HttpUtils.getRequest(
							ApiConfigs.requestLink + "setClickPraiseNum",
							params);
					if (jsonStr.length() > 0) {
						System.out.println(">>>>>>>>>>setPraise:" + jsonStr);
						JSONObject jsonObj;
						try {
							jsonObj = new JSONObject(jsonStr);
							int status = jsonObj.getInt("status");
							String msg = jsonObj.getString("msg");
							handler.obtainMessage(status, msg).sendToTarget();
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else {
						DialogUtil.getInstance().ShowToast(context, "数据请求出现问题");
					}
				}
			}
		}).start();
	}
}
