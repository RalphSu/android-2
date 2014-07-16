package com.recordmix.utils;

import java.io.File;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;

import com.recordmix.biz.RecordMixManager;
import com.recordmix.utils.CustomMultipartEntity.ProgressListener;
import com.recordmix.view.MainActivity;

public class HttpMultipartPost extends AsyncTask<String, Integer, String> {

	private Context context;
	private File audioFile;
	private String urlString, tempId, appid, appkey, uuid, fmt, speechfmt,
			speechtext;
	private ProgressDialog pd;
	private long totalSize;
	private Handler handler;

	public HttpMultipartPost(Context context, String urlString, String tempId,
			String appid, String appkey, String uuid, String fmt,
			String speechfmt, String speechtext, File audioFile, Handler handler) {
		this.context = context;
		this.urlString = urlString;
		this.tempId = tempId;
		this.appid = appid;
		this.appkey = appkey;
		this.uuid = uuid;
		this.fmt = fmt;
		this.speechfmt = speechfmt;
		this.speechtext = speechtext;
		this.audioFile = audioFile;
		this.handler = handler;
	}

	@Override
	protected void onPreExecute() {
//		pd = new ProgressDialog(context);
//		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//		pd.setMessage("录音上传中，请稍等...");
//		pd.setCancelable(false);
//		pd.show();
	}

	@Override
	protected String doInBackground(String... params) {
		String serverResponse = null;

		HttpClient httpClient = new DefaultHttpClient();
		HttpContext httpContext = new BasicHttpContext();
		HttpPost httpPost = new HttpPost(urlString);

		try {
			CustomMultipartEntity multipartContent = new CustomMultipartEntity(
					new ProgressListener() {
						@Override
						public void transferred(long num) {
							 publishProgress((int) ((num / (float) totalSize) * 100));  
						}
					});
			multipartContent.addPart("appid", new StringBody(appid));
			multipartContent.addPart("appkey", new StringBody(appkey));
			multipartContent.addPart("uuid", new StringBody(uuid));
			multipartContent.addPart("fmt", new StringBody(fmt));
			multipartContent.addPart("speechfmt", new StringBody(speechfmt));
			multipartContent.addPart("speechtext", new StringBody(speechtext));
			multipartContent.addPart("speechfile", new FileBody(audioFile));
			totalSize = multipartContent.getContentLength();

			// Send it
			httpPost.setEntity(multipartContent);
			HttpResponse response = httpClient.execute(httpPost, httpContext);
			serverResponse = EntityUtils.toString(response.getEntity());

		} catch (Exception e) {
			e.printStackTrace();
		}

		return serverResponse;
	}

	@Override
	protected void onProgressUpdate(Integer... progress) {
		pd.setProgress((int) (progress[0]));
	}

	@Override
	protected void onPostExecute(String result) {
		System.out.println("result: " + result);
		// 关闭上传进度
		pd.dismiss();
		try {
			JSONObject jsonObj = new JSONObject(result);
			int status = jsonObj.getInt("status");
			String msg = jsonObj.getString("msg");
			final String speechid = jsonObj.getString("speechid");
			if (status == 1) {
				// 显示合成界面
				DialogUtil.getInstance().ProgressDialog(context, true);
				// 向服务器发出合成命令
				RecordMixManager.getInstance().addMix(context, speechid,
						tempId, handler);
				MainActivity.mixId=speechid;

			} else {
				DialogUtil.getInstance().ShowToast(context, msg);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	@Override
	protected void onCancelled() {
		System.out.println("cancle");
	}

}
