package com.example.data;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.service.FileService;

public class FileActivity extends Activity {
	private EditText filenameEditText, filecontentEditText;
	private FileService service;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_file);
		service = new FileService(this);
		filenameEditText = (EditText) findViewById(R.id.txtFilename);
		filecontentEditText = (EditText) findViewById(R.id.txtFilecontent);
	}
	
	public void save(View view) {

		String filename = filenameEditText.getText().toString();
		String content = filecontentEditText.getText().toString();
		try {
			service.save(filename, content);
			Toast.makeText(this, getString(R.string.save_success), Toast.LENGTH_LONG).show();
		} catch (Exception e) {
			Toast.makeText(this, getString(R.string.save_fail), Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
	}
	public void save2sdcard(View view) {
		
		String filename = filenameEditText.getText().toString();
		String content = filecontentEditText.getText().toString();
		try {
			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				service.save2sdcard(filename, content);
				Toast.makeText(this, getString(R.string.save_success), Toast.LENGTH_LONG).show();
			}
		} catch (Exception e) {
			Toast.makeText(this, getString(R.string.save_fail), Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
	}

	public void open(View view) {
		String filename = filenameEditText.getText().toString();
		try {
			String content=service.open(filename);
			filecontentEditText.setText(content);
			Toast.makeText(this, getString(R.string.open_success), Toast.LENGTH_LONG).show();
		} catch (Exception e) {
			Toast.makeText(this, getString(R.string.open_fail), Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
	}

}
