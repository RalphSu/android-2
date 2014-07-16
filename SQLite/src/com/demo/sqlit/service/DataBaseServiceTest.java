package com.demo.sqlit.service;

import java.util.List;

import com.demo.sqlit.dbutils.DbOpenHelper;
import com.demo.sqlit.entity.Person;

import android.R.integer;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;

public class DataBaseServiceTest extends AndroidTestCase {
	
	
	public void testCreateDb() {
		DbOpenHelper dbOpenHelper=new DbOpenHelper(this.getContext());
		dbOpenHelper.getReadableDatabase();
	}
	
	public void testAdd() {
		
		DataBaseService service=new DataBaseService(this.getContext());
		for (int i=0;i<20;i++) {
			
			Person entity=new Person(0, "aozhi"+i, "15220018510", 100);
		service.add(entity);
		}
		
	}
	public void testGetPersonList() {
		
		DataBaseService service=new DataBaseService(this.getContext());
		List<Person> list=service.getPersonList(0, 10);
		for (Person person : list) {
			Log.i("dbservice", person.toString());
		}
	}

}
