package com.example.net;

import java.io.IOException;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.service.ImageService;

public class ImageActivity extends Activity {
	private static final String TAG = "ImageService";

	private EditText editText;
	private ImageView imageView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image);
		editText=(EditText)findViewById(R.id.image_editText);
		imageView=(ImageView)findViewById(R.id.imageView);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.image, menu);
		return true;
	}
public void showImage(View view) {
		
		ImageService imageService=new ImageService();
		try {
			String urlpath=editText.getText().toString();
			Log.i(TAG, "===========>"+urlpath);
			byte[] data=imageService.getImage(urlpath);
			Bitmap bitmap=BitmapFactory.decodeByteArray(data, 0, data.length);
			imageView.setImageBitmap(bitmap);
		} catch (IOException e) {
			Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
	}
}
