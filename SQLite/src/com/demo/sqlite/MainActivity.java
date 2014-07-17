package com.demo.sqlite;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;

import com.demo.sqlit.entity.Person;
import com.demo.sqlit.service.DataBaseService;
import com.demo.sqlite.adapter.MyAdapter;

public class MainActivity extends Activity {

	private ListView listView;
	private DataBaseService dataBaseService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		 listView = (ListView) this.findViewById(R.id.listView);
		dataBaseService = new DataBaseService(getApplicationContext());
		showList();
	}

	private void showList() {
		List<Person> persons=dataBaseService.getPersonList(0, 20);
		MyAdapter myAdapter=new MyAdapter(this, persons, R.layout.activity_item);
		listView.setAdapter(myAdapter);
	}
	private void showList3() {
		Cursor cursor = dataBaseService.getDataListCursor(0, 20);
		SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(getApplicationContext(), R.layout.activity_item,
				cursor, new String[] { "name", "phone", "amount" }, new int[] { R.id.name, R.id.phone, R.id.amount }, 0);
		
		listView.setAdapter(cursorAdapter);
	}

	private void showList2() {
		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		List<Person> list = dataBaseService.getPersonList(0, 20);
		for (Person person : list) {
			Map<String, Object> item = new HashMap<String, Object>();
			item.put("name", person.getName());
			item.put("phone", person.getPhone());
			item.put("amount", person.getAmount());
			item.put("id", person.getId());
			data.add(item);
		}

		SimpleAdapter simpleAdapter = new SimpleAdapter(getApplicationContext(), data, R.layout.activity_item,
				new String[] { "name", "phone", "amount" }, new int[] { R.id.name, R.id.phone, R.id.amount });
		listView.setAdapter(simpleAdapter);
	}

}
