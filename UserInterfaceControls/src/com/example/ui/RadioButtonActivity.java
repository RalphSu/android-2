package com.example.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class RadioButtonActivity extends Activity {

	private RadioGroup radioSexGroup;
	private RadioButton radioSexButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_radiobutton);
	}

	public void display(View view) {

		radioSexGroup = (RadioGroup) findViewById(R.id.radioSex);
		// get selected radio button from radioGroup
		int selectedId = radioSexGroup.getCheckedRadioButtonId();

		// find the radiobutton by returned id
		radioSexButton = (RadioButton) findViewById(selectedId);
		Toast.makeText(RadioButtonActivity.this, radioSexButton.getText(), Toast.LENGTH_SHORT).show();

	}
}
