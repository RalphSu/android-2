package com.anjoyo.qq_music;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectOutputStream.PutField;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.anjoyo.LRC.LrcRead;
import com.anjoyo.LRC.LyricContent;
import com.anjoyo.LRC.LyricView;
import com.anjoyo.LRC.havelrc;
import com.anjoyo.LRC.noLrc;
import com.anjoyo.qq_music.R.id;
import com.anjoyo.resource.GetMusicData;
import com.anjoyo.resource.MusicUtils;
import com.anjoyo.service.MediaPlayService;
import com.anjoyo.songinfo.SongInfoSong;

import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class MainPlaying extends Activity {
	ImageView backimageView, main_playing_image, play_mode1;
	TextView title, time1, time2, nolrc;
	View cView;
	int play_state, pesition, duration, current;
	int mode = 3;

	ImageButton button1, button3;
	ImageButton button2;
	Button main_playing_button2;
	List<Map<String, Object>> list;
	public static SeekBar seekBar, seekBar2;
	boolean isdroadcast = false;
	private LrcRead mLrcRead;
	private LyricView mLyricView;
	private int index = 0;
	private int CurrentTime = 0;
	private int CountTime = 0;
	private List<LyricContent> LyricList = new ArrayList<LyricContent>();
	LocalActivityManager manager;
	ViewPager viewPager;
	AudioManager audioManager;

	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.mian_playing);
		backimageView = (ImageView) findViewById(R.id.main_playing_back);
		title = (TextView) findViewById(R.id.main_playing_title);
		button1 = (ImageButton) findViewById(R.id.main_pre);
		button2 = (ImageButton) findViewById(R.id.mian_playing_button);
		button3 = (ImageButton) findViewById(R.id.main_next);
		main_playing_image = (ImageView) findViewById(R.id.main_playing_image);
		main_playing_button2 = (Button) findViewById(R.id.main_playing_button2);
		play_mode1 = (ImageView) findViewById(R.id.play_mode1);

		seekBar = (SeekBar) findViewById(R.id.seekbar);

		// “Ù¡ø
		seekBar2 = (SeekBar) findViewById(R.id.sound_seekBar);

		time1 = (TextView) findViewById(R.id.time1);
		time2 = (TextView) findViewById(R.id.time2);
		cView = findViewById(R.id.control_view);
		//
		// nolrc = (TextView) findViewById(R.id.nolrcview);
		// mLrcRead = new LrcRead();
		// mLyricView = (LyricView) findViewById(R.id.LrcShow);
		// mLyricView.setText("");
		//
		manager = new LocalActivityManager(this, true);
		manager.dispatchCreate(savedInstanceState);
		viewPager = (ViewPager) findViewById(R.id.viewpage);
		ArrayList<View> viewlist = new ArrayList<View>();
		Intent intent1 = new Intent(this, noLrc.class);
		viewlist.add(getView("A", intent1));
		Intent intent2 = new Intent(this, havelrc.class);
		viewlist.add(getView("B", intent2));
		viewPager.setAdapter(new MyPagerAdapter(viewlist));
		viewPager.setCurrentItem(0);
		//
		title.setMovementMethod(ScrollingMovementMethod.getInstance());
		list = GetMusicData.GetMusicData(this);
		backimageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainPlaying.this,
						TabHostActivity.class);
				startActivity(intent);
			}
		});
		button1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mode == 3) {
					int c = pesition - 1;
					if (c == -1) {
						c = list.size() - 1;
					}
					Intent intent = new Intent(MainPlaying.this,
							MediaPlayService.class);
					intent.putExtra("pesition", c);
					intent.putExtra("what", "play");
					startService(intent);

				}
				if (mode == 2) {
					Random random = new Random();
					pesition = random.nextInt(list.size() - 1);
					Intent intent = new Intent(MainPlaying.this,
							MediaPlayService.class);
					intent.putExtra("pesition", pesition);
					intent.putExtra("what", "play");
					startService(intent);
				}
				if (mode == 1) {
					int c = pesition - 1;
					if (c == -1) {
						c = list.size() - 1;
					}
					Intent intent = new Intent(MainPlaying.this,
							MediaPlayService.class);
					intent.putExtra("pesition", c);
					intent.putExtra("what", "play");
					startService(intent);
				}
			}
		});
		button3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mode == 3) {
					int c = pesition + 1;
					if (c == list.size()) {
						c = 0;
					}
					Intent intent = new Intent(MainPlaying.this,
							MediaPlayService.class);
					intent.putExtra("pesition", c);
					intent.putExtra("what", "play");
					startService(intent);
				}
				if (mode == 2) {
					Random random = new Random();
					pesition = random.nextInt(list.size() - 1);
					Intent intent = new Intent(MainPlaying.this,
							MediaPlayService.class);
					intent.putExtra("pesition", pesition);
					intent.putExtra("what", "play");
					startService(intent);
				}
				if (mode == 1) {
					int c = pesition + 1;
					if (c == list.size()) {
						c = 0;
					}
					Intent intent = new Intent(MainPlaying.this,
							MediaPlayService.class);
					intent.putExtra("pesition", c);
					intent.putExtra("what", "play");
					startService(intent);
				}
			}
		});

		main_playing_button2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (play_state == 1) {
					Intent intent = new Intent(MainPlaying.this,
							MediaPlayService.class);
					intent.putExtra("what", "pause");
					intent.putExtra("pesition", pesition);
					startService(intent);
					play_state = 0;
					main_playing_button2
							.setBackgroundResource(R.drawable.pause_button_pressed);

				} else if (play_state == 0) {
					Intent intent = new Intent(MainPlaying.this,
							MediaPlayService.class);
					intent.putExtra("what", "restart");
					intent.putExtra("pesition", pesition);
					startService(intent);
					play_state = 1;
					main_playing_button2
							.setBackgroundResource(R.drawable.playing_button_pressed);

				}
			}
		});

		main_playing_image.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				cView.setVisibility(View.VISIBLE);
			}
		});

		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			public void onStopTrackingTouch(SeekBar seekBar) {
				Intent intent = new Intent(MainPlaying.this,
						MediaPlayService.class);
				intent.putExtra("what", "seekto");
				intent.putExtra("pesition", pesition);
				intent.putExtra("seekto", seekBar.getProgress());
				startService(intent);
			}

			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {

			}
		});

		play_mode1.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setAction("MODE");
				if (mode == 3) {
					intent.putExtra("m1", 1);
				}
				if (mode == 1) {
					intent.putExtra("m1", 2);
				}
				if (mode == 2) {
					intent.putExtra("m1", 3);
				}
				sendBroadcast(intent);
			}
		});
		voiceCtrl();

		//
		Intent intent = new Intent();
		intent.setAction("UI2");
		intent.putExtra("UISTATE2", "2");
		sendBroadcast(intent);

		IntentFilter filter = new IntentFilter();
		filter.addAction("isplay");
		filter.addAction("info");
		filter.addAction("current");
		filter.addAction("BTNMODE");
		filter.addAction("M");
		registerReceiver(receiver, filter);
	}

	BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals("isplay")) {
				play_state = intent.getIntExtra("state", 0);
				if (play_state == 1) {
					main_playing_button2
							.setBackgroundResource(R.drawable.playing_button_pressed);
				} else {
					main_playing_button2
							.setBackgroundResource(R.drawable.pause_button_pressed);
				}
			}
			if (intent.getAction().equals("info")) {
				pesition = intent.getIntExtra("pesition", -1);
				duration = (Integer) list.get(pesition).get("TIME");
				time2.setText(GetFormatTime(duration).toString());
				seekBar.setMax(duration);
				CountTime = duration;
				title.setText(list.get(pesition).get("TITLE").toString());

				long musicID = Integer.parseInt(list.get(pesition).get("ID")
						.toString());
				long musicAlbum_ID = Integer.parseInt(list.get(pesition)
						.get("ALBUMID").toString());

				Bitmap bm = MusicUtils.getArtWork(MainPlaying.this, musicID,
						musicAlbum_ID, true);
				main_playing_image.setImageBitmap(bm);

				/*
				 * String lrcurl = list.get(pesition).get("NAME").toString();
				 * String lrcurl2 = lrcurl.substring(0, lrcurl.length() - 4);
				 * try { mLrcRead.Read("/mnt/sdcard/Music/" + lrcurl2 + ".lrc");
				 * LyricList = mLrcRead.GetLyricContent();
				 * mLyricView.setSentenceEntities(LyricList);
				 * mHandler.post(mRunnable);
				 * mLyricView.setVisibility(View.VISIBLE);
				 * nolrc.setVisibility(View.GONE); } catch
				 * (FileNotFoundException e) { // TODO Auto-generated catch
				 * block e.printStackTrace();
				 * //mLyricView.setVisibility(View.INVISIBLE);
				 * nolrc.setVisibility(View.VISIBLE);
				 * mLyricView.setVisibility(View.GONE); } catch (IOException e)
				 * { // TODO Auto-generated catch block e.printStackTrace(); }
				 */

			}
			if (intent.getAction().equals("current")) {
				current = intent.getIntExtra("current", 0);
				time1.setText(GetFormatTime(current).toString());
				seekBar.setProgress(current);
				CurrentTime = current;
			}
			if (intent.getAction().equals("BTNMODE")) {
				mode = intent.getIntExtra("m", 3);
				if (mode == 1) {
					play_mode1
							.setBackgroundResource(R.drawable.playmode_repeate_single_hover);
				}
				if (mode == 2) {
					play_mode1
							.setBackgroundResource(R.drawable.playmode_repeate_random_hover);
				}
				if (mode == 3) {
					play_mode1
							.setBackgroundResource(R.drawable.playmode_repeate_all_hover);
				}
			}

		}
	};

	// Handler mHandler = new Handler();
	/*
	 * Runnable mRunnable = new Runnable() { public void run() {
	 * 
	 * mLyricView.SetIndex(Index());
	 * 
	 * mLyricView.invalidate();
	 * 
	 * mHandler.postDelayed(mRunnable, 50); } };
	 */

	/*
	 * public int Index() {
	 * 
	 * if (CurrentTime < CountTime) {
	 * 
	 * for (int i = 0; i < LyricList.size(); i++) { if (i < LyricList.size() -
	 * 1) { if (CurrentTime < LyricList.get(i).getLyricTime() && i == 0) { index
	 * = i; }
	 * 
	 * if (CurrentTime > LyricList.get(i).getLyricTime() && CurrentTime <
	 * LyricList.get(i + 1) .getLyricTime()) { index = i; } }
	 * 
	 * if (i == LyricList.size() - 1 && CurrentTime >
	 * LyricList.get(i).getLyricTime()) { index = i; } } }
	 * 
	 * // System.out.println(index); return index; }
	 */

	private static String GetFormatTime(int time) {
		SimpleDateFormat sim = new SimpleDateFormat("mm:ss");
		return sim.format(time);
	}

	private View getView(String id, Intent intent) {
		return manager.startActivity(id, intent).getDecorView();
	}

	public class MyPagerAdapter extends PagerAdapter {
		List<View> list = new ArrayList<View>();

		public MyPagerAdapter(ArrayList<View> list) {
			this.list = list;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			ViewPager pViewPager = ((ViewPager) container);
			pViewPager.removeView(list.get(position));
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			ViewPager pViewPager = ((ViewPager) arg0);
			pViewPager.addView(list.get(arg1));
			return list.get(arg1);
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {

		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {
		}
	}

	public void voiceCtrl() {
		audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		int M = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		seekBar2.setMax(M);
		int C = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		seekBar2.setProgress(C);
		seekBar2.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				if (fromUser) {
					int a = seekBar.getProgress();
					audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, a,
							0);
				}

			}
		});
	}

}
