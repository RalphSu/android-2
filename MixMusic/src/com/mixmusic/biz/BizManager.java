package com.mixmusic.biz;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.ProgressBar;

import com.mixmusic.utils.DatabaseUtil;
import com.mixmusic.utils.DialogUtil;
import com.mixmusic.utils.HttpUploadPost;
import com.mixmusic.utils.HttpUtils;
import com.mixmusic.utils.JsonUtils;
import com.mixmusic.utils.MD5;

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
	public void SaveRecordToDB(Context mContext, String fileName, String savePath) {

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

		return Environment.getExternalStorageDirectory().getAbsolutePath() + "/MixMusic/files";
	}

	/**
	 * 获取网卡地址
	 * 
	 * @param context
	 * @return
	 */
	protected String getMac(final Context context) {
		try {
			WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

			WifiInfo info = wifi.getConnectionInfo();

			return info.getMacAddress();
		} catch (Exception e) {
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

				Map<String, String> params = new HashMap<String, String>();
				params.put("phoneKey", getPhoneKey(context));

				if (HttpUtils.isOpenNetwork(context) == true) {

					String jsonStr = HttpUtils.getRequest(ApiConfigs.loginLink, params);
					if (jsonStr.length() > 0) {
						System.out.println(">>>>>>>>>>getLogin:" + jsonStr);
						JSONObject jsonObj;
						try {
							jsonObj = new JSONObject(jsonStr);
							int status = jsonObj.getInt("status");
							String msg = jsonObj.getString("msg");
							if (status == 1) {

								ApiConfigs.selectId = jsonObj.getString("musicId");
								ApiConfigs.selectName = jsonObj.getString("musicName");
							}
							handler.obtainMessage(status, msg).sendToTarget();
						} catch (JSONException e) {
							
							e.printStackTrace();
							DialogUtil.getInstance().ShowToast(context, "数据请求出现问题");
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
	 * @param handler
	 */
	public void getMySongList(final Context context, final int page, final int size, final Handler handler) {

		new Thread(new Runnable() {

			@Override
			public void run() {

				Map<String, String> params = new HashMap<String, String>();
				params.put("phoneKey", getPhoneKey(context));
				params.put("currentPage", Integer.toString(page));
				params.put("pageSize", Integer.toString(size));

				if (HttpUtils.isOpenNetwork(context) == true) {

					String jsonStr = HttpUtils.getRequest(ApiConfigs.requestLink + "getMySongJsonList", params);
					if (jsonStr.length() > 0) {
						mixSongList = JsonUtils.getMixSongList(context, jsonStr);
					}
					handler.obtainMessage(1, mixSongList).sendToTarget();
				} else {
					handler.obtainMessage(0, null).sendToTarget();
				}

			}
		}).start();

	}

	/**
	 * 发现列表
	 * 
	 * @param context
	 * @param page
	 * @param size
	 * @param type
	 *            默认type=0，创建时间降序；type=1，根据点赞数的降序
	 * @param handler
	 */
	public void getAllSongList(final Context context, final int page, final int size, final int type,
			final Handler handler) {

		new Thread(new Runnable() {

			@Override
			public void run() {

				Map<String, String> params = new HashMap<String, String>();
				params.put("phoneKey", getPhoneKey(context));
				params.put("currentPage", Integer.toString(page));
				params.put("pageSize", Integer.toString(size));
				params.put("type", Integer.toString(type));

				if (HttpUtils.isOpenNetwork(context) == true) {

					String jsonStr = HttpUtils.getRequest(ApiConfigs.requestLink + "getAllSongJsonList", params);
					if (jsonStr.length() > 0) {
						mixSongList = JsonUtils.getMixSongList(context, jsonStr);
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
	public void getRingList(final Context context, final int page, final int size, final int type, final Handler handler) {

		new Thread(new Runnable() {

			@Override
			public void run() {

				Map<String, String> params = new HashMap<String, String>();
				params.put("phoneKey", getPhoneKey(context));
				params.put("currentPage", Integer.toString(page));
				params.put("pageSize", Integer.toString(size));
				params.put("type", Integer.toString(type));

				if (HttpUtils.isOpenNetwork(context) == true) {

					String jsonStr = HttpUtils.getRequest(ApiConfigs.musictLink + "getMusicJsonList", params);
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
	public void updateFileToMix(final Context context, final ProgressBar progressBar, final String musicId,
			final File audioFile, final String speechfmt, final Handler handler) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				Looper.prepare();

				String urlStr = ApiConfigs.requestLink + "compoundSong";
				String phoneKey = getPhoneKey(context);
				String appid = ApiConfigs.appid;
				String appkey = ApiConfigs.appkey;
				HttpUploadPost post = new HttpUploadPost(context, progressBar, urlStr, appid, appkey, speechfmt,
						musicId, phoneKey, audioFile, handler);
				post.execute();
				Looper.loop();
			}

		}).start();
	}

	/**
	 * 重新合成请求
	 * 
	 * @param context
	 * @param handler
	 */
	public void reMixMusic(final Context context, final Handler handler) {
		new Thread(new Runnable() {

			@Override
			public void run() {

				Map<String, String> params = new HashMap<String, String>();
				params.put("musicId", ApiConfigs.selectId);
				params.put("appid", ApiConfigs.appid);
				params.put("appkey", ApiConfigs.appkey);
				params.put("phoneKey", getPhoneKey(context));
				params.put("speechid", ApiConfigs.speechid);
				params.put("recordId", ApiConfigs.recordId);
				if (HttpUtils.isOpenNetwork(context) == true) {

					String jsonStr = HttpUtils.getRequest(ApiConfigs.requestLink + "compoundSongAgain", params);
					if (jsonStr.length() > 0) {
						JSONObject jsonObj;
						System.out.println(">>>>>>>>>>---------reMixMusic重新合成请求：" + jsonStr);
						try {
							jsonObj = new JSONObject(jsonStr);
							int status = jsonObj.getInt("status");
							String msg = jsonObj.getString("msg");
							if (status == 1) {
								ApiConfigs.songId = jsonObj.getString("songId");
								ApiConfigs.speechid = jsonObj.getString("speechid");
								ApiConfigs.recordId = jsonObj.getString("recordId");
								checkMixMusic(context, ApiConfigs.songId, "amr", handler);
							} else {
								handler.obtainMessage(status, msg).sendToTarget();
							}
						} catch (JSONException e) {
							
							e.printStackTrace();
							DialogUtil.getInstance().ShowToast(context, "歌曲合成失败");
						}
					}

				} else {
					handler.obtainMessage(0, null).sendToTarget();
				}

			}
		}).start();

	}

	/**
	 * 变音歌曲合成状态查询
	 * 
	 * @param context
	 * @param songId
	 * @param handler
	 */
	public void checkMixMusic(final Context context, final String songId, final String speechfmt, final Handler handler) {
		new Thread(new Runnable() {

			@Override
			public void run() {

				Map<String, String> params = new HashMap<String, String>();
				params.put("songId", songId);
				params.put("appid", ApiConfigs.appid);
				params.put("appkey", ApiConfigs.appkey);
				params.put("phoneKey", getPhoneKey(context));
				if (HttpUtils.isOpenNetwork(context) == true) {

					String jsonStr = HttpUtils.getRequest(ApiConfigs.requestLink + "getSongResStatus", params);
					if (jsonStr.length() > 0) {
						JSONObject jsonObj;
						System.out.println(">>>>>>>>>>---------checkMixMusic合成音乐查询：" + jsonStr);
						try {
							jsonObj = new JSONObject(jsonStr);
							int status = jsonObj.getInt("status");
							String msg = jsonObj.getString("msg");
							if (status == 1) {
								ApiConfigs.songId = jsonObj.getString("songUrl");
								ApiConfigs.tempId = jsonObj.getString("tempId");
								handler.obtainMessage(1, ApiConfigs.songId).sendToTarget();
							} else if (status == 19) {
								Thread.sleep(500);
								checkMixMusic(context, songId, speechfmt, handler);
							} else {
								handler.obtainMessage(status, msg).sendToTarget();
							}
						} catch (JSONException e) {
							
							e.printStackTrace();
							DialogUtil.getInstance().ShowToast(context, "歌曲合成失败");
						} catch (InterruptedException e) {
							
							e.printStackTrace();
						}
					}

				} else {
					handler.obtainMessage(0, null).sendToTarget();
				}

			}
		}).start();

	}

	public void setPraise(final Context context, final String songId, final Handler handler) {
		new Thread(new Runnable() {
			@Override
			public void run() {

				Map<String, String> params = new HashMap<String, String>();
				params.put("id", songId);
				params.put("phoneKey", getPhoneKey(context));

				if (HttpUtils.isOpenNetwork(context) == true) {

					String jsonStr = HttpUtils.getRequest(ApiConfigs.requestLink + "setClickPraiseNum", params);
					if (jsonStr.length() > 0) {
						System.out.println(">>>>>>>>>>setPraise:" + jsonStr);
						JSONObject jsonObj;
						try {
							jsonObj = new JSONObject(jsonStr);
							int status = jsonObj.getInt("status");
							String msg = jsonObj.getString("msg");
							handler.obtainMessage(status, msg).sendToTarget();
						} catch (JSONException e) {
							
							e.printStackTrace();
						}
					} else {
						DialogUtil.getInstance().ShowToast(context, "数据请求出现问题");
					}
				}
			}
		}).start();
	}

	/**
	 * 合成歌曲保存并改名
	 * 
	 * @param context
	 * @param handler
	 */
	public void saveReName(final Context context, final String songName, final Handler handler) {
		new Thread(new Runnable() {

			@Override
			public void run() {

				Map<String, String> params = new HashMap<String, String>();

				params.put("phoneKey", getPhoneKey(context));
				params.put("tempId", ApiConfigs.tempId);
				params.put("songName", songName);
				if (HttpUtils.isOpenNetwork(context) == true) {

					String jsonStr = HttpUtils.getRequest(ApiConfigs.requestLink + "save", params);
					if (jsonStr.length() > 0) {
						JSONObject jsonObj;
						System.out.println(">>>>>>>>>>---------save保存并改名：" + jsonStr);
						try {
							jsonObj = new JSONObject(jsonStr);
							int status = jsonObj.getInt("status");
							String msg = jsonObj.getString("msg");
							Message message = new Message();

							if (status == 1) {
								message.what = 1;
								message.obj = msg;
								handler.sendMessage(message);
							} else {
								message.what = 1;
								message.obj = msg;
								handler.sendMessage(message);
							}
						} catch (JSONException e) {
							
							e.printStackTrace();
							DialogUtil.getInstance().ShowToast(context, "歌曲合成失败");
						}
					}

				} else {
					handler.obtainMessage(0, null).sendToTarget();
				}

			}
		}).start();

	}

	/**
	 * 删除用户幻音
	 * 
	 * @param context
	 * @param handler
	 */
	public void delete(final Context context, final String songId, final Handler handler) {
		new Thread(new Runnable() {

			@Override
			public void run() {

				Map<String, String> params = new HashMap<String, String>();

				params.put("phoneKey", getPhoneKey(context));
				params.put("id", songId);
				if (HttpUtils.isOpenNetwork(context) == true) {

					String jsonStr = HttpUtils.getRequest(ApiConfigs.requestLink + "delete", params);
					if (jsonStr.length() > 0) {
						JSONObject jsonObj;
						System.out.println(">>>>>>>>>>---------delete删除用户歌曲：" + jsonStr);
						try {
							jsonObj = new JSONObject(jsonStr);
							int status = jsonObj.getInt("status");
							String msg = jsonObj.getString("msg");
							Message message = new Message();

							if (status == 1) {
								message.what = 1;
								message.obj = msg;
								handler.sendMessage(message);
							} else {
								message.what = 1;
								message.obj = msg;
								handler.sendMessage(message);
							}
						} catch (JSONException e) {
							
							e.printStackTrace();
							DialogUtil.getInstance().ShowToast(context, "删除失败");
						}
					}

				} else {
					handler.obtainMessage(0, null).sendToTarget();
				}

			}
		}).start();

	}
}
