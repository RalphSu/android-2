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
	 * ����ģʽ
	 */
	private static BizManager instance;

	public static BizManager getInstance() {
		if (instance == null) {
			instance = new BizManager();
		}
		return instance;
	}

	/**
	 * �����ļ�
	 * 
	 * @param fileName
	 * @return
	 */
	public File CreateFilePath(String pfx) {
		try {
			// �����ļ�·��
			String dirPath = GetRootPath();
			// ����Ŀ¼
			File savePathDir = new File(dirPath);
			if (!savePathDir.exists()) {
				savePathDir.mkdirs();
			}
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH��mm-ss");
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
	 * ������Ϣ�����ݿ�
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
	 * ��ȡ����·��
	 * 
	 * @return
	 */
	public String GetRootPath() {

		return Environment.getExternalStorageDirectory().getAbsolutePath() + "/MixMusic/files";
	}

	/**
	 * ��ȡ������ַ
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
	 * ��ȡ�û�Ψһֵ
	 * 
	 * @return
	 */
	protected String getPhoneKey(final Context context) {
		return MD5.Md5(getMac(context));
	}

	/**
	 * �������ʱִ�еĲ���
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
							DialogUtil.getInstance().ShowToast(context, "���������������");
						}
					} else {
						DialogUtil.getInstance().ShowToast(context, "���������������");
					}
				}
			}
		}).start();

	}

	/**
	 * �û����������б�
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
	 * �����б�
	 * 
	 * @param context
	 * @param page
	 * @param size
	 * @param type
	 *            Ĭ��type=0������ʱ�併��type=1�����ݵ������Ľ���
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
	 * �����б�
	 * 
	 * @param context
	 * @param page
	 * @param size
	 * @param type
	 *            Ĭ�ϴ���type=0����ȡ���У�type=1�Ǿ��䣻type=2������
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
	 * �ϴ�¼���ļ�
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
	 * ���ºϳ�����
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
						System.out.println(">>>>>>>>>>---------reMixMusic���ºϳ�����" + jsonStr);
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
							DialogUtil.getInstance().ShowToast(context, "�����ϳ�ʧ��");
						}
					}

				} else {
					handler.obtainMessage(0, null).sendToTarget();
				}

			}
		}).start();

	}

	/**
	 * ���������ϳ�״̬��ѯ
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
						System.out.println(">>>>>>>>>>---------checkMixMusic�ϳ����ֲ�ѯ��" + jsonStr);
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
							DialogUtil.getInstance().ShowToast(context, "�����ϳ�ʧ��");
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
						DialogUtil.getInstance().ShowToast(context, "���������������");
					}
				}
			}
		}).start();
	}

	/**
	 * �ϳɸ������沢����
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
						System.out.println(">>>>>>>>>>---------save���沢������" + jsonStr);
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
							DialogUtil.getInstance().ShowToast(context, "�����ϳ�ʧ��");
						}
					}

				} else {
					handler.obtainMessage(0, null).sendToTarget();
				}

			}
		}).start();

	}

	/**
	 * ɾ���û�����
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
						System.out.println(">>>>>>>>>>---------deleteɾ���û�������" + jsonStr);
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
							DialogUtil.getInstance().ShowToast(context, "ɾ��ʧ��");
						}
					}

				} else {
					handler.obtainMessage(0, null).sendToTarget();
				}

			}
		}).start();

	}
}
