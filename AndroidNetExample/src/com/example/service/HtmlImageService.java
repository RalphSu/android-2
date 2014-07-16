package com.example.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HtmlImageService {
	public String getHtmlSource(String urlpath) throws IOException {
		URL url=new URL(urlpath);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setConnectTimeout(5000);
		connection.setRequestMethod("GET");
		if (connection.getResponseCode()==HttpURLConnection.HTTP_OK) {
			InputStream io=connection.getInputStream();
			ByteArrayOutputStream os=new ByteArrayOutputStream();
			byte[] buffer=new byte[1024];
			int length=0;
			while ((length=io.read(buffer))!=-1) {
				os.write(buffer, 0, length);
			}
			io.close();
			return new String (os.toByteArray());
		}
		return null;
	}

}
