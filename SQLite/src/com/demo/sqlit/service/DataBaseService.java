package com.demo.sqlit.service;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.demo.sqlit.dbutils.DbOpenHelper;
import com.demo.sqlit.entity.Person;

public class DataBaseService {

	private DbOpenHelper dbOpenHelper;

	public DataBaseService(Context context) {
		dbOpenHelper = new DbOpenHelper(context);
	}

	public void add(Person entity) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		db.execSQL("insert into person(name,phone,amount) values (?,?,?)", new Object[] {entity.getName(),entity.getPhone(),entity.getAmount()});
	}
	
	public void delete(Integer  id) {
		SQLiteDatabase db=dbOpenHelper.getWritableDatabase();
		db.execSQL("delete from person where id=?", new Object[] {id});
	}
	
	public void update(Person entity) {
		SQLiteDatabase db=dbOpenHelper.getWritableDatabase();
		db.execSQL("update person set name=?,phone=?,amount=? where _id=?", new Object[] {entity.getName(),entity.getPhone(),entity.getAmount(),entity.getId()});
	}
	
	public Person query(Integer id) {
		SQLiteDatabase db=dbOpenHelper.getReadableDatabase();
		Cursor cursor=db.rawQuery("select * from person where _id =?", new String[] {id.toString()});
		if(cursor.moveToFirst()) {
			String name=cursor.getString(cursor.getColumnIndex("name"));
			String phone=cursor.getString(cursor.getColumnIndex("phone"));
			int amount=cursor.getInt(cursor.getColumnIndex("amount"));
			cursor.close();
			return new Person(id.intValue(),name,phone,amount);
		}
		cursor.close();
		return null;
		
	}
	public Cursor getDataListCursor(int start,int limit) {
		SQLiteDatabase db=dbOpenHelper.getWritableDatabase();
		Cursor cursor =db.rawQuery("select * from person order by _id desc limit ?,?", new String[] {String.valueOf(start),String.valueOf(limit)});
		return cursor;
	}
	public List<Person> getPersonList(int start,int limit) {
		List<Person> list=new ArrayList<Person>();
		SQLiteDatabase db=dbOpenHelper.getWritableDatabase();
		Cursor cursor =db.rawQuery("select * from person order by _id desc limit ?,?", new String[] {String.valueOf(start),String.valueOf(limit)});
		while(cursor.moveToNext()) {
			int id=cursor.getInt(cursor.getColumnIndex("_id"));
			String name=cursor.getString(cursor.getColumnIndex("name"));
			String phone=cursor.getString(cursor.getColumnIndex("phone"));
			int amount=cursor.getInt(cursor.getColumnIndex("amount"));
			list.add(new Person(id,name,phone,amount));
		}
		cursor.close();
		return list;
	}

}