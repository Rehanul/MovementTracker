package com.lpu.MovementTracker;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class Accelerometer implements Runnable {

	private SensorManager sensorManager;
	private Sensor accelerometer;
	private SensorEventListener sensorEventListener = new SensorEventListener() {
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}

		@Override
		public void onSensorChanged(SensorEvent event) {
			x = event.values[0];
			y = event.values[1];
			z = event.values[2];
			mean = (float) Math.sqrt(x * x + y * y + z * z);
			db.addMean(mean);
			// Log.d("log", "mean : " + mean);
		}
	};
	private float x = 0;
	private float y = 0;
	private float z = 0;
	private float mean = 0;
	private DatabaseHandler db;

	Accelerometer(Context context) {
		sensorManager = (SensorManager) context
				.getSystemService(context.SENSOR_SERVICE);
		accelerometer = sensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		db = new DatabaseHandler(context);
		sensorManager.unregisterListener(sensorEventListener);
	}

	public void start() {
		// Log.d("log", "Start Accelerometer");
		sensorManager.registerListener(sensorEventListener, accelerometer,
				SensorManager.SENSOR_DELAY_NORMAL);
	}

	public void stop() {
		// Log.d("log", "Stop Accelerometer");
		sensorManager.unregisterListener(sensorEventListener);
	}

	@Override
	public void run() {

	}

}
