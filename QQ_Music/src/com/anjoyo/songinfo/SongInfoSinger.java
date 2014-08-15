package com.anjoyo.songinfo;

import java.util.List;
import java.util.Map;

import com.anjoyo.qq_music.R;
import com.anjoyo.resource.GetMusicData;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class SongInfoSinger extends Activity {
	ListView listView;
	SimpleAdapter simpleAdapter;
    List<Map<String,Object>>  datalist;
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.song_2);
		listView = (ListView) findViewById(R.id.singer_info);
		datalist=GetMusicData.GetMusicData(this);
		listView.setAdapter(new MyAdapter1(this));
		Toast.makeText(this, "∏Ë ÷",Toast.LENGTH_SHORT).show();
	}
	private class MyAdapter1 extends BaseAdapter {
		LayoutInflater inflater;

		public MyAdapter1(Context c) {
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
				convertView = inflater.inflate(R.layout.song_2_item, null);
				viewHolder = new ViewHolder();
				viewHolder.imageview = (ImageView) convertView
						.findViewById(R.id.singer_info_image);
				viewHolder.siner_name = (TextView) convertView
						.findViewById(R.id.singer_info_name);
				
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			
			viewHolder.siner_name.setText(datalist.get(position).get("SINGER")
					.toString());
			viewHolder.imageview.setBackgroundResource(R.drawable.default_mini_singer);
			
			return convertView;
		}

		class ViewHolder {
			ImageView imageview;
			TextView siner_name;

		}
	}
}
