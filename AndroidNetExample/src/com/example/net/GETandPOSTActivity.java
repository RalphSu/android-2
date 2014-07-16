package com.example.net;

import com.example.service.GetAndPostService;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class GETandPOSTActivity extends Activity {

	private String path="http://192.168.100.6:8080/web/login";
	private EditText editText;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_getandpost);
		
		editText=(EditText)findViewById(R.id.editText_name);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.getand_post, menu);
		return true;
	}
	
	public void doGet(View view) {
		String name=editText.getText().toString();
		GetAndPostService service=new GetAndPostService();
		boolean status=service.doGet(path,name);
		if (status) {
			Toast.makeText(this, "提交成功", Toast.LENGTH_LONG).show();
		}else {
			Toast.makeText(this, "提交失败", Toast.LENGTH_LONG).show();
		}
	}
	
	public void doPost(View view) {
		String name=editText.getText().toString();
		GetAndPostService service=new GetAndPostService();
		boolean status=service.doPost(path,name);
		if (status) {
			Toast.makeText(this, "提交成功", Toast.LENGTH_LONG).show();
		}else {
			Toast.makeText(this, "提交失败", Toast.LENGTH_LONG).show();
		}
	}

}
