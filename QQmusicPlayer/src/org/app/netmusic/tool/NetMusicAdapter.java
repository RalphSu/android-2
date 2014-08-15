package org.app.netmusic.tool;

import java.util.ArrayList;
import java.util.HashMap;

import org.app.qqmusicplayer.MusicOnlineActivity;
import org.app.qqmusicplayer.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NetMusicAdapter extends BaseAdapter {
	private Activity activity;
	private ArrayList<HashMap<String, String>> data;
	private static LayoutInflater inflater = null;
	public DowdloadImage imageLoader; // 用来下载图片的类，

	public NetMusicAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
		activity = a;
		data = d;
		inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imageLoader = new DowdloadImage(activity.getApplicationContext());
	}

	@Override
	public int getCount() {
		  return data.size();  
	}

	@Override
	public Object getItem(int position) {
		 return position;  
	}

	@Override
	public long getItemId(int position) {
		 return position;  
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		 View vi=convertView;  
	        if(convertView==null)  
	            vi = inflater.inflate(R.layout.net_music_item, null);
	        TextView title = (TextView)vi.findViewById(R.id.net_musicname); // 标题  
	        TextView artist = (TextView)vi.findViewById(R.id.net_singer); // 歌手名  
	        TextView duration = (TextView)vi.findViewById(R.id.net_time); // 时长  
	        ImageView thumb_image=(ImageView)vi.findViewById(R.id.net_images_album); // 缩略图  
	          
	        HashMap<String, String> song = new HashMap<String, String>();  
	        song = data.get(position);
	        title.setText(song.get(MusicOnlineActivity.KEY_TITLE));  
	        artist.setText(song.get(MusicOnlineActivity.KEY_ARTIST));  
	        duration.setText(song.get(MusicOnlineActivity.KEY_DURATION));  
	        imageLoader.DisplayImage(song.get(MusicOnlineActivity.KEY_THUMB_URL), thumb_image);  
		return vi;
	}

}
