package com.lpu.MovementTracker;

import java.util.Calendar;

import com.google.android.gms.maps.GoogleMap;

import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class DefineRegionActivity extends Activity {

	private GoogleMap map;
	private LatLng CenterPoint;
	private Marker marker;
	private Circle circle;
	private EditText editTextRange;
	private Button buttonSetRange;
	private Button buttonDone;
	private int Range = 500;
	SharedPreferences sp;
	float zoom;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.defineregionlayout);
		editTextRange = (EditText) findViewById(R.id.editTextRange);
		buttonSetRange = (Button) findViewById(R.id.buttonSetRange);
		buttonDone = (Button) findViewById(R.id.buttonDone);
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
				.getMap();

		// check if GPS is on or not
		LocationManager locManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		boolean enabled = locManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);
		if (!enabled) {
			enableGPS();
		}
		map.setMyLocationEnabled(true);
		sp = getSharedPreferences("AppData", MODE_PRIVATE);

		double lat = Double.parseDouble(sp.getString("centerlatitude", "0"));
		double lng = Double.parseDouble(sp.getString("centerlongitude", "0"));
		CenterPoint = new LatLng(lat, lng);

		// adding a marker
		marker = map.addMarker(new MarkerOptions().position(CenterPoint)
				.snippet("Boundary Center")
				.icon(BitmapDescriptorFactory.fromResource(R.drawable.boy)));
		zoom = sp.getFloat("zoom", 14);
		CameraUpdate update = CameraUpdateFactory.newLatLngZoom(CenterPoint,
				zoom);
		map.animateCamera(update);

		// adding circular boundary
		Range = sp.getInt("range", 500);
		CircleOptions circleOptions = new CircleOptions().center(CenterPoint)
				.radius(Range);
		circle = map.addCircle(circleOptions);
		map.setOnMapClickListener(new OnMapClickListener() {

			@Override
			public void onMapClick(LatLng latlng) {
				CenterPoint = latlng;

				// replace marker
				marker.setPosition(latlng);
				circle.setCenter(latlng);
				float zoom = sp.getFloat("zoom", 15);
				CameraUpdate update = CameraUpdateFactory.newLatLngZoom(
						CenterPoint, map.getCameraPosition().zoom);
				map.animateCamera(update);
			}
		});

		buttonSetRange.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!(editTextRange.getText().toString()).equals("")) {
					Range = Integer
							.parseInt(editTextRange.getText().toString());
					// replace circle boundary
					circle.setRadius(Range);
				}
			}
		});

		map.setOnCameraChangeListener(new OnCameraChangeListener() {

			@Override
			public void onCameraChange(CameraPosition cp) {
				zoom = cp.zoom;
			}
		});
		buttonDone.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// save CenterPoint in SharedPrefrences
				SharedPreferences.Editor editor = sp.edit();
				editor.putString("centerlatitude", "" + CenterPoint.latitude);
				editor.putString("centerlongitude", "" + CenterPoint.longitude);
				editor.putInt("range", Range);
				editor.putFloat("zoom", zoom);
				editor.commit();
				finish();
			}
		});
	}

	private void enableGPS() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Enable GPS");
		alert.setMessage("Please turn on your GPS");
		/*alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				Intent intent = new Intent(
						Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				startActivity(intent);
			}
		});*/
		alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialogInterface, int arg1) {
				dialogInterface.cancel();
			}
		});
		alert.setCancelable(false);
		Dialog dialog = alert.create();
		dialog.show();
	}

	@Override
	protected void onPause() {
		super.onPause();

		finish();
		// if at signup the CenterPoint is Unknown
		// then the user has to press done and cannot go back
		if (CenterPoint.latitude == 0.0) {
			Intent i = new Intent(DefineRegionActivity.this,
					DefineRegionActivity.class);
			startActivity(i);
		}
	}
}
