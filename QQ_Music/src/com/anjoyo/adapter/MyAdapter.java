package com.anjoyo.adapter;

import com.anjoyo.qq_music.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class MyAdapter extends BaseAdapter {
	int image1[] = new int[] { R.drawable.cloud_all_music,
			R.drawable.cloud_downloaded_music, R.drawable.cloud_folder,
			R.drawable.cloud_my_favourite_normal,
			R.drawable.cloud_my_music_list };

	String text1[]=new String[]{"1","2","3","4","51"};
	String text2[]=new String[]{"1","2","3","4","51"};
	public Context context;
	public MyAdapter(Context context)
	{
		this.context=context;
	}
	public int getCount() {
		// TODO Auto-generated method stub
		return 5;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		return null;
	}

}
