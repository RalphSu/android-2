package com.example.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import android.content.Context;
import android.os.Environment;

public class FileService {
	private Context context;

	public FileService(Context content) {
		this.context = content;
	}
	///data/data/<package_name>/files/<filename>
	public void save(String filename, String content) throws Exception {
		FileOutputStream outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
		outputStream.write(content.getBytes());
		outputStream.close();
	}

	//path: /Android/data/<package_name>/files/<filename>
	public void save2sdcard(String filename, String content) throws Exception {
		//Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
		String path=Environment.getExternalStorageDirectory()+"/Android/data/com.example.data/files/";
		File file = new File(path, filename);
		new File(file.getParent()).mkdirs(); 
		FileOutputStream outputStream = new FileOutputStream(file);
		outputStream.write(content.getBytes());
		outputStream.close();
	}

	public String open(String filename) throws Exception {
		FileInputStream inputStream = context.openFileInput(filename);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024] ;
		int len = 0;
		while ((len = inputStream.read(buffer)) != -1) {
			outputStream.write(buffer, 0, len);
		}
		return new String(outputStream.toByteArray());
	}

}
