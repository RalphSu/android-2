package com.example.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class MainActivity extends Activity {

	private static final String TAG = "MainActivity";
	private RadioGroup radioSexGroup;
	private RadioButton radioSexButton;
	private String classname = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

	}

	public void open(View view) {
		try {
			radioSexGroup = (RadioGroup) findViewById(R.id.radioClassName);
			// get selected radio button from radioGroup
			int selectedId = radioSexGroup.getCheckedRadioButtonId();

			// find the radiobutton by returned id
			radioSexButton = (RadioButton) findViewById(selectedId);
			String flagname = radioSexButton.getText().toString();
			Log.i(TAG, flagname);
			if (flagname.equals(getString(R.string.title_activity_radio_button))) {
				classname = "com.example.ui.RadioButtonActivity";
			} else if (flagname.equals(getString(R.string.title_activity_textbox))) {
				classname = "com.example.ui.TextFieldsActivity";
			} else if (flagname.equals(getString(R.string.title_activity_password_field))) {
				classname = "com.example.ui.PasswordFieldActivity";
			} else if (flagname.equals(getString(R.string.title_activity_toggle_button))) {
				classname = "com.example.ui.ToggleButtonActivity";
			} else if (flagname.endsWith(getString(R.string.title_activity_rating_bar))) {
				classname = "com.example.ui.RatingBarActivity";
			} else if (flagname.equals(getString(R.string.title_activity_button))) {
				classname = "com.example.ui.ButtonActivity";
			} else if (flagname.equals(getString(R.string.title_activity_check_box))) {
				classname = "com.example.ui.CheckBoxActivity";
			}else if (flagname.equals(getString(R.string.title_activity_spinner))) {
				classname = "com.example.ui.SpinnerActivity";
			}else if (flagname.equals(getString(R.string.title_activity_date_picker))) {
				classname = "com.example.ui.DatePickerActivity";
			}else if (flagname.equals(getString(R.string.title_activity_time_picker))) {
				classname = "com.example.ui.TimePickerActivity";
			}else if (flagname.equals(getString(R.string.title_activity_analog_clock))) {
				classname = "com.example.ui.AnalogClockActivity";
			}else if (flagname.equals(getString(R.string.title_activity_progress_bar))) {
				classname = "com.example.ui.ProgressBarActivity";
			}else if (flagname.equals(getString(R.string.title_activity_alert_dialog))) {
				classname = "com.example.ui.AlertDialogActivity";
			}

			Class<Activity> vClass = (Class<Activity>) Class.forName(classname);

			Intent intent = new Intent(this, vClass);
			startActivity(intent);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

	}
}
