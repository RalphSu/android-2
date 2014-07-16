package com.example.service;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.localservice.R;

public class Main extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
	}

	public void binding(View view) {
		Intent intent = new Intent(this, BindingActivity.class);
		startActivity(intent);
	}

	public void messenger(View view) {
		Intent intent = new Intent(this, MessengerActivity.class);
		startActivity(intent);
	}
	public void remote(View view) {
		Intent intent = new Intent(this, RemoteActivity.class);
		startActivity(intent);
	}
}
