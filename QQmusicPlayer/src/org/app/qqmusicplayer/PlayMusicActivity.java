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
 * �����ǲ��Ž���ģ�顣ԭ�ȵĲ��ŵ���û�÷�����ѣ���ΰѺ�̨�ݺ�ǿ���£������ܺ�̨���ţ����Ҳ�ȡ������ŵĹ���
 * ��߶�һЩ��Դ�Լ�״̬�Ĳ���������OnStart�½��С���ʾ�������֣�����loadclip ����+��ʵ���readlrc��·�������ֻ�SD����ͬ����ͬ����������֮ǰ�޸��������ʵ��·�� 
 * �����ԣ�������ʾ���йصĶ������ǵ���OnStart����
 */
public class PlayMusicActivity extends Activity {
	private int[] _ids;// ��ʱ����id
	private String[] _artists;// ������
	private String[] _titles;// ����
	private TextView musicnames;// ������
	private TextView artisting;// ������
	private ImageButton play_btn;// ���Ű�ť
	private ImageButton last_btn;// ��һ��
	private ImageButton next_btn;// ��һ��
	private TextView playtimes = null;// �Ѳ���ʱ��
	private TextView durationTime = null;// ����ʱ��
	private SeekBar seekbar;// ������
	private int position;// ����һ��λ�ã����ڶ�λ�û�����б�����
	private int currentPosition;// ��ǰ����λ��
	private int duration;// ��ʱ��
	private TextView lrcTextview;// ���
    private ImageView album;// ר��
	private SlidingDrawer slidingdrawer;
	private ImageButton LoopBtn = null;// ѭ��
	private ImageButton RandomBtm = null;// ���
	private ImageView ly_handle;//�ֶ�
	private static final String MUSIC_CURRENT = "com.app.currentTime";// Action��״̬
	private static final String MUSIC_DURATION = "com.app.duration";
	private static final String MUSIC_NEXT = "com.app.next";
	private static final String MUSIC_UPDATE = "com.app.update";
	private static final int PLAY = 1;// ���岥��״̬
	private static final int PAUSE = 2;// ��ͣ״̬
	private static final int STOP = 3;// ֹͣ
	private static final int PROGRESS_CHANGE = 4;// �������ı�
	private static final int STATE_PLAY = 1;// ����״̬��Ϊ1
	private static final int STATE_PAUSE = 2;// ����״̬��Ϊ2
	public static final int LOOP_NONE = 0;//��ѭ��
	public static final int LOOP_ONE = 1;//����ѭ��
	public static final int LOOP_ALL = 2;//ȫ��ѭ��
	public static int loop_flag = LOOP_NONE;
	public static boolean random_flag = false;
	public static int[] randomIDs = null;
	public static int randomNum = 0;
	private int flag;// ���
	private Cursor cursor;// �α�
	private TreeMap<Integer, LRCbean> lrc_map = new TreeMap<Integer, LRCbean>();// Treemap����
	private AudioManager am;
	private int maxVolume;// �������
	private int currentVolume;// ��ǰ����
	public Context context;//������
    private RelativeLayout play_control;//����̨
    private RelativeLayout play_seekbar;//������
    float start;
    float end;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.play_music);
		Intent intent = this.getIntent();//���������б������
		Bundle bundle = intent.getExtras();
		_ids = bundle.getIntArray("_ids");
		randomIDs = new int[_ids.length];
		position = bundle.getInt("position");
		_titles = bundle.getStringArray("_titles");
		_artists = bundle.getStringArray("_artists");
		// �������Ҹ����ؼ�ID�������Ϥ�ģ��ʲ�����
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
		// ����д������ť������������ID֮��Ӽ������ڼ���д����
		ShowPlayBtn();
		ShowLastBtn();
		ShowNextBtn();
		ShowSeekBar();
		ShowLoop();
		ShowRandom();
		ShowSliderDrawers();
		
		
	}

	
	/** ��ʾ���*/
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
					Toast.makeText(PlayMusicActivity.this, "��ѡ������������", Toast.LENGTH_SHORT).show();
				} else {
					random_flag = false;
					RandomBtm.setBackgroundResource(R.drawable.random);
				}

			}

		});

	}
/** ��ʾ��˳����ߵ����ظ�����*/
	private void ShowLoop() {
		LoopBtn = (ImageButton) findViewById(R.id.playback_mode);
		LoopBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (loop_flag) {
				case LOOP_NONE:
					loop_flag = LOOP_ONE;
					LoopBtn.setBackgroundResource(R.drawable.loop_one);
					Toast.makeText(PlayMusicActivity.this, "��ѡ����ǵ����ظ�", Toast.LENGTH_SHORT).show();
					break;

				case LOOP_ONE:
					loop_flag = LOOP_ALL;
					LoopBtn.setBackgroundResource(R.drawable.loop_all);
					Toast.makeText(PlayMusicActivity.this, "��ѡ�����ȫ��ѭ��", Toast.LENGTH_SHORT).show();
					break;
				case LOOP_ALL:
					loop_flag = LOOP_NONE;
					LoopBtn.setBackgroundResource(R.drawable.loop_none);
					Toast.makeText(PlayMusicActivity.this, "��ѡ����ǵ���ѭ��", Toast.LENGTH_SHORT).show();
					break;
				}
			}
		});

	}

	/** ���Ű�ť*/
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

	/** ��ʾ������*/
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

	/** ��ʾ��һ��*/
	public void ShowNextBtn() {
		next_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				nextOne();

			}
		});

	}

	/** ��ʾ��һ��*/
	public void ShowLastBtn() {
		
		last_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				lastOne();
			}
		});

	}

	/** ��������*/
	public void play() {
		flag = PLAY;
		play_btn.setImageResource(R.drawable.pause_button);
		Intent intent = new Intent();
		intent.setAction("com.app.media.MUSIC_SERVICE");
		intent.putExtra("op", PLAY);// ����񴫵�����
		startService(intent);

	}

	/** ��ͣ��������*/
	public void pause() {
		flag = PAUSE;
		play_btn.setImageResource(R.drawable.play_button);
		Intent intent = new Intent();
		intent.setAction("com.app.media.MUSIC_SERVICE");
		intent.putExtra("op", PAUSE);
		startService(intent);

	}

	/*** �������ı�*/
	private void seekbar_change(int progress) {
		Intent intent = new Intent();
		intent.setAction("com.app.media.MUSIC_SERVICE");
		intent.putExtra("op", PROGRESS_CHANGE);
		intent.putExtra("progress", progress);
		startService(intent);

	}

	/** ��һ��*/
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
	 * ��һ��
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
    /**�ҵ����λ��**/
	public static int findRandomSound(int end) {
		int ret = -1;
		ret = (int) (Math.random() * end);
		while (havePlayed(ret, end)) {
			ret = (int) (Math.random() * end);
		}
		return ret;
	}
  /**�Ƿ��ڲ���**/
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
	 * ֹͣ��������
	 */
	private void stop() {
		Intent isplay = new Intent("notifi.update");
		sendBroadcast(isplay);// �����̨֧��
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
	 * �����治�ɼ�ʱ�򣬷�ע��Ĺ㲥
	 */
	@Override
	protected void onStop() {
		super.onStop();
		unregisterReceiver(musicReceiver);
	}

	/**
	 * ���·��ؼ��¼�
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
	 * ��ʼ��
	 */
	private void setup() {
		loadclip();
		init();
		ReadSDLrc();

	}

	/**
	 * ����ʷ���
	 */
	private void ReadSDLrc() {
		/**�������ڵĸ�ʾ���ҪString����ĵ�4������-��ʾ�ļ�����**/
		cursor = getContentResolver().query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
				new String[] { MediaStore.Audio.Media.TITLE,
						MediaStore.Audio.Media.DURATION,
						MediaStore.Audio.Media.ARTIST,
						MediaStore.Audio.Media.ALBUM,
						MediaStore.Audio.Media.DISPLAY_NAME,
						MediaStore.Audio.Media.ALBUM_ID }, "_id=?",
				new String[] { _ids[position] + "" }, null);
		cursor.moveToFirst();// ���α�������һλ
		Bitmap bm = getArtwork(this, _ids[position], cursor.getInt(5), true);
		/**�л�����ʱ��ר��ͼƬ����͸��Ч��**/
		Animation albumanim=AnimationUtils.loadAnimation(context, R.anim.album_replace);
		/**��ʼ���Ŷ���Ч��**/
		album.startAnimation(albumanim);
		album.setImageBitmap(bm);
		/**�α궨λ��DISPLAY_NAME**/
		String name = cursor.getString(4);
		/**sd�����������ֽ�ȡ�ַ��ܲ��ҵ�����λ��,�ⲽ��Ҫ��û��дһֱ��ʾ����ļ��޷���ʾ,˳��˵����ͬ�ֻ��ͺ�SD���в�ͬ��·����**/
		read("/sdcard/music/" + name.substring(0, name.indexOf(".")) + ".lrc");
		/** �ڵ���ʱ���Ȱ���������д��������ʱ���ڿ���̨��ӡ���������֣���ô�ɴ��жϸ���û����.ֻ��û�л�ȡλ��**/
		System.out.println(cursor.getString(4));

	}
	/**
	 * ��ȡ��ʵķ���������IO����һ��һ�е���ʾ
	 */
	private void read(String path) {
		lrc_map.clear();
		TreeMap<Integer, LRCbean> lrc_read = new TreeMap<Integer, LRCbean>();
		String data = "";
		BufferedReader br = null;
		File file = new File(path);
		System.out.println(path);
		/**���û�и�ʣ�����û�и����ʾ**/
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
					if (data.charAt(3) == ':' && data.charAt(6) == '.') {// �Ӹ�����Ŀ�ʼ
						data = data.replace("[", "");
						data = data.replace("]", "@");
						data = data.replace(".", ":");
						String lrc[] = data.split("@");
						String lrcContent = null;
						if (lrc.length == 2) {
							lrcContent = lrc[lrc.length - 1];// ���
						} else {
							lrcContent = "";
						}

						for (int i = 0; i < lrc.length - 1; i++) {
							String lrcTime[] = lrc[0].split(":");

							int m = Integer.parseInt(lrcTime[0]);// ��
							int s = Integer.parseInt(lrcTime[1]);// ��
							int ms = Integer.parseInt(lrcTime[2]);// ����

							int begintime = (m * 60 + s) * 1000 + ms;// ת���ɺ���
							LRCbean lrcbean = new LRCbean();
							lrcbean.setBeginTime(begintime);// ���ø�ʿ�ʼʱ��
							lrcbean.setLrcBody(lrcContent);// ���ø�ʵ�����
							lrc_read.put(begintime, lrcbean);

						}

					}
				}
			}
			stream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// ����ÿ������Ҫ��ʱ��
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
	 * ��ʼ������
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
	 * ��ȡ���������ֵ��ַ���
	 */
	private void loadclip() {
		seekbar.setProgress(0);
		/**���ø�����**/
		if (_titles[position].length() > 15)
			musicnames.setText(_titles[position].substring(0, 12) + "...");// ���ø�����
		else
			musicnames.setText(_titles[position]);

		/**������������**/ 
		if (_artists[position].equals("<unknown>"))
			artisting.setText("δ֪������");
		else
			artisting.setText(_artists[position]);
		Intent intent = new Intent();
		intent.putExtra("_ids", _ids);
		intent.putExtra("_titles", _titles);
		intent.putExtra("_artists", _artists);
		intent.putExtra("position", position);
	    intent.setAction("com.app.media.MUSIC_SERVICE");// �������action���ͷ���
		startService(intent);

	}

	/**�ں�̨MusicService��ʹ��handler��Ϣ���ƣ���ͣ����ǰ̨���͹㲥���㲥����������ǵ�ǰmp���ŵ�ʱ��㣬
	 * ǰ̨���յ��㲥���ò���ʱ��������½�����,����������������һЩ��˵��Ȼ������ʵ�֡����ǻ��Ǿ��ÿ������̲߳���**/
	protected BroadcastReceiver musicReceiver = new BroadcastReceiver() {

		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(MUSIC_CURRENT)) {
				currentPosition = intent.getExtras().getInt("currentTime");// ��õ�ǰ����λ��
				playtimes.setText(toTime(currentPosition));
				seekbar.setProgress(currentPosition);// ���ý�����
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
	 * ����ʱ��ת��
	 */
	public String toTime(int time) {

		time /= 1000;
		int minute = time / 60;
		int second = time % 60;
		minute %= 60;
		return String.format("%02d:%02d", minute, second);
	}

	

	/**
	 * �ص�������С����
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
	 * �����Ǹ����ŵ�ʱ����ʾר��ͼƬ�����б�ͬ,����ʱͼƬҪ������cam�Ǹ�����д���ʵ�ͼƬ��
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
				/**��ָ��ԭʼ��С**/
				options.inSampleSize = 1;
				/** ֻ���д�С�ж�**/
				options.inJustDecodeBounds = true;
				/**���ô˷����õ�options�õ�ͼƬ�Ĵ�С**/
				BitmapFactory.decodeStream(in, null, options);
				/**���ǵ�Ŀ��������N pixel�Ļ�������ʾ�� ������Ҫ����computeSampleSize�õ�ͼƬ���ŵı���**/
				options.inSampleSize = computeSampleSize(options, 100);
				/**���ǵõ������ŵı��������ڿ�ʼ��ʽ����BitMap����**/
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
			// ֻ���д�С�ж�
			options.inJustDecodeBounds = true;
			// ���ô˷����õ�options�õ�ͼƬ�Ĵ�С
			BitmapFactory.decodeFileDescriptor(fd, null, options);
			// ���ǵ�Ŀ������800pixel�Ļ�������ʾ��
			// ������Ҫ����computeSampleSize�õ�ͼƬ���ŵı���
			options.inSampleSize = 100;
			// OK,���ǵõ������ŵı��������ڿ�ʼ��ʽ����BitMap����
			options.inJustDecodeBounds = false;
			options.inDither = false;
			options.inPreferredConfig = Bitmap.Config.ARGB_8888;

			// ����options��������������Ҫ���ڴ�
			bm = BitmapFactory.decodeFileDescriptor(fd, null, options);
		} catch (FileNotFoundException ex) {

		}

		return bm;
	}

	/**����������ͼƬ�Ĵ�С�����жϣ����õ����ʵ����ű���������2��1/2,3��1/3**/
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
	
	/**�򵥵ĳ���Ч��**/
	private void ShowSliderDrawers() {
		    slidingdrawer.setOnDrawerOpenListener(new OnDrawerOpenListener() {
				
				@Override
				public void onDrawerOpened() {
					/**�������ʱ����ʾy�ᶯ��ͬʱ�ѿ��Ʋ���������������Ŀ���Ǹ��õĿ�����ʰ�*/
					play_control.setVisibility(View.GONE);
					/**���ؽ�����**/
					play_seekbar.setVisibility(View.GONE);
					/**��չ��**/
					ly_handle.setImageResource(R.drawable.lyr_handle_expand_default);
					/**����ר��ͼƬ**/
					album.setVisibility(View.GONE);
				}
			});
       		slidingdrawer.setOnDrawerCloseListener(new OnDrawerCloseListener() {
       			
				@Override
				public void onDrawerClosed() {
				    /**�رճ���ʱ��Ѳ��ſ��ư�ť��ԭ**/
					play_control.setVisibility(View.VISIBLE);
					/**��ԭ������**/
					play_seekbar.setVisibility(View.VISIBLE);
					/**��ԭĬ�ϵ�**/
					ly_handle.setImageResource(R.drawable.lyr_handle_collapse_default);
					/**�رճ���Ч��ר��ͼƬ��ʾ**/
					album.setVisibility(View.VISIBLE);
					
				}
			});
       		
	}

	
}
