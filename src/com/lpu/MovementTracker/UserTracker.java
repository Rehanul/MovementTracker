package com.lpu.MovementTracker;

import java.util.Calendar;

import com.google.android.gms.maps.model.LatLng;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.media.MediaPlayer;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

public class UserTracker implements Runnable {

	private Context context;
	private Accelerometer accelerometer = null;
	private float previous;
	private GPS gps = null;
	private boolean run = true;
	private DatabaseHandler db;
	private MediaPlayer alarm;
	private double centerLatitude;
	private double centerLongitude;
	private int Range;
	private float reqDeviation;
	private int time = 0;
	private String contactNumber;
	private boolean location;
	private boolean motion;
	private float deviation = -1;
	private boolean smsgps = false;
	private boolean smsboundary = false;
	private SharedPreferences sp;

	UserTracker(Context con) {
		this.context = con;
		db = new DatabaseHandler(context);

		accelerometer = new Accelerometer(context);
		alarm = MediaPlayer.create(context, R.raw.gta);
		gps = new GPS(context);
		sp = context.getSharedPreferences("AppData", context.MODE_PRIVATE);
	}

	private void getValues() {
		reqDeviation = (float) ((sp.getInt("deviation", 40) / 10.0) + 2);
		// Log.d("log", "deviation : " + reqDeviation);
		location = sp.getBoolean("location", true);
		motion = sp.getBoolean("motion", true);
		centerLatitude = Double
				.parseDouble(sp.getString("centerlatitude", "0"));
		centerLongitude = Double.parseDouble(sp.getString("centerlongitude",
				"0"));
		Range = sp.getInt("range", 500);
		contactNumber = sp.getString("contactnumber", "0");
	}

	public void start() {
		getValues();
		if (motion)
			accelerometer.start();
		if (location)
			gps.start();

	}

	public void stop() {
		if (motion)
			accelerometer.stop();
		if (location)
			gps.stop();
	}

	@Override
	public void run() {
		while (run) {
			try {
				Thread.sleep(3000);
				time += 3;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			// if motion tracker is on
			if (motion) {
				// check if the user is running
				accelerometer.stop();
				deviation = db.getDeviation();
				accelerometer.start();
				Log.d("log", "Deviation : " + deviation);

				/*
				 * if (previous > 4 && deviation < 4) alarm.stop(); if
				 * (deviation > 4 && previous < 4) alarm.start();
				 */

				if (previous > reqDeviation) {
					alarm.release();
				}
				if (deviation > reqDeviation) {
					alarm = MediaPlayer.create(context, R.raw.gta);
					alarm.start();
				}
				previous = deviation;

				if (!run) {
					accelerometer.stop();
					if (previous > reqDeviation)
						alarm.release();
				}
			}

			// if location tracker is on
			if (location) {
				// if gps is working then
				LatLng current = gps.getCurrentLocation();
				if (current.latitude != 0.0 && current.longitude != 0.0) {
					// check if the user is inside the boundary
					smsgps = false;
					// Log.d("log", current.toString());
					float results[] = new float[2];
					Location.distanceBetween(current.latitude,
							current.longitude, centerLatitude, centerLongitude,
							results);
					if (results[0] > Range) {
						Log.d("log", "out of boundary");
						if (!smsboundary) {
							 Toast.makeText(context, "out of boundary",
							 Toast.LENGTH_LONG).show();
							Log.d("log", "sending message : out of boundary");
							smsboundary = true;
							 SmsManager sms = SmsManager.getDefault();
							 sms.sendTextMessage("+91" + contactNumber, null,
							 "Out of Boundary", null, null);
						}
					} else {
						smsboundary = false;
					}

					// save data every -- minutes
					// if (time > 3 * 20 * 1)// 1 minute
					if (time > 3 * 5) // 15 seconds
					{
						String status;
						current = gps.getCurrentLocation();
						if (deviation == -1) {
							status = "Unknown";
						} else if (deviation > 4)
							status = "Running";
						else
							status = "Walking";

						Calendar ci = Calendar.getInstance();
						int hour = ci.get(Calendar.HOUR);

						String Date = "Time: " + ci.get(Calendar.DAY_OF_MONTH)
								+ "/" + (ci.get(Calendar.MONTH) + 1) + "/"
								+ ci.get(Calendar.YEAR) + " ";
						if (hour > 12) {
							hour = hour - 12;
							Date = Date + hour + ":" + ci.get(Calendar.MINUTE)
									+ ":" + ci.get(Calendar.SECOND) + "PM";
						} else {
							Date = Date + hour + ":" + ci.get(Calendar.MINUTE)
									+ ":" + ci.get(Calendar.SECOND) + "AM";
						}
						Log.d("log", Date + " status:" + status);
						db.addStatus(current.latitude, current.longitude, Date,
								status);
						time = 0;
					}// end of save data every -- minutes
				}// end of if gps is working
				else {
					Log.d("log", "gps not working");
					if (!smsgps) {
						 Toast.makeText(context, "GPS Turned off",
						 Toast.LENGTH_LONG).show();
						Log.d("log", "sending message : gps not working");
						smsgps = true;
						 SmsManager sms = SmsManager.getDefault();
						 sms.sendTextMessage("+91" + contactNumber, null,
						 "GPS Turned off", null, null);
					}
				}
			}// end of if location tracker is on
		}// end of while run
	}

	public boolean isRunning() {
		return run;
	}

	public void setRunning(boolean run) {
		this.run = run;
	}

}
