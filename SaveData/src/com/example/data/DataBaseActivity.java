package com.example.data;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.service.DataBaseService;

public class DataBaseActivity extends Activity {

	private DataBaseService service;
	private EditText editText;
	private Cursor cursor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_database);
		editText = (EditText) findViewById(R.id.txtPhone);
		service = new DataBaseService(this);

	}

	public void save(View view) {
		String phoneName = editText.getText().toString();
		service.insert(phoneName);
		Toast.makeText(this, getString(R.string.save_success), Toast.LENGTH_LONG).show();
	}

	public void list(View view) {
		cursor = service.getDataListCursor(0, 5);
		StringBuilder buf=new StringBuilder();
		while(cursor.moveToNext())
		{
			String name=cursor.getString(cursor.getColumnIndex("phonename"));
			buf.append(name).append("\n");
		}
		Toast.makeText(this, buf.toString(), Toast.LENGTH_LONG).show();
	}

}
