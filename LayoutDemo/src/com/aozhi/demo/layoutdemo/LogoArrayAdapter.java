package com.aozhi.demo.layoutdemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class LogoArrayAdapter extends ArrayAdapter<String> {
	private final Context context;
	private final String[] values;

	public LogoArrayAdapter(Context context, String[] values) {
		super(context, R.layout.activity_img_list, values);
		this.context = context;
		this.values = values;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		if (rowView == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			rowView = inflater.inflate(R.layout.activity_img_list, parent, false);
			ItemViewCache cache = new ItemViewCache();

			cache.textView = (TextView) rowView.findViewById(R.id.label);
			cache.imageView = (ImageView) rowView.findViewById(R.id.logo);
			rowView.setTag(cache);
		}
		ItemViewCache viewCache = (ItemViewCache) rowView.getTag();
		viewCache.textView.setText(values[position]);

		// Change icon based on name
		String s = values[position];

		if (s.equals("WindowsMobile")) {
			viewCache.imageView.setImageResource(R.drawable.windowsmobile_logo);
		} else if (s.equals("iOS")) {
			viewCache.imageView.setImageResource(R.drawable.ios_logo);
		} else if (s.equals("Blackberry")) {
			viewCache.imageView.setImageResource(R.drawable.blackberry_logo);
		} else {
			viewCache.imageView.setImageResource(R.drawable.android_logo);
		}

		return rowView;
	}

	// 元素的缓冲类,用于优化ListView
	private static class ItemViewCache {
		public TextView textView;
		public ImageView imageView;
	}
}
