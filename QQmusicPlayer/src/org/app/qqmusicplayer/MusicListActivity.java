package org.app.qqmusicplayer;

import java.io.File;

import org.app.qqmusic.service.MusicService;
import org.app.qqmusic.tool.MusicListAdapter;
import org.app.qqmusic.tool.ScanSDReicver;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.method.DigitsKeyListener;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MusicListActivity extends Activity {
	/***�����б�**/
	private ListView listviews;
	/** ����������ʱ�������ֱ��ű��⣬������ **/
	private int _ids[];
	private String _titles[];
	private String _artists[];
	private String _path[];
	private String _alltime[];
	private AlertDialog ad = null;
	private AlertDialog.Builder builder = null;
	MusicListAdapter adapter;// �����б�������
	private int num;
	private int c;
	private TextView timers;//��ʾ����ʱ������
	
	/** �˵����� */
	private static final int SCAN = Menu.FIRST;
	private static final int CREATE_LIST = Menu.FIRST + 1;
	private static final int TIME_CLOSE = Menu.FIRST + 2;
	private static final int SETTINGS = Menu.FIRST + 3;
	private static final int HELP = Menu.FIRST + 4;
	private static final int EXIT = Menu.FIRST + 5;
    /**�����˵�����**/
	private static final String LISTMENU_PLAY = "���Ÿ���";
	private static final String LISTMENU_DEL = "ɾ���ļ�";
	private static final String LISTMENU_SLEEP = "����";
	private static final String LISTMENU_INFO = "������Ϣ";
	private static final int PLAY = 0;
	private static final int DELETE=1;
	private static final int SLEEP=2;
	private static final int INFO=3;
	Timers timer;//����ʱ�ڲ�����
	ScanSDReicver receiver;// ɨ��SD���ġ�

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.music_list);
		listviews = (ListView) findViewById(R.id.music_list);
		timers=(TextView) findViewById(R.id.timer_clock);
		/** ѡ���������¼�***/
		listviews.setOnItemClickListener(new MusicListOnClickListener());
		listviews.setOnCreateContextMenuListener(new MusicListOnContextMenu());
		ShowMp3List();
	}

	/** ��ʾmp3�б�*/
	private void ShowMp3List() {
		// ���α����ý����Ϣ
		Cursor cursor = this.getContentResolver().query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
				new String[] { MediaStore.Audio.Media.TITLE,
						MediaStore.Audio.Media.DURATION,
						MediaStore.Audio.Media.ARTIST,
						MediaStore.Audio.Media._ID,
						MediaStore.Audio.Media.DISPLAY_NAME,
						MediaStore.Audio.Media.DATA,
						MediaStore.Audio.Media.ALBUM_ID }, null, null, null);
		/** �ж��α��Ƿ�Ϊ�գ���Щ�ط���ʹû������Ҳ�ᱨ�쳣����Ϊ�α겻�ȶ������в����ͳ�����,��Σ�����û�û�����ָ�֪�û�û������ */
		if (null != cursor && cursor.getCount() == 0) {
			builder = new AlertDialog.Builder(this);
			builder.setMessage(R.string.music_list_tell).setPositiveButton(
					"ȷ��", null);
			ad = builder.create();
			ad.show();
			listviews.setBackgroundResource(R.drawable.list_empty);
			return;

		}
		/**���α��Ƶ���һλ**/
		cursor.moveToFirst();
		/** �ֱ�ʵ����**/
		_ids = new int[cursor.getCount()];//
		_titles = new String[cursor.getCount()];
		_artists = new String[cursor.getCount()];
		_path = new String[cursor.getCount()];
		_alltime=new String[cursor.getCount()];
		/***��ѭ������Ϣ���ҳ���*/
		for (int i = 0; i < cursor.getCount(); i++) {
			_ids[i] = cursor.getInt(3);
			_titles[i] = cursor.getString(0);
			_artists[i] = cursor.getString(2);
			_path[i] = cursor.getString(5).substring(4);
			_alltime[i]=(toTime(cursor.getInt(1)));
			/***һֱ���α�������**/
			cursor.moveToNext();
			System.out.println("ID��:" + _ids[i]);
			System.out.println("������:" + _titles[i]);
			System.out.println("����:" + _artists[i]);
			System.out.println("ʱ�䳤��:" + _alltime[i]);
		}
		listviews.setAdapter(new MusicListAdapter(this, cursor));

	}
	/**
	 * ʱ���ת��
	 */
	public String toTime(int time) {

		time /= 1000;
		int minute = time / 60;
		int second = time % 60;
		minute %= 60;
		return String.format("%02d:%02d", minute, second);
	}
	/** �����б���Ӽ����������֮�󲥷�����*/
	private class MusicListOnClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View view, int position,
				long id) {
			playMusic(position);

		}

	}
	/** ɨ��SD��*/
	private void ScanSDCard() {
		IntentFilter intentfilter = new IntentFilter(
				Intent.ACTION_MEDIA_SCANNER_STARTED);
		intentfilter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
		intentfilter.addDataScheme("file");
		receiver = new ScanSDReicver();
		registerReceiver(receiver, intentfilter);
		sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
				Uri.parse("file://"
						+ Environment.getExternalStorageDirectory()
								.getAbsolutePath())));
		

	}
	/** �����б�����λ�ÿ�ʼ����*/
	public void playMusic(int position) {
		Intent intent = new Intent(MusicListActivity.this,
				PlayMusicActivity.class);
		intent.putExtra("_ids", _ids);
		intent.putExtra("_titles", _titles);
		intent.putExtra("_artists", _artists);
		intent.putExtra("position", position);
		startActivity(intent);
		finish();

	}

	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, SCAN, 0, R.string.menu_scan).setIcon(R.drawable.menu_scan);
		menu.add(1, CREATE_LIST, 1, R.string.menu_crerte_list).setIcon(
				R.drawable.menu_create_list);
		menu.add(2, TIME_CLOSE, 2, R.string.menu_time_close).setIcon(
				R.drawable.menu_fixtime_quit);
		menu.add(3, SETTINGS, 3, R.string.settings).setIcon(
				R.drawable.menu_settings);
		menu.add(4, HELP, 4, R.string.menu_help).setIcon(R.drawable.menu_help);
		menu.add(5, EXIT, 5, R.string.menu_exit).setIcon(R.drawable.menu_quit);
		return true;
	}

@Override
	public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
	case SCAN:
		ScanSDCard();
		break;
	case CREATE_LIST:
		Toast.makeText(MusicListActivity.this, "��ʱ�����Ŵ˹���", Toast.LENGTH_SHORT).show();
		break;
	case TIME_CLOSE:
		sleeps();
		break;
	case SETTINGS:
		break;
	case HELP:
		Intent intent=new Intent(MusicListActivity.this,AboutActivity.class);
		startActivity(intent);
		break;
	case EXIT:
		exit();
		break;
	}
	return super.onOptionsItemSelected(item);
	}
/***�����ĵ���¼�***/
private class MusicListOnContextMenu implements OnCreateContextMenuListener{

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo info) {
		menu.setHeaderTitle(R.string.Content_Menu_title);
		menu.add(0, PLAY, 0, LISTMENU_PLAY);
		menu.add(1, DELETE, 1, LISTMENU_DEL);
		menu.add(2, SLEEP, 2, LISTMENU_SLEEP);
		menu.add(3, INFO, 3, LISTMENU_INFO);
		final AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) info;
		num = menuInfo.position;
		
	}

	
}
@Override
public boolean onContextItemSelected(MenuItem item) {
    switch (item.getItemId()) {
	case PLAY:
	   playMusic(num);
		break;
	case DELETE:
		deleteMusic();
		break;
	case SLEEP:
		sleeps();
		break;
	case INFO:
		MusicInfo();
		break;
		
	}
	return false;
}

/**
 * ��ʾ������ϸ��Ϣ
 */
	private void MusicInfo() {
		String[] infos = { "����:" + _titles[num], "����:" + _artists[num],
				"·��:" + _path[num],"ʱ��:"+_alltime[num]};
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(android.R.drawable.btn_star);
		builder.setTitle("������Ϣ:");
		builder.setItems(infos, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

			}
		});
		builder.setPositiveButton(R.string.edit, new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface arg0, int arg1) {

			}
		});
		builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface arg0, int arg1) {

			}
		});
		builder.create().show();
	
	}




/**
 * ɾ������
 */
private void deleteMusic() {
	AlertDialog.Builder builder = new AlertDialog.Builder(this);
	builder.setMessage(R.string.back_title)
			.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					deleteMusic(num); // ���б���ɾ������
					deleteMusicFile(num); // ��SD����ɾ������
					ShowMp3List(); // ���»���б���ҩ��ʾ������
					adapter.notifyDataSetChanged();// ֪ͨ����UI

				}
			}).setNegativeButton(R.string.no, null).show();

}
/**
 * ���б���ɾ��ѡ�е�����
 */
private void deleteMusic(int position) {
	this.getContentResolver().delete(
			MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
			MediaStore.Audio.Media._ID + "=" + _ids[position], null);
}

/**
 * ��sdcard��ɾ��ѡ�е�����,���Ǿ��û�������԰���try������Ϊ��
 */
private void deleteMusicFile(int position) {
	try {
		File file = new File(_path[position]);
		file.delete();
	} catch (Exception e) {

		e.printStackTrace();
	}
	System.out.println(_path[position]);
}



@Override
public boolean onKeyDown(int keyCode, KeyEvent event) {
	if(keyCode == KeyEvent.KEYCODE_BACK &&event.getRepeatCount() == 0) {
		new AlertDialog.Builder(MusicListActivity.this)
			.setMessage("ȷ���˳���?")
			.setTitle("��ʾ").setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Intent mediaServer = new Intent(MusicListActivity.this, 
							MusicService.class);
					stopService(mediaServer);
					finish();
				}
			})
			.setNegativeButton("ȡ��", null)
			.show();
		return false;
	}
	return super.onKeyDown(keyCode, event);
}

	/**
	 * �˳������������˳��Ҿ����ȰѲ��ŷ����ֹͣ��Ȼ��ֱ��finish��
	 */
	private void exit() {
		Intent Exitintent = new Intent(this, MusicService.class);
		stopService(Exitintent);
		finish();
	}
//һ������ʱ
	class Timers extends CountDownTimer{

		public Timers(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
			 if (c==0) {
				finish();
			     exit();
			}else {
				finish();
				onDestroy();
				android.os.Process.killProcess(android.os.Process.myPid());
			}
		}

		@Override
		public void onTick(long millisUntilFinished) {
			timers.setText("" + millisUntilFinished / 1000 / 60 + ":"
					+ millisUntilFinished / 1000 % 60);
			// �������������9 ˵������2λ����,����ֱ�����롣����С�ڵ���9 �Ǿ���1λ��������ǰ���һ��0
			String abc = (millisUntilFinished / 1000 / 60) > 9 ? (millisUntilFinished / 1000 / 60)
					+ ""
					: "0" + (millisUntilFinished / 1000 / 60);
			String b = (millisUntilFinished / 1000 % 60) > 9 ? (millisUntilFinished / 1000 % 60)
					+ ""
					: "0" + (millisUntilFinished / 1000 % 60);
			timers.setText(abc + ":" + b);
		}
		
	}
	/**
	 * ���߷���
	 */
	private void sleeps() {
		final EditText edtext = new EditText(this);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.dormant_time_minute);
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setView(edtext);
		edtext.setText("5");
		edtext.setHint(R.string.dormant_time_hint);
		edtext.setKeyListener(new DigitsKeyListener(false, true));
		edtext.setGravity(Gravity.CENTER_HORIZONTAL);
		edtext.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
		edtext.setTextColor(Color.RED);
		edtext.setSelection(edtext.length());
		edtext.selectAll();
		builder.setPositiveButton(R.string.yes,
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface arg0, int arg1) {

						if (edtext.length() <= 2 && edtext.length() != 0) {
							if (".".equals(edtext.getText().toString())) {
								Toast.makeText(MusicListActivity.this, "����",
										Toast.LENGTH_SHORT).show();
							} else {
								final String time = edtext.getText().toString();
								long Money = Integer.parseInt(time);
								long cX = Money * 60000;
								timer= new Timers(cX, 1000);
							 timer.start();
								Toast.makeText(
										MusicListActivity.this,
										"����ģʽ����!\n" + String.valueOf(time)
												+ "\t���Ӻ�رճ���!",
										Toast.LENGTH_LONG).show();
								timers.setVisibility(View.INVISIBLE);
								timers.setVisibility(View.VISIBLE);
								timers.setText(String.valueOf(time));
							}

						} else {
							Toast.makeText(MusicListActivity.this, "�����뼸����",
									Toast.LENGTH_SHORT).show();
						}
					}

				});

		builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface arg0, int arg1) {
			}
		});
		builder.create().show();

	}
}
