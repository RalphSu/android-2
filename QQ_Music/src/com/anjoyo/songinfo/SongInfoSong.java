package com.anjoyo.songinfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.anjoyo.qq_music.R;
import com.anjoyo.resource.GetMusicData;
import com.anjoyo.service.MediaPlayService;
import com.anjoyo.service.MediaPlayService.mBinder;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class SongInfoSong extends Activity implements OnItemClickListener {
	ListView listView;
	List<Map<String, Object>> datalist;
	MediaPlayService mediaPlayService;
	public SharedPreferences preferences;
	int pesition = -1, play_state;
	View view;
	ImageView imageView;

	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.song_1);
		listView = (ListView) findViewById(R.id.song_name_list);
		datalist = GetMusicData.GetMusicData(this);
		listView.setAdapter(new MyAdapter(this));
		Toast.makeText(this, "共有" + datalist.size() + "首歌", Toast.LENGTH_SHORT)
				.show();
		listView.setOnItemClickListener(songListclickListener);

		IntentFilter filter = new IntentFilter();
		filter.addAction("isplay");
		filter.addAction("info");
		registerReceiver(receiver, filter);

	}

	BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals("isplay")) {
				play_state = intent.getIntExtra("state", 0);
			}
			if (intent.getAction().equals("info")) {
				pesition = intent.getIntExtra("pesition", -1);
			}
		}
	};

	// 点击列表播放音乐
	OnItemClickListener songListclickListener = new OnItemClickListener() {

		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {

			Intent intent = new Intent(SongInfoSong.this,
					MediaPlayService.class);
			intent.putExtra("pesition", arg2);
			if (pesition != arg2) {
				intent.putExtra("what", "play");
			}
			if (pesition == arg2) {
				intent.putExtra("what", "pause");
			}
			if (pesition == arg2 && play_state == 0) {
				intent.putExtra("what", "restart");
			}
			startService(intent);

			if(view==null){
				view=arg1;
				imageView=(ImageView) view.findViewById(R.id.isplay);
				imageView.setVisibility(View.VISIBLE);
			}
			else {
				imageView = (ImageView) view.findViewById(R.id.isplay);
				imageView.setVisibility(View.GONE);

				imageView = (ImageView) arg1.findViewById(R.id.isplay);
				imageView.setVisibility(View.VISIBLE);
				
				view = arg1;

			}
		}
	};

	private class MyAdapter extends BaseAdapter {
		LayoutInflater inflater;

		public MyAdapter(Context c) {
			this.inflater = LayoutInflater.from(c);
		}

		public int getCount() {
			return datalist.size();
		}

		public Object getItem(int position) {
			return null;
		}

		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ViewHolder viewHolder;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.song_1_item, null);
				viewHolder = new ViewHolder();
				viewHolder.isplay = (ImageView) convertView
						.findViewById(R.id.isplay);
				viewHolder.song_name = (TextView) convertView
						.findViewById(R.id.song_name);
				viewHolder.siner_name = (TextView) convertView
						.findViewById(R.id.singer_name);
				viewHolder.downloadimage = (ImageView) convertView
						.findViewById(R.id.download_image);
				viewHolder.listview_click = (TextView) convertView
						.findViewById(R.id.songlist_click);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			viewHolder.song_name.setText(datalist.get(position).get("TITLE")
					.toString());
			viewHolder.siner_name.setText(datalist.get(position).get("SINGER")
					.toString());

			viewHolder.downloadimage
					.setBackgroundResource(R.drawable.tenpay_icon_sucess);
			viewHolder.isplay
					.setBackgroundResource(R.drawable.play_list_played);
			
			viewHolder.listview_click.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Toast.makeText(SongInfoSong.this, position + "内容",
							Toast.LENGTH_SHORT).show();
				}
			});
			return convertView;
		}

		class ViewHolder {
			ImageView isplay, downloadimage;
			TextView song_name, listview_click;
			TextView siner_name;

		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub

	}

}
