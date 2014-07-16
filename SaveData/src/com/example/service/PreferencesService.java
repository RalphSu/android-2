package com.example.service;

import java.util.HashMap;
import java.util.Map;

import com.example.data.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PreferencesService {
	private Context context;

	public PreferencesService(Context context) {
		this.context=context;
	}

	///data/data/<package_name>/shared_prefs/<filename>.xml
	public void save(String username, String password) {
		SharedPreferences preferences = context.getSharedPreferences("User", Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putString(context.getString(R.string.key_username), username);
		editor.putString(context.getString(R.string.key_password), password);
		editor.commit();
	}
	
	public Map<String, String> loadValue() {
		Map<String, String> data=new HashMap<String, String>();
		SharedPreferences preferences =context.getSharedPreferences("User", Context.MODE_PRIVATE);
		data.put(context.getString(R.string.key_username), preferences.getString(context.getString(R.string.key_username), ""));
		data.put(context.getString(R.string.key_password), preferences.getString(context.getString(R.string.key_password), ""));
		return data;
	}
}
