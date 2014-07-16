package com.example.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.widget.ToggleButton;

public class ToggleButtonActivity extends Activity {
	private ToggleButton toggleButton1, toggleButton2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_togglebutton);
	}

	public void display(View view) {
		toggleButton1 = (ToggleButton) findViewById(R.id.toggleButton1);
		toggleButton2 = (ToggleButton) findViewById(R.id.toggleButton2);

		StringBuffer result = new StringBuffer();
		result.append("toggleButton1 : ").append(toggleButton1.getText());
		result.append("\ntoggleButton2 : ").append(toggleButton2.getText());

		Toast.makeText(getApplicationContext(), result.toString(), Toast.LENGTH_SHORT).show();

	}
}
