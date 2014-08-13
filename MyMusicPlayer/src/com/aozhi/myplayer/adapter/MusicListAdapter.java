package com.aozhi.myplayer.adapter;

import java.util.List;
import com.aozhi.myplayer.R;
import com.aozhi.myplayer.MusicRetriever.Item;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MusicListAdapter extends BaseAdapter {
	private Context context;
	private List<Item> datas;

	public MusicListAdapter(Context context,List<Item> items)
	{
		this.context=context;
		this.datas=items;
	}
	@Override
	public int getCount() {
		return datas.size();
	}

	@Override
	public Object getItem(int position) {
		return datas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView==null) {
			convertView=LayoutInflater.from(context).inflate(R.layout.player_music_item, null);
			holder=new ViewHolder();
			holder.text=(TextView)convertView.findViewById(R.id.music_item);
			convertView.setTag(holder);
		}else {
			holder=(ViewHolder)convertView.getTag();
		}
		holder.text.setText(((Item)getItem(position)).getTitle());
		return convertView;
	}
	 static class ViewHolder {
         TextView text;
         ImageView icon;
     }
}
