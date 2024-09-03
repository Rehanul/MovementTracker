package com.lpu.MovementTracker;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "database.db";
	private static final String TABLE_NAME_ACCELEROMETER = "mean";
	private static final String TABLE_NAME_MONITORING = "monitoring";
	private static final String KEY_MEAN = "mean";
	private static final String KEY_LAT = "lat";
	private static final String KEY_LNG = "lng";
	private static final String KEY_TIME = "time";
	private static final String KEY_STATUS = "status";

	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_TABLE_ACCELEROMETER = "CREATE TABLE "
				+ TABLE_NAME_ACCELEROMETER + "(" + KEY_MEAN + " INTEGER " + ")";
		db.execSQL(CREATE_TABLE_ACCELEROMETER);
		String CREATE_TABLE_MONITORING = "CREATE TABLE "
				+ TABLE_NAME_MONITORING + "(" + KEY_LAT + " TEXT , " + KEY_LNG
				+ " TEXT , " + KEY_TIME + " TEXT , " + KEY_STATUS + " TEXT "
				+ ")";
		db.execSQL(CREATE_TABLE_MONITORING);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_ACCELEROMETER);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_MONITORING);
		onCreate(db);
	}

	public void createNewTableAccelerometer() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_ACCELEROMETER);
		String CREATE_TABLE_ACCELEROMETER = "CREATE TABLE "
				+ TABLE_NAME_ACCELEROMETER + "(" + KEY_MEAN + " INTEGER " + ")";
		db.execSQL(CREATE_TABLE_ACCELEROMETER);
	}

	public void createNewTableStatus() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_MONITORING);
		String CREATE_TABLE_MONITORING = "CREATE TABLE "
				+ TABLE_NAME_MONITORING + "(" + KEY_LAT + " TEXT , " + KEY_LNG
				+ " TEXT , " + KEY_TIME + " TEXT , " + KEY_STATUS + " TEXT "
				+ ")";
		db.execSQL(CREATE_TABLE_MONITORING);
	}

	public void addMean(float mean) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_MEAN, mean);
		db.insert(TABLE_NAME_ACCELEROMETER, null, values);
		db.close();
	}

	public ArrayList<Float> getAllMean() {
		ArrayList<Float> meanList = new ArrayList<Float>();
		String selectQuery = "SELECT  * FROM " + TABLE_NAME_ACCELEROMETER;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				meanList.add(Float.parseFloat(cursor.getString(0)));
			} while (cursor.moveToNext());
			return meanList;
		}
		return null;
	}

	public float getDeviation() {
		String selectQuery = "SELECT  * FROM " + TABLE_NAME_ACCELEROMETER;
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		double deviation = 0;
		float mean = 0;
		int size = cursor.getCount();
		if (cursor.moveToFirst()) {
			do {
				mean = mean + Float.parseFloat(cursor.getString(0));
			} while (cursor.moveToNext());
			mean = mean / size;
			cursor.moveToFirst();
			do {
				deviation = deviation
						+ Math.pow(
								Float.parseFloat(cursor.getString(0)) - mean, 2);
			} while (cursor.moveToNext());
			deviation = deviation / (size - 1);
			deviation = Math.sqrt(deviation);

			db.close();
			createNewTableAccelerometer();
			return (float) deviation;
		}
		return 0;
	}

	public void addStatus(double lat, double lng, String time, String status) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_LAT, "" + lat);
		values.put(KEY_LNG, "" + lng);
		values.put(KEY_TIME, time);
		values.put(KEY_STATUS, status);
		db.insert(TABLE_NAME_MONITORING, null, values);
		db.close();
	}

	public void addStatus(Status status) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_LAT, "" + status.getLatitude());
		values.put(KEY_LNG, "" + status.getLongitude());
		values.put(KEY_TIME, status.getTime());
		values.put(KEY_STATUS, status.getStatus());
		db.insert(TABLE_NAME_MONITORING, null, values);
		db.close();
	}

	public ArrayList<Status> getAllStatus() {
		ArrayList<Status> statusList = new ArrayList<Status>();
		String selectQuery = "SELECT  * FROM " + TABLE_NAME_MONITORING;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				Status s = new Status();
				s.setLatitude(Double.parseDouble(cursor.getString(0)));
				s.setLongitude(Double.parseDouble(cursor.getString(1)));
				s.setTime(cursor.getString(2));
				s.setStatus(cursor.getString(3));
				statusList.add(s);
			} while (cursor.moveToNext());
			return statusList;
		}
		return null;
	}
}
