package com.example.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.util.Log;

public class ImageService {
	private static final String TAG = "ImageService";
	public byte[] getImage(String urlpath) throws IOException {

		Log.i(TAG, "=============>");
		URL url=new URL(urlpath);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setConnectTimeout(5000);
		connection.setRequestMethod("GET");
		Log.i(TAG, "=============>"+connection.getResponseCode());
		if (connection.getResponseCode()==HttpURLConnection.HTTP_OK) {
			InputStream io=connection.getInputStream();
			ByteArrayOutputStream os=new ByteArrayOutputStream();
			byte[] buffer=new byte[1024];
			int length=0;
			while ((length=io.read(buffer))!=-1) {
				os.write(buffer, 0, length);
			}
			io.close();
			return os.toByteArray();
			
		}
		return null;
	}

}
