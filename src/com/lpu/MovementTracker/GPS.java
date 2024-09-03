package com.lpu.MovementTracker;

import com.google.android.gms.maps.model.LatLng;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

public class GPS implements LocationListener, Runnable {
	private String provider;
	private LocationManager locationManager;
	private double Latitude;
	private double Longitude;
	private Context context;

	// 31.253603 75.703669
	GPS(Context con) {
		this.context = con;
		LocationManager service = (LocationManager) context
				.getSystemService(context.LOCATION_SERVICE);
		// Check if GPS is enabled and if not
		// display a dialog
		// go to the settings
		boolean enabled = service
				.isProviderEnabled(LocationManager.GPS_PROVIDER);

		if (!enabled) {
			// add alert dialog
			Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			context.startActivity(intent);
		}

		// Get the location manager
		locationManager = (LocationManager) context
				.getSystemService(context.LOCATION_SERVICE);
		// criteria to select the location provider
		Criteria criteria = new Criteria();
		provider = locationManager.getBestProvider(criteria, false);
		Location location = locationManager.getLastKnownLocation(provider);

		// Initialize the location if GPS has location update
		// of less than 2 minute
		if (location != null) {
			if ((System.currentTimeMillis() - location.getTime()) > (2 * 60 * 1000)) {
				Log.d("log",
						"last known location time : "
								+ System.currentTimeMillis()
								+ " - "
								+ location.getTime()
								+ " = "
								+ (System.currentTimeMillis() - location
										.getTime()));
				onLocationChanged(location);
			}
		}
	}

	public void start() {
		locationManager.requestLocationUpdates(provider, 400, 1, this);
	}

	public void stop() {
		locationManager.removeUpdates(this);
	}

	@Override
	public void onLocationChanged(Location location) {
		Latitude = location.getLatitude();
		Longitude = location.getLongitude();
		Log.d("log", "Location changed : " + Latitude + ", " + Longitude);
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onProviderDisabled(String provider) {
		Latitude = 0.0;
		Longitude = 0.0;
		// send sms when gps turned off
	}

	public LatLng getCurrentLocation() {
		return new LatLng(Latitude, Longitude);
	}

	@Override
	public void run() {

	}

	public double getLatitude() {
		return Latitude;
	}

	public double getLongitude() {
		return Longitude;
	}
}
