package com.example.data;

import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.service.PreferencesService;

public class PreferencesActivity extends Activity {

	EditText unameEditText,passwordEditText;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_preferences);
		unameEditText=(EditText)findViewById(R.id.txtUsername);
		passwordEditText=(EditText)findViewById(R.id.txtPassword);
		
		load();
	}

	public void save(View view) {
		String username=unameEditText.getText().toString();
		String password=passwordEditText.getText().toString();
		
		PreferencesService service=new PreferencesService(getApplicationContext());
		service.save(username, password);
		Toast.makeText(this, getString(R.string.save_success), Toast.LENGTH_SHORT).show();
	}
	
	private void load() {
		PreferencesService service=new PreferencesService(getApplicationContext());
		Map<String, String> data=service.loadValue();
		unameEditText.setText(data.get(getString(R.string.key_username)));
		passwordEditText.setText(data.get(getString(R.string.key_password)));
	}
}
