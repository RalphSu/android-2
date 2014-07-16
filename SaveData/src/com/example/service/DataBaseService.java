package com.example.service;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DataBaseService {

	private DbOpenHelper dbOpenHelper;

	public DataBaseService(Context context) {
		dbOpenHelper = new DbOpenHelper(context);
	}

	public void insert(String phoneName) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		db.execSQL("insert into phone(phonename,updateTime) values(?,?)", new String[] { phoneName, getUpdateTime() });
	}
	
	public Cursor getDataListCursor(int start,int limit) {
		SQLiteDatabase db=dbOpenHelper.getWritableDatabase();
		Cursor cursor =db.rawQuery("select * from phone order by _id desc limit ?,?", new String[] {String.valueOf(start),String.valueOf(limit)});
		return cursor;
	}

	private String getUpdateTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(new Date());
	}
}
