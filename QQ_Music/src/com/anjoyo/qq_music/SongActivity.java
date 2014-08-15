package com.anjoyo.qq_music;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.anjoyo.songinfo.SongInfoClass;
import com.anjoyo.songinfo.SongInfoSinger;
import com.anjoyo.songinfo.SongInfoSong;

import android.app.Activity;
import android.app.DownloadManager.Query;
import android.app.TabActivity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.Media;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class SongActivity extends TabActivity implements
		OnCheckedChangeListener {
	ImageView backview;
	TabHost tabHost;
	RadioGroup radioGroup;
	RadioButton radioButton1, radioButton2, radioButton3;

	protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.song);
		setView();
		backview = (ImageView) findViewById(R.id.back);
		backview.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(SongActivity.this,
						TabHostActivity.class);
				startActivity(intent);
			}
		});
		tabHost = getTabHost();

		tabHost.addTab(tabHost.newTabSpec("S").setIndicator("S")
				.setContent(new Intent(SongActivity.this, SongInfoSong.class)));
		tabHost.addTab(tabHost
				.newTabSpec("P")
				.setIndicator("P")
				.setContent(new Intent(SongActivity.this, SongInfoSinger.class)));
		tabHost.addTab(tabHost.newTabSpec("C").setIndicator("C")
				.setContent(new Intent(SongActivity.this, SongInfoClass.class)));
		tabHost.setCurrentTab(0);

		radioGroup.setOnCheckedChangeListener(this);
		
		

	}

	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
		case R.id.radioButton1:
			tabHost.setCurrentTabByTag("S");
			break;
		case R.id.radioButton2:
			tabHost.setCurrentTabByTag("P");
			break;
		case R.id.radioButton3:
			tabHost.setCurrentTabByTag("C");
			break;

		default:
			break;
		}
	}

	public void setView() {
		radioGroup = (RadioGroup) findViewById(R.id.songinfo_radiogroup);
		radioButton1 = (RadioButton) findViewById(R.id.radioButton1);
		radioButton2 = (RadioButton) findViewById(R.id.radioButton2);
		radioButton3 = (RadioButton) findViewById(R.id.radioButton3);
	}

	

}
