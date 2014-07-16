package com.aozhi.demo.layoutdemo;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

public class ImgListActivity extends ListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.activity_img_list);
		setListAdapter(new LogoArrayAdapter(this, SimpleListActivity.MOBILE_OS));
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Toast.makeText(getApplicationContext(),SimpleListActivity.MOBILE_OS[position], Toast.LENGTH_LONG).show();
	}

}
