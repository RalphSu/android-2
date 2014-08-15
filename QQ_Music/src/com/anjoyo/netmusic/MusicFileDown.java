package com.anjoyo.netmusic;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class MusicFileDown {
	/**
	 * 下载文件并读取文件内容（只适用于文本文件）
	 * */
	public String DownLoad(String path) {
		try {
			String line;
			StringBuilder sb = new StringBuilder();
			URL url = new URL(path); // 定义需要访问的地址
			HttpURLConnection httpconn = (HttpURLConnection) url
					.openConnection(); // 通过HttpURLConnection打开连接
			InputStream inputS = httpconn.getInputStream(); // 得到一个数据流对象
			// 将数据流放入到缓冲区，并得到一个输入流
			BufferedReader b = new BufferedReader(new InputStreamReader(inputS,
					"gb2312"));
			while ((line = b.readLine()) != null) { // 循环读取每一行，如果当前行有数据，就追加到字符串中
				sb.append(line);
			}
			System.out.println(sb.toString()
					+ "1111111111111111111111-------------");
			return sb.toString();
		} catch (IOException e) {
			Log.i("DownLoad", "--------error--------------");
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 获取网络图片
	 */
	public Bitmap GetImgToIntent(String path) {
		HttpURLConnection httpconn = null;
		InputStream inputS = null;
		BufferedReader b = null;
		try {
			URL url = new URL(path); // 定义需要访问的地址
			// 1 建立连接
			httpconn = (HttpURLConnection) url.openConnection(); // 通过HttpURLConnection打开连接
			// 2 将服务端中的内容获取到buffer缓冲区中
			inputS = httpconn.getInputStream(); // 得到一个数据流对象
			BufferedInputStream buff = new BufferedInputStream(inputS);
			return BitmapFactory.decodeStream(buff);
		} catch (IOException e) {
			System.out.println("img-------error----" + e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

}
