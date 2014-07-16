package com.example.net;

import java.io.IOException;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.service.HtmlImageService;

public class HtmlActivity extends Activity {
	private static final String TAG = "ImageService";

	private EditText editText;
	private TextView textView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_html);
		
		editText=(EditText)findViewById(R.id.html_editText);
		textView=(TextView)findViewById(R.id.htmlView);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.html, menu);
		return true;
	}
public void showHtml(View view) {
		
		HtmlImageService htmlImageService=new HtmlImageService();
		try {
			String urlpath=editText.getText().toString();
			String dataString=htmlImageService.getHtmlSource(urlpath);
			textView.setText(dataString);
			
		} catch (IOException e) {
			Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
	}
}
