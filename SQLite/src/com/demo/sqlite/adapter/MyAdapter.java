package com.demo.sqlite.adapter;

import java.util.List;

import com.demo.sqlit.entity.Person;
import com.demo.sqlite.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MyAdapter extends BaseAdapter {

	public MyAdapter(Context context, List<Person> persons, int resouce) {
		this.context = context;
		this.persons = persons;
		this.resource = resouce;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	}

	private Context context;
	private List<Person> persons;
	private int resource;
	private LayoutInflater inflater;

	@Override
	public int getCount() {
		return persons.size();
	}

	@Override
	public Object getItem(int position) {
		return persons.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			convertView = inflater.inflate(resource, parent, false);
			Cache cache = new Cache();
			cache.nameTextView = (TextView) convertView.findViewById(R.id.name);
			cache.phoneTextView = (TextView) convertView.findViewById(R.id.phone);
			cache.amountTextView = (TextView) convertView.findViewById(R.id.amount);
			convertView.setTag(cache);
		}
		Cache cache = (Cache) convertView.getTag();
		TextView nameTextView = cache.nameTextView;
		TextView phoneTextView = cache.phoneTextView;
		TextView amountTextView = cache.amountTextView;
		Person person = persons.get(position);
		nameTextView.setText(person.getName());
		phoneTextView.setText(person.getPhone());
		amountTextView.setText(person.getAmount() + "");

		return convertView;
	}

	private final class Cache {
		public TextView nameTextView;
		public TextView phoneTextView;
		public TextView amountTextView;
	}

}
