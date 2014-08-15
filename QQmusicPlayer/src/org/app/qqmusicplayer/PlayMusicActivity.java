package org.app.qqmusicplayer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.TreeMap;

import org.app.qqmusic.tool.LRCbean;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.SlidingDrawer;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 这里是播放界面模块。原先的播放等于没用服务而已，这次把后台狠狠强化下，不但能后台播放，而且采取随机播放的功能
 * 这边对一些资源以及状态的操作保存在OnStart下进行。显示歌名歌手，调用loadclip 方法+歌词调用readlrc，路径根据手机SD卡不同而不同。所以运行之前修改下你真机实际路径 
 * 很明显，所有显示的有关的东西都是调用OnStart方法
 */
public class PlayMusicActivity extends Activity {
	private int[] _ids;// 临时音乐id
	private String[] _artists;// 艺术家
	private String[] _titles;// 标题
	private TextView musicnames;// 音乐名
	private TextView artisting;// 艺术家
	private ImageButton play_btn;// 播放按钮
	private ImageButton last_btn;// 上一首
	private ImageButton next_btn;// 下一首
	private TextView playtimes = null;// 已播放时间
	private TextView durationTime = null;// 歌曲时间
	private SeekBar seekbar;// 进度条
	private int position;// 定义一个位置，用于定位用户点击列表曲首
	private int currentPosition;// 当前播放位置
	private int duration;// 总时间
	private TextView lrcTextview;// 歌词
    private ImageView album;// 专辑
	private SlidingDrawer slidingdrawer;
	private ImageButton LoopBtn = null;// 循环
	private ImageButton RandomBtm = null;// 随机
	private ImageView ly_handle;//手动
	private static final String MUSIC_CURRENT = "com.app.currentTime";// Action的状态
	private static final String MUSIC_DURATION = "com.app.duration";
	private static final String MUSIC_NEXT = "com.app.next";
	private static final String MUSIC_UPDATE = "com.app.update";
	private static final int PLAY = 1;// 定义播放状态
	private static final int PAUSE = 2;// 暂停状态
	private static final int STOP = 3;// 停止
	private static final int PROGRESS_CHANGE = 4;// 进度条改变
	private static final int STATE_PLAY = 1;// 播放状态设为1
	private static final int STATE_PAUSE = 2;// 播放状态设为2
	public static final int LOOP_NONE = 0;//不循环
	public static final int LOOP_ONE = 1;//单体循环
	public static final int LOOP_ALL = 2;//全部循环
	public static int loop_flag = LOOP_NONE;
	public static boolean random_flag = false;
	public static int[] randomIDs = null;
	public static int randomNum = 0;
	private int flag;// 标记
	private Cursor cursor;// 游标
	private TreeMap<Integer, LRCbean> lrc_map = new TreeMap<Integer, LRCbean>();// Treemap对象
	private AudioManager am;
	private int maxVolume;// 最大音量
	private int currentVolume;// 当前音量
	public Context context;//上下文
    private RelativeLayout play_control;//控制台
    private RelativeLayout play_seekbar;//进度条
    float start;
    float end;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.play_music);
		Intent intent = this.getIntent();//接收来自列表的数据
		Bundle bundle = intent.getExtras();
		_ids = bundle.getIntArray("_ids");
		randomIDs = new int[_ids.length];
		position = bundle.getInt("position");
		_titles = bundle.getStringArray("_titles");
		_artists = bundle.getStringArray("_artists");
		// 以下是找各个控件ID。大家熟悉的，故不解释
		musicnames = (TextView) findViewById(R.id.play_music_name);
		artisting = (TextView) findViewById(R.id.playback_artist_name);
		play_btn = (ImageButton) findViewById(R.id.pausebtn);
		last_btn = (ImageButton) findViewById(R.id.prevbtn);
		next_btn = (ImageButton) findViewById(R.id.nextbtn);
		playtimes = (TextView) findViewById(R.id.playtime);
		durationTime = (TextView) findViewById(R.id.duration);
		seekbar = (SeekBar) findViewById(R.id.play_seekbar);
		lrcTextview = (TextView) findViewById(R.id.lrc);
		album = (ImageView) findViewById(R.id.playback_album_art);
		am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		maxVolume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		currentVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
	    context=this;
	   
		slidingdrawer = (SlidingDrawer) findViewById(R.id.playback_drawer);
		ly_handle=(ImageView) findViewById(R.id.drawer_handle);
		play_control=(RelativeLayout) findViewById(R.id.bottom);
		play_seekbar=(RelativeLayout) findViewById(R.id.player_progress_bar);
		// 下面写几个按钮方法。首先找ID之后加监听后，在监听写方法
		ShowPlayBtn();
		ShowLastBtn();
		ShowNextBtn();
		ShowSeekBar();
		ShowLoop();
		ShowRandom();
		ShowSliderDrawers();
		
		
	}

	
	/** 显示随机*/
    private void ShowRandom() {
		RandomBtm = (ImageButton) findViewById(R.id.playback_mode);
		RandomBtm.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (random_flag == false) {
					for (int i = 0; i < _ids.length; i++) {
						randomIDs[i] = -1;
					}
					random_flag = true;
					RandomBtm.setBackgroundResource(R.drawable.random_select);
					Toast.makeText(PlayMusicActivity.this, "你选择的是随机播放", Toast.LENGTH_SHORT).show();
				} else {
					random_flag = false;
					RandomBtm.setBackgroundResource(R.drawable.random);
				}

			}

		});

	}
/** 显示按顺序或者单曲重复播放*/
	private void ShowLoop() {
		LoopBtn = (ImageButton) findViewById(R.id.playback_mode);
		LoopBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (loop_flag) {
				case LOOP_NONE:
					loop_flag = LOOP_ONE;
					LoopBtn.setBackgroundResource(R.drawable.loop_one);
					Toast.makeText(PlayMusicActivity.this, "你选择的是单曲重复", Toast.LENGTH_SHORT).show();
					break;

				case LOOP_ONE:
					loop_flag = LOOP_ALL;
					LoopBtn.setBackgroundResource(R.drawable.loop_all);
					Toast.makeText(PlayMusicActivity.this, "你选择的是全部循环", Toast.LENGTH_SHORT).show();
					break;
				case LOOP_ALL:
					loop_flag = LOOP_NONE;
					LoopBtn.setBackgroundResource(R.drawable.loop_none);
					Toast.makeText(PlayMusicActivity.this, "你选择的是单曲循环", Toast.LENGTH_SHORT).show();
					break;
				}
			}
		});

	}

	/** 播放按钮*/
	private void ShowPlayBtn() {
		
		play_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				switch (flag) {
				case STATE_PLAY:
					pause();
					break;

				case STATE_PAUSE:
					play();
					break;
				}

			}
		});

	}

	/** 显示进度条*/
	private void ShowSeekBar() {
		seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

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
					seekbar_change(progress);
				}

			}
		});

	}

	/** 显示下一首*/
	public void ShowNextBtn() {
		next_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				nextOne();

			}
		});

	}

	/** 显示上一首*/
	public void ShowLastBtn() {
		
		last_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				lastOne();
			}
		});

	}

	/** 播放音乐*/
	public void play() {
		flag = PLAY;
		play_btn.setImageResource(R.drawable.pause_button);
		Intent intent = new Intent();
		intent.setAction("com.app.media.MUSIC_SERVICE");
		intent.putExtra("op", PLAY);// 向服务传递数据
		startService(intent);

	}

	/** 暂停播放音乐*/
	public void pause() {
		flag = PAUSE;
		play_btn.setImageResource(R.drawable.play_button);
		Intent intent = new Intent();
		intent.setAction("com.app.media.MUSIC_SERVICE");
		intent.putExtra("op", PAUSE);
		startService(intent);

	}

	/*** 进度条改变*/
	private void seekbar_change(int progress) {
		Intent intent = new Intent();
		intent.setAction("com.app.media.MUSIC_SERVICE");
		intent.putExtra("op", PROGRESS_CHANGE);
		intent.putExtra("progress", progress);
		startService(intent);

	}

	/** 下一首*/
	public void nextOne() {
		if (_ids.length == 1 || loop_flag == LOOP_ONE) {
			position = position;
			Intent intent = new Intent();
			intent.setAction("com.app.media.MUSIC_SERVICE");
			intent.putExtra("length", 1);
			startService(intent);
			play();
			return;

		}
		if (random_flag == true) {
			if (randomNum < _ids.length - 1) {
				randomIDs[randomNum] = position;
				position = findRandomSound(_ids.length);
				randomNum++;

			} else {
				randomNum = 0;
				for (int i = 0; i < _ids.length; i++) {
					randomIDs[i] = -1;
				}
				randomIDs[randomNum] = position;
				position = findRandomSound(_ids.length);
				randomNum++;
			}
		} else {
			if (position == _ids.length - 1) {
				position = 0;
			} else if (position < _ids.length - 1) {
				position++;
			}
		}
		stop();
		setup();
		play();

	}

	/**
	 * 上一首
	 */
	public void lastOne() {
		if (_ids.length == 1 || loop_flag == LOOP_ONE) {
			position = position;
			Intent intent = new Intent();
			intent.setAction("com.app.media.MUSIC_SERVICE");
			intent.putExtra("length", 1);
			startService(intent);
			play();
			return;
		}
		if (random_flag == true) {
			if (randomNum < _ids.length - 1) {
				randomIDs[randomNum] = position;
				position = findRandomSound(_ids.length);
				randomNum++;

			} else {
				randomNum = 0;
				for (int i = 0; i < _ids.length; i++) {
					randomIDs[i] = -1;
				}
				randomIDs[randomNum] = position;
				position = findRandomSound(_ids.length);
				randomNum++;
			}
		} else {
			if (position == 0) {
				position = _ids.length - 1;
			} else if (position > 0) {
				position--;
			}
		}
		stop();
		setup();
		play();
	}
    /**找到随机位置**/
	public static int findRandomSound(int end) {
		int ret = -1;
		ret = (int) (Math.random() * end);
		while (havePlayed(ret, end)) {
			ret = (int) (Math.random() * end);
		}
		return ret;
	}
  /**是否在播放**/
	public static boolean havePlayed(int position, int num) {
		boolean ret = false;

		for (int i = 0; i < num; i++) {
			if (position == randomIDs[i]) {
				ret = true;
				break;
			}
		}

		return ret;
	}

	/**
	 * 停止播放音乐
	 */
	private void stop() {
		Intent isplay = new Intent("notifi.update");
		sendBroadcast(isplay);// 发起后台支持
		unregisterReceiver(musicReceiver);
		Intent intent = new Intent();
		intent.setAction("com.app.media.MUSIC_SERVICE");
		intent.putExtra("op", STOP);
		startService(intent);

	}

	@Override
	protected void onStart() {
		super.onStart();
		setup();
		play();
	}

	/**
	 * 当界面不可见时候，反注册的广播
	 */
	@Override
	protected void onStop() {
		super.onStop();
		unregisterReceiver(musicReceiver);
	}

	/**
	 * 按下返回键事件
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent(PlayMusicActivity.this,
					TabHostMainActivity.class);
			startActivity(intent);
			finish();
		}
		return true;
	}

	/**
	 * 初始化
	 */
	private void setup() {
		loadclip();
		init();
		ReadSDLrc();

	}

	/**
	 * 读歌词方法
	 */
	private void ReadSDLrc() {
		/**我们现在的歌词就是要String数组的第4个参数-显示文件名字**/
		cursor = getContentResolver().query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
				new String[] { MediaStore.Audio.Media.TITLE,
						MediaStore.Audio.Media.DURATION,
						MediaStore.Audio.Media.ARTIST,
						MediaStore.Audio.Media.ALBUM,
						MediaStore.Audio.Media.DISPLAY_NAME,
						MediaStore.Audio.Media.ALBUM_ID }, "_id=?",
				new String[] { _ids[position] + "" }, null);
		cursor.moveToFirst();// 将游标移至第一位
		Bitmap bm = getArtwork(this, _ids[position], cursor.getInt(5), true);
		/**切换播放时候专辑图片出现透明效果**/
		Animation albumanim=AnimationUtils.loadAnimation(context, R.anim.album_replace);
		/**开始播放动画效果**/
		album.startAnimation(albumanim);
		album.setImageBitmap(bm);
		/**游标定位到DISPLAY_NAME**/
		String name = cursor.getString(4);
		/**sd卡的音乐名字截取字符窜并找到它的位置,这步重要，没有写一直表示歌词文件无法显示,顺便说声不同手机型号SD卡有不同的路径。**/
		read("/sdcard/music/" + name.substring(0, name.indexOf(".")) + ".lrc");
		/** 在调试时我先把音乐名字写死，运行时候在控制台打印出音乐名字，那么由此判断歌名没问题.只是没有获取位置**/
		System.out.println(cursor.getString(4));

	}
	/**
	 * 读取歌词的方法，采用IO方法一行一行的显示
	 */
	private void read(String path) {
		lrc_map.clear();
		TreeMap<Integer, LRCbean> lrc_read = new TreeMap<Integer, LRCbean>();
		String data = "";
		BufferedReader br = null;
		File file = new File(path);
		System.out.println(path);
		/**如果没有歌词，则用没有歌词显示**/
		if (!file.exists()) {
			Animation lrcanim=AnimationUtils.loadAnimation(context, R.anim.album_replace);
			lrcTextview.setText(R.string.lrc_messenge);
			lrcTextview.startAnimation(lrcanim);
			return;
		}
		FileInputStream stream = null;
		try {
			stream = new FileInputStream(file);
			br = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		try {
			while ((data = br.readLine()) != null) {
				if (data.length() > 6) {
					if (data.charAt(3) == ':' && data.charAt(6) == '.') {// 从歌词正文开始
						data = data.replace("[", "");
						data = data.replace("]", "@");
						data = data.replace(".", ":");
						String lrc[] = data.split("@");
						String lrcContent = null;
						if (lrc.length == 2) {
							lrcContent = lrc[lrc.length - 1];// 歌词
						} else {
							lrcContent = "";
						}

						for (int i = 0; i < lrc.length - 1; i++) {
							String lrcTime[] = lrc[0].split(":");

							int m = Integer.parseInt(lrcTime[0]);// 分
							int s = Integer.parseInt(lrcTime[1]);// 秒
							int ms = Integer.parseInt(lrcTime[2]);// 毫秒

							int begintime = (m * 60 + s) * 1000 + ms;// 转换成毫秒
							LRCbean lrcbean = new LRCbean();
							lrcbean.setBeginTime(begintime);// 设置歌词开始时间
							lrcbean.setLrcBody(lrcContent);// 设置歌词的主体
							lrc_read.put(begintime, lrcbean);

						}

					}
				}
			}
			stream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// 计算每句歌词需要的时间
		lrc_map.clear();
		data = "";
		Iterator<Integer> iterator = lrc_read.keySet().iterator();
		LRCbean oldval = null;
		int i = 0;

		while (iterator.hasNext()) {
			Object ob = iterator.next();
			LRCbean val = lrc_read.get(ob);
			if (oldval == null) {
				oldval = val;
			} else {
				LRCbean item1 = new LRCbean();
				item1 = oldval;
				item1.setLineTime(val.getBeginTime() - oldval.getBeginTime());
				lrc_map.put(new Integer(i), item1);
				i++;
				oldval = val;
			}
		}
	}
	/**
	 * 初始化服务
	 */
	private void init() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(MUSIC_CURRENT);
		filter.addAction(MUSIC_DURATION);
		filter.addAction(MUSIC_NEXT);
		filter.addAction(MUSIC_UPDATE);
		filter.addAction("notifi.update");
		registerReceiver(musicReceiver, filter);

	}

	/**
	 * 获取歌名，歌手的字符串
	 */
	private void loadclip() {
		seekbar.setProgress(0);
		/**设置歌曲名**/
		if (_titles[position].length() > 15)
			musicnames.setText(_titles[position].substring(0, 12) + "...");// 设置歌曲名
		else
			musicnames.setText(_titles[position]);

		/**设置艺术家名**/ 
		if (_artists[position].equals("<unknown>"))
			artisting.setText("未知艺术家");
		else
			artisting.setText(_artists[position]);
		Intent intent = new Intent();
		intent.putExtra("_ids", _ids);
		intent.putExtra("_titles", _titles);
		intent.putExtra("_artists", _artists);
		intent.putExtra("position", position);
	    intent.setAction("com.app.media.MUSIC_SERVICE");// 给将这个action发送服务
		startService(intent);

	}

	/**在后台MusicService里使用handler消息机制，不停的向前台发送广播，广播里面的数据是当前mp播放的时间点，
	 * 前台接收到广播后获得播放时间点来更新进度条,暂且先这样。但是一些人说虽然这样能实现。但是还是觉得开个子线程不错**/
	protected BroadcastReceiver musicReceiver = new BroadcastReceiver() {

		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(MUSIC_CURRENT)) {
				currentPosition = intent.getExtras().getInt("currentTime");// 获得当前播放位置
				playtimes.setText(toTime(currentPosition));
				seekbar.setProgress(currentPosition);// 设置进度条
				Iterator<Integer> iterator = lrc_map.keySet().iterator();
				while (iterator.hasNext()) {
					Object o = iterator.next();
					LRCbean val = lrc_map.get(o);
					if (val != null) {

						if (currentPosition > val.getBeginTime()
								&& currentPosition < val.getBeginTime()
										+ val.getLineTime()) {
							lrcTextview.setText(val.getLrcBody());
							break;
						}
					}
				}
			} else if (action.equals(MUSIC_DURATION)) {
				duration = intent.getExtras().getInt("duration");
				seekbar.setMax(duration);
				durationTime.setText(toTime(duration));

			} else if (action.equals(MUSIC_NEXT)) {
				nextOne();
			} else if (action.equals(MUSIC_UPDATE)) {
				position = intent.getExtras().getInt("position");

				setup();
			}
		}
	};

	/**
	 * 播放时间转换
	 */
	public String toTime(int time) {

		time /= 1000;
		int minute = time / 60;
		int second = time % 60;
		minute %= 60;
		return String.format("%02d:%02d", minute, second);
	}

	

	/**
	 * 回调音量大小函数
	 */
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		int action = event.getAction();
		int keyCode = event.getKeyCode();
		switch (keyCode) {
		case KeyEvent.KEYCODE_VOLUME_UP:
			if (action == KeyEvent.ACTION_UP) {
				if (currentVolume < maxVolume) {
					currentVolume = currentVolume + 1;
					am.setStreamVolume(AudioManager.STREAM_MUSIC,
							currentVolume, 0);
				} else {
					am.setStreamVolume(AudioManager.STREAM_MUSIC,
							currentVolume, 0);
				}
			}
			return false;
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			if (action == KeyEvent.ACTION_UP) {
				if (currentVolume > 0) {
					currentVolume = currentVolume - 1;
					am.setStreamVolume(AudioManager.STREAM_MUSIC,
							currentVolume, 0);
				} else {
					am.setStreamVolume(AudioManager.STREAM_MUSIC,
							currentVolume, 0);
				}
			}
			return false;
		default:
			return super.dispatchKeyEvent(event);
		}
	}

	/**
	 * 以下是歌曲放的时候显示专辑图片。和列表不同,播放时图片要大。所以cam那个方法写合适的图片吧
	 */
	public static Bitmap getArtwork(Context context, long song_id,
			long album_id, boolean allowdefault) {
		if (album_id < 0) {

			if (song_id >= 0) {
				Bitmap bm = getArtworkFromFile(context, song_id, -1);
				if (bm != null) {
					return bm;
				}
			}
			if (allowdefault) {
				return getDefaultArtwork(context);
			}
			return null;
		}
		ContentResolver res = context.getContentResolver();
		Uri uri = ContentUris.withAppendedId(sArtworkUri, album_id);
		if (uri != null) {
			InputStream in = null;
			try {
				in = res.openInputStream(uri);
				BitmapFactory.Options options = new BitmapFactory.Options();
				/**先指定原始大小**/
				options.inSampleSize = 1;
				/** 只进行大小判断**/
				options.inJustDecodeBounds = true;
				/**调用此方法得到options得到图片的大小**/
				BitmapFactory.decodeStream(in, null, options);
				/**我们的目标是在你N pixel的画面上显示。 所以需要调用computeSampleSize得到图片缩放的比例**/
				options.inSampleSize = computeSampleSize(options, 100);
				/**我们得到了缩放的比例，现在开始正式读入BitMap数据**/
				options.inJustDecodeBounds = false;
				options.inDither = false;
				options.inPreferredConfig = Bitmap.Config.ARGB_8888;
				in = res.openInputStream(uri);
				return BitmapFactory.decodeStream(in, null, options);
			} catch (FileNotFoundException ex) {

				Bitmap bm = getArtworkFromFile(context, song_id, album_id);
				if (bm != null) {
					if (bm.getConfig() == null) {
						bm = bm.copy(Bitmap.Config.RGB_565, false);
						if (bm == null && allowdefault) {
							return getDefaultArtwork(context);
						}
					}
				} else if (allowdefault) {
					bm = getDefaultArtwork(context);
				}
				return bm;
			} finally {
				try {
					if (in != null) {
						in.close();
					}
				} catch (IOException ex) {
				}
			}
		}

		return null;
	}

	private static Bitmap getArtworkFromFile(Context context, long songid,
			long albumid) {
		Bitmap bm = null;
		if (albumid < 0 && songid < 0) {
			throw new IllegalArgumentException(
					"Must specify an album or a song id");
		}
		try {

			BitmapFactory.Options options = new BitmapFactory.Options();

			FileDescriptor fd = null;
			if (albumid < 0) {
				Uri uri = Uri.parse("content://media/external/audio/media/"
						+ songid + "/albumart");
				ParcelFileDescriptor pfd = context.getContentResolver()
						.openFileDescriptor(uri, "r");
				if (pfd != null) {
					fd = pfd.getFileDescriptor();
				}
			} else {
				Uri uri = ContentUris.withAppendedId(sArtworkUri, albumid);
				ParcelFileDescriptor pfd = context.getContentResolver()
						.openFileDescriptor(uri, "r");
				if (pfd != null) {
					fd = pfd.getFileDescriptor();
				}
			}
			options.inSampleSize = 1;
			// 只进行大小判断
			options.inJustDecodeBounds = true;
			// 调用此方法得到options得到图片的大小
			BitmapFactory.decodeFileDescriptor(fd, null, options);
			// 我们的目标是在800pixel的画面上显示。
			// 所以需要调用computeSampleSize得到图片缩放的比例
			options.inSampleSize = 100;
			// OK,我们得到了缩放的比例，现在开始正式读入BitMap数据
			options.inJustDecodeBounds = false;
			options.inDither = false;
			options.inPreferredConfig = Bitmap.Config.ARGB_8888;

			// 根据options参数，减少所需要的内存
			bm = BitmapFactory.decodeFileDescriptor(fd, null, options);
		} catch (FileNotFoundException ex) {

		}

		return bm;
	}

	/**这个函数会对图片的大小进行判断，并得到合适的缩放比例，比如2即1/2,3即1/3**/
	static int computeSampleSize(BitmapFactory.Options options, int target) {
		int w = options.outWidth;
		int h = options.outHeight;
		int candidateW = w / target;
		int candidateH = h / target;
		int candidate = Math.max(candidateW, candidateH);
		if (candidate == 0)
			return 1;
		if (candidate > 1) {
			if ((w > target) && (w / candidate) < target)
				candidate -= 1;
		}
		if (candidate > 1) {
			if ((h > target) && (h / candidate) < target)
				candidate -= 1;
		}
		return candidate;
	}

	private static Bitmap getDefaultArtwork(Context context) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inPreferredConfig = Bitmap.Config.RGB_565;
		return BitmapFactory.decodeStream(context.getResources()
				.openRawResource(R.drawable.default_album), null, opts);
	}

	private static final Uri sArtworkUri = Uri
			.parse("content://media/external/audio/albumart");
	
	/**简单的抽屉效果**/
	private void ShowSliderDrawers() {
		    slidingdrawer.setOnDrawerOpenListener(new OnDrawerOpenListener() {
				
				@Override
				public void onDrawerOpened() {
					/**当抽屉打开时候显示y轴动画同时把控制播放隐藏起来。其目的是更好的看到歌词吧*/
					play_control.setVisibility(View.GONE);
					/**隐藏进度条**/
					play_seekbar.setVisibility(View.GONE);
					/**下展开**/
					ly_handle.setImageResource(R.drawable.lyr_handle_expand_default);
					/**隐藏专辑图片**/
					album.setVisibility(View.GONE);
				}
			});
       		slidingdrawer.setOnDrawerCloseListener(new OnDrawerCloseListener() {
       			
				@Override
				public void onDrawerClosed() {
				    /**关闭抽屉时候把播放控制按钮还原**/
					play_control.setVisibility(View.VISIBLE);
					/**还原进度条**/
					play_seekbar.setVisibility(View.VISIBLE);
					/**还原默认的**/
					ly_handle.setImageResource(R.drawable.lyr_handle_collapse_default);
					/**关闭抽屉效果专辑图片显示**/
					album.setVisibility(View.VISIBLE);
					
				}
			});
       		
	}

	
}
