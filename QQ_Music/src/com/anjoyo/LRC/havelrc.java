package com.anjoyo.LRC;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.anjoyo.qq_music.MainPlaying;
import com.anjoyo.qq_music.R;
import com.anjoyo.resource.GetMusicData;
import com.anjoyo.resource.MusicUtils;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

public class havelrc extends Activity{
	TextView nolrc;
	int  pesition, duration, current;
	private LrcRead mLrcRead;
	private LyricView mLyricView;
	private int index = 0;
	private int CurrentTime = 0;
	private int CountTime = 0;
	List<Map<String, Object>> dataList;
	private List<LyricContent> LyricList = new ArrayList<LyricContent>();
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lrcview2);
		dataList=GetMusicData.GetMusicData(this);
		nolrc = (TextView) findViewById(R.id.nolrctextview);
		mLrcRead = new LrcRead();
		mLyricView = (LyricView) findViewById(R.id.lrcview2);
		
		
		//
		IntentFilter filter = new IntentFilter();
		filter.addAction("isplay");
		filter.addAction("info");
		filter.addAction("current");
		filter.addAction("M");
		registerReceiver(receiver, filter);
	}
	BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			
			if (intent.getAction().equals("info")) {
				pesition = intent.getIntExtra("pesition", -1);
				duration = (Integer) dataList.get(pesition).get("TIME");				
				
				CountTime = duration;
				String lrcurl = dataList.get(pesition).get("NAME").toString();
				String lrcurl2 = lrcurl.substring(0, lrcurl.length() - 4);
				try {
					mLrcRead.Read("/mnt/sdcard/Music/" + lrcurl2 + ".lrc");
					LyricList = mLrcRead.GetLyricContent();
					mLyricView.setSentenceEntities(LyricList);
					mHandler.post(mRunnable);
					mLyricView.setVisibility(View.VISIBLE);
					nolrc.setVisibility(View.GONE);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					//mLyricView.setVisibility(View.INVISIBLE);
					nolrc.setVisibility(View.VISIBLE);
					mLyricView.setVisibility(View.GONE);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			if (intent.getAction().equals("current")) {
				current = intent.getIntExtra("current", 0);				
				CurrentTime = current;
			}
			
		}
	};
	Handler mHandler = new Handler();
	Runnable mRunnable = new Runnable() {
		public void run() {

			mLyricView.SetIndex(Index());

			mLyricView.invalidate();

			mHandler.postDelayed(mRunnable, 50);
		}
	};
	
	public int Index() {

		if (CurrentTime < CountTime) {

			for (int i = 0; i < LyricList.size(); i++) {
				if (i < LyricList.size() - 1) {
					if (CurrentTime < LyricList.get(i).getLyricTime() && i == 0) {
						index = i;
					}

					if (CurrentTime > LyricList.get(i).getLyricTime()
							&& CurrentTime < LyricList.get(i + 1)
									.getLyricTime()) {
						index = i;
					}
				}

				if (i == LyricList.size() - 1
						&& CurrentTime > LyricList.get(i).getLyricTime()) {
					index = i;
				}
			}
		}

		// System.out.println(index);
		return index;
	}

}
