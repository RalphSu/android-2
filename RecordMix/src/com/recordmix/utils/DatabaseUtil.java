package com.recordmix.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseUtil{

	private static final String TAG = "DatabaseUtil";

	/**
	 * 数据库名称
	 */
	private static final String DATABASE_NAME = "recordmix_database";

	/**
	 * 数据库版本
	 */
	private static final int DATABASE_VERSION = 1;

	/**
	 * 表名
	 */
	private static final String DATABASE_TABLE = "tb_recordmix";

	/**
	 * 列名
	 */
	public static final String KEY_NAME = "name";
	public static final String KEY_DATETIME = "datetime";
	public static final String KEY_PATH = "path";
	public static final String KEY_ROWID = "_id";

	/**
	 * 数据库创建语句
	 */
	private static final String CREATE_STUDENT_TABLE =
		"create table " + DATABASE_TABLE + " (" + KEY_ROWID + " integer primary key autoincrement, "
		+ KEY_NAME +" text not null, " + KEY_PATH + " text not null, " + KEY_DATETIME + " text not null);";

	/**
	 * 上下文
	 */
	private final Context mCtx;

	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;

	/**
	 * 内部私有类。数据库的Helper类用于创建和更新数据库。
	 */
	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}
		/**
		 * onCreate方法被调用时，数据库中不存在的第一次。
		 */
		@Override
		public void onCreate(SQLiteDatabase db) {
			Log.i(TAG, "Creating DataBase: " + CREATE_STUDENT_TABLE);
			db.execSQL(CREATE_STUDENT_TABLE);
		}
		/**
		 * ONUPGRADE方法被调用时，数据库版本变化。
		 */
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion);
		}
	}

	/**
	 * 构造 - 使上下文允许打开/创建的数据库
	 *
	 * @param ctx the Context within which to work
	 */
	public DatabaseUtil(Context ctx) {
		this.mCtx = ctx;
	}
	/**
	 * 此方法用于创建/打开连接
	 * @return instance of DatabaseUtil
	 * @throws SQLException
	 */
	public DatabaseUtil open() throws SQLException {
		mDbHelper = new DatabaseHelper(mCtx);
		mDb = mDbHelper.getWritableDatabase();
		return this;
	}
	/**
	 * 此方法用于关闭连接。
	 */
	public void close() {
		mDbHelper.close();
	}

	/**
	 * 插入数据
	 * @param name
	 * @param grade
	 * @return long
	 */
	public long createRecord(String name, String path, String datetime) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_NAME, name);
		initialValues.put(KEY_PATH, path);
		initialValues.put(KEY_DATETIME, datetime);
		return mDb.insert(DATABASE_TABLE, null, initialValues);
	}
	/**
	 * 删除数据
	 * @param rowId
	 * @return boolean
	 */
	public boolean deleteRecord(long rowId) {
		return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
	}

	/**
	 * 查询返回游标来保存数据.
	 * @return Cursor
	 */
	public Cursor fetchAllRecords() {
		return mDb.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME,
				KEY_PATH,KEY_DATETIME}, null, null, null, null, null);
	}

	/**
	 * 返回指定游标，保存数据
	 * @param id
	 * @return Cursor
	 * @throws SQLException
	 */
	public Cursor fetchRecord(long id) throws SQLException {
		Cursor mCursor =
			mDb.query(true, DATABASE_TABLE, new String[] {KEY_ROWID,
					KEY_NAME, KEY_PATH,KEY_DATETIME}, KEY_ROWID + "=" + id, null,
					null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	/**
	 * 更新数据.
	 * @param id
	 * @param name
	 * @param standard
	 * @return boolean
	 */
	public boolean updateRecord(int id, String name, String path, String datetime) {
		ContentValues args = new ContentValues();
		args.put(KEY_NAME, name);
		args.put(KEY_PATH, path);
		args.put(KEY_DATETIME, datetime);
		return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + id, null) > 0;
	}
}
