package com.example.ui;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

public class CheckBoxActivity extends Activity {

	private Map<Integer, String> chooses;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_checkbox);

		chooses = new HashMap<Integer, String>();
	}

	public void onCheckboxClicked(View view) {
		// Is the view now checked?
		boolean checked = ((CheckBox) view).isChecked();

		// Check which checkbox was clicked
		switch (view.getId()) {
		case R.id.chkAndroid:
			if (checked) {
				chooses.put(Integer.valueOf(R.id.chkAndroid), getString(R.string.chk_android));
			} else {
				chooses.remove(Integer.valueOf(R.id.chkAndroid));
			}
			break;
		case R.id.chkIos:
			if (checked) {
				chooses.put(Integer.valueOf(R.id.chkIos), getString(R.string.chk_ios));
			} else {
				chooses.remove(Integer.valueOf(R.id.chkIos));
			}
			break;
		case R.id.chkWindows:

			if (checked) {
				chooses.put(Integer.valueOf(R.id.chkWindows), getString(R.string.chk_windows));
			} else {
				chooses.remove(Integer.valueOf(R.id.chkWindows));
			}
			break;
		default:
			break;
		}
	}

	public void display(View view) {
		StringBuffer result = new StringBuffer("Choose:\n");
		for (String value : chooses.values()) {
			result.append(value).append("\n");
		}
		Toast.makeText(CheckBoxActivity.this, result.toString(), Toast.LENGTH_LONG).show();
	}

}
