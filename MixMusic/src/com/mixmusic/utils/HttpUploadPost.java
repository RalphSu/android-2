package com.mixmusic.utils;

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
import android.os.Message;
import android.view.View;
import android.widget.ProgressBar;

import com.mixmusic.biz.ApiConfigs;
import com.mixmusic.biz.BizManager;
import com.mixmusic.utils.CustomMultipartEntity.ProgressListener;
import com.mixmusic.view.MainActivity;

public class HttpUploadPost extends AsyncTask<String, Integer, String> {

	private Context context;
	private File audioFile;
	private String appid, appkey, speechfmt, urlString, musicId, phoneKey;
	private ProgressBar progressbar;
	private long totalSize;
	private Handler handler;

	public HttpUploadPost(Context context, ProgressBar progressbar,
			String urlString, String appid, String appkey, String speechfmt,
			String musicId, String phoneKey, File audioFile, Handler handler) {
		this.context = context;
		this.progressbar = progressbar;
		this.urlString = urlString;
		this.appid = appid;
		this.appkey = appkey;
		this.speechfmt = speechfmt;
		this.musicId = musicId;
		this.phoneKey = phoneKey;
		this.audioFile = audioFile;
		this.handler = handler;
	}

	@Override
	protected void onPreExecute() {

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
							System.out.println("--------当前值num:" + num);
							System.out.println("--------总值totalSize:"
									+ totalSize);
							System.out
									.println("--------当前值((float)num/totalSize):"
											+ ((float) num / totalSize));
							System.out
									.println("--------当前值(int)(((float)num/totalSize)*100)):"
											+ (int) (((float) num / totalSize) * 100));
							publishProgress((int) (((float) num / totalSize) * 100));
							try {
								Thread.sleep(10);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					});
			multipartContent.addPart("appid", new StringBody(appid));
			multipartContent.addPart("appkey", new StringBody(appkey));
			multipartContent.addPart("speechfmt", new StringBody(speechfmt));
			multipartContent.addPart("musicId", new StringBody(musicId));
			multipartContent.addPart("phoneKey", new StringBody(phoneKey));
			multipartContent.addPart("file", new FileBody(audioFile));
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
		progressbar.setProgress((int) (progress[0]));
	}

	@Override
	protected void onPostExecute(String result) {
		System.out.println("result: " + result);
		try {
			JSONObject jsonObj = new JSONObject(result);
			int status = jsonObj.getInt("status");
			String msg = jsonObj.getString("msg");
			System.out.println(">>>>>>>>>>------onPostExecute上传结果:" + result);
			//TabHost里面的页面，必须使用sendMessage，因为obtainMessage无响应
			Message message= new Message();
			if (status == 1) {	
				
				ApiConfigs.songId = jsonObj.getString("songId");	
				ApiConfigs.speechid = jsonObj.getString("speechid");
				ApiConfigs.recordId = jsonObj.getString("recordId");	
				message.what=1;
				message.obj=ApiConfigs.songId;

			} else {
				message.what=0;
				message.obj=msg;
			}
			handler.sendMessage(message);

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
