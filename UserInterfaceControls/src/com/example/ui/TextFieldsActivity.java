package com.example.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class TextFieldsActivity extends Activity {

	private EditText edittext;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_textbox);
		addKeyListener();
		
		EditText editTextSearch = (EditText) findViewById(R.id.search);
		editTextSearch.setOnEditorActionListener(new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				 boolean handled = false;
			        if (actionId == EditorInfo.IME_ACTION_SEND) {
			        	Toast.makeText(getApplicationContext(), v.getText(), Toast.LENGTH_LONG).show();
			        	handled = true;
			        }
			        return handled;
			}
		});
	}

	public void addKeyListener() {

		// get edittext component
		edittext = (EditText) findViewById(R.id.editText);

		// add a keylistener to keep track user input
		edittext.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {

				// if keydown and "enter" is pressed
				if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {

					// display a floating message
					Toast.makeText(TextFieldsActivity.this, edittext.getText(), Toast.LENGTH_LONG).show();
					return true;

				} else if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_9)) {

					// display a floating message
					Toast.makeText(TextFieldsActivity.this, "Number 9 is pressed!", Toast.LENGTH_LONG).show();
					return true;
				}

				return false;
			}
		});
	}

}
