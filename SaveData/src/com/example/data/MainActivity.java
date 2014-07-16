package com.example.data;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class MainActivity extends Activity {

	private RadioGroup radioSexGroup;
	private RadioButton radioSexButton;
	private String classname = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

	}

	@SuppressWarnings("unchecked")
	public void display(View view) {
		try {
			radioSexGroup = (RadioGroup) findViewById(R.id.radioClassName);
			// get selected radio button from radioGroup
			int selectedId = radioSexGroup.getCheckedRadioButtonId();

			// find the radiobutton by returned id
			radioSexButton = (RadioButton) findViewById(selectedId);
			String txtName = radioSexButton.getText().toString();
			if (txtName.equals(getString(R.string.title_activity_preferences ))) {
				classname = "com.example.data.PreferencesActivity";
			} else if (txtName.equals(getString(R.string.title_activity_file))) {
				classname = "com.example.data.FileActivity";
			}else if (txtName.equals(getString(R.string.title_activity_data_base))) {
				classname = "com.example.data.DataBaseActivity";
			}

			Class<Activity> vClass = (Class<Activity>) Class.forName(classname);

			Intent intent = new Intent(this, vClass);
			startActivity(intent);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

	}

}
