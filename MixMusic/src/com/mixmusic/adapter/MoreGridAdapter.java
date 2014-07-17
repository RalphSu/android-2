package com.mixmusic.adapter;

import java.util.List;

import com.mixmusic.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MoreGridAdapter extends BaseAdapter{
	private List<MoreItem> dataList;
	private Context mContext;

	public MoreGridAdapter(Context context,List<MoreItem> data){
		setData(data);
		mContext=context;
	}

	public void setData(List<MoreItem> data){
		this.dataList=data;
	}
	@Override
	public int getCount() {
		return dataList==null?0:dataList.size();
	}

	@Override
	public MoreItem getItem(int position) {
		return dataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	private MoreItemHolder initHolder(View convertView){
		MoreItemHolder holder=new MoreItemHolder();
		holder. icon=(ImageView)convertView.findViewById(R.id.more_icon);
		holder. text=(TextView)convertView.findViewById(R.id.more_text);
		return holder;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		final MoreItem item=getItem(position);
		MoreItemHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.music_moregrid_item, null);
			holder=initHolder(convertView);
			convertView.setTag(holder);
		}else{
			holder = (MoreItemHolder) convertView.getTag();
		}
		
		holder.icon.setImageDrawable(item.icon);
		holder.text.setText(item.text);
		return convertView;
	}
	
	class MoreItemHolder{
		ImageView icon;
		TextView text;
	}
}

