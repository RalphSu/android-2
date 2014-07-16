package com.example.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.AnalogClock;
import android.widget.DigitalClock;

public class AnalogClockActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_analog_clock);
		
		AnalogClock ac = (AnalogClock) findViewById(R.id.analogClock1);
		//what can i do with AnalogClock?
 
		DigitalClock dc = (DigitalClock) findViewById(R.id.digitalClock1);
		//what can i do with DigitalClock also? for display only
 
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.analog_clock, menu);
		return true;
	}

}
