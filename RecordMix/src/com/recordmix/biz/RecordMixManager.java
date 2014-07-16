package com.recordmix.biz;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;

import com.recordmix.utils.DatabaseUtil;
import com.recordmix.utils.DialogUtil;
import com.recordmix.utils.HttpMultipartPost;
import com.recordmix.utils.HttpUtils;
import com.recordmix.utils.JsonUtils;

public class RecordMixManager {

	private List<HashMap<String, Object>> ringList;
	/**
	 * 单例模式
	 */
	private static RecordMixManager instance;

	public static RecordMixManager getInstance() {
		if (instance == null) {
			instance = new RecordMixManager();
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
		// if
		// (android.os.Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
		// {
		//
		// }
		return Environment.getExternalStorageDirectory().getAbsolutePath()
				+ "/RecordMix/files";
	}

	/**
	 * 获取歌曲模板列表
	 * 
	 * @param context
	 * @return
	 */
	public void getTempRingList(final Context context, final Handler handler) {

		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				BizConfigs biz = new BizConfigs(context);
				Map<String, String> params = new HashMap<String, String>();
				params.put("appid", biz.appid());
				params.put("appkey", biz.appkey());
				params.put("uuid", biz.uuid());
				params.put("fmt", "json");

				if (HttpUtils.isOpenNetwork(context) == true) {

					String jsonStr = HttpUtils.getRequest(biz.apiLink()
							+ "tplist", params);
					if (jsonStr.length() > 0) {
						ringList = JsonUtils.getTempRingList(context, jsonStr);
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
	public void updateFileToMix(Context context, String tempId, File audioFile,String pfx,
			Handler handler) {

		BizConfigs biz = new BizConfigs(context);
		String urlStr = biz.apiLink() + "upload";
		String appid, appkey, uuid, fmt, speechfmt, speechtext;
		appid = biz.appid();
		appkey = biz.appkey();
		uuid = biz.uuid();
		fmt = "json";
		speechfmt = pfx;
		speechtext = "";
		File speechfile = audioFile;
		HttpMultipartPost post = new HttpMultipartPost(context, urlStr, tempId,
				appid, appkey, uuid, fmt, speechfmt, speechtext, speechfile,
				handler);
		post.execute();
	}

	/**
	 * 
	 * @param context
	 * @param speechid
	 *            已上传到服务器上的语音文件ID
	 * @param tplid
	 *            合成模板ID
	 */
	public void addMix(final Context context, final String speechid,
			final String tplid, final Handler handler) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				BizConfigs biz = new BizConfigs(context);
				Map<String, String> params = new HashMap<String, String>();
				params.put("appid", biz.appid());
				params.put("appkey", biz.appkey());
				params.put("uuid", biz.uuid());
				params.put("fmt", "json");
				params.put("speechid", speechid);
				params.put("tplid", tplid);

				if (HttpUtils.isOpenNetwork(context) == true) {

					String jsonStr = HttpUtils.getRequest(biz.apiLink()
							+ "remixadd", params);
					if (jsonStr.length() > 0) {
						try {
							JSONObject jsonObj = new JSONObject(jsonStr);
							int status = jsonObj.getInt("status");
							String msg = jsonObj.getString("msg");
							String remixid = jsonObj.getString("remixid");
							if (status == 1) {
								getMix(context, remixid, handler);
							} else {
								DialogUtil.getInstance()
										.ShowToast(context, msg);
							}
						} catch (Exception e) {
							// TODO: handle exception
							e.printStackTrace();
						}

					}
				}
			}
		}).start();

	}

	/**
	 * 获取合成后的连接
	 * 
	 * @param context
	 * @param remixid
	 * @param handler
	 */
	public void getMix(Context context, String remixid, Handler handler) {

		BizConfigs biz = new BizConfigs(context);
		Map<String, String> params = new HashMap<String, String>();
		params.put("appid", biz.appid());
		params.put("appkey", biz.appkey());
		params.put("uuid", biz.uuid());
		params.put("fmt", "json");
		params.put("remixid", remixid);

		if (HttpUtils.isOpenNetwork(context) == true) {

			String jsonStr = HttpUtils.getRequest(biz.apiLink() + "remixchk",
					params);
			if (jsonStr.length() > 0) {
				try {
					JSONObject jsonObj = new JSONObject(jsonStr);
					int status = jsonObj.getInt("status");
					String msg = jsonObj.getString("msg");

					if (status == 1) {
						String songUrl = jsonObj.getString("url");
						String songfmt = jsonObj.getString("songfmt");
						handler.obtainMessage(1, songUrl).sendToTarget();
					} else if (status == 18) {
						// 等待进入处理队列
						getMix(context, remixid, handler);
					} else if (status == 19) {
						// 合成中
						getMix(context, remixid, handler);
					} else {
						handler.obtainMessage(16, null).sendToTarget();
					}
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();

				}
			}
		}
	}
}
