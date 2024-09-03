package com.lpu.MovementTracker;

import java.util.ArrayList;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class ShowPathActivity extends Activity {
	private GoogleMap map;
	double Latitude, Longitude;
	Button buttonClear;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.showpathlayout);
		DatabaseHandler db = new DatabaseHandler(this);
		buttonClear = (Button) findViewById(R.id.buttonClear);
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
				.getMap();
		ArrayList<Status> statusList = new ArrayList<Status>();
		double minlat, maxlat, minlng, maxlng;
		// minlat = maxlat = statusList.get(0).getLatitude();
		// minlng = maxlng = statusList.get(0).getLongitude();

		// adding boundary on map
		SharedPreferences sp = getSharedPreferences("AppData", MODE_PRIVATE);
		int Range = sp.getInt("range", 500);
		double lat, lng;
		lat = Double.parseDouble(sp.getString("centerlatitude", "0"));
		lng = Double.parseDouble(sp.getString("centerlongitude", "0"));
		LatLng CenterPoint = new LatLng(lat, lng);
		CircleOptions circleOptions = new CircleOptions().center(CenterPoint)
				.radius(Range);
		map.addCircle(circleOptions);

		statusList = db.getAllStatus();
		if (statusList != null) {
			LatLng center = new LatLng(statusList.get(0).getLatitude(),
					statusList.get(0).getLongitude());
			CameraUpdate update = CameraUpdateFactory.newLatLngZoom(center, 14);
			map.animateCamera(update);
			for (int i = 0; i < statusList.size(); i++) {
				Latitude = statusList.get(i).getLatitude();
				Longitude = statusList.get(i).getLongitude();
				/*
				 * if (Latitude < minlat) minlat = Latitude; if (Longitude <
				 * minlng) minlng = Longitude; if (Longitude > maxlat) maxlng =
				 * Latitude; if (Longitude < maxlng) maxlng = Longitude;
				 */

				map.addMarker(new MarkerOptions()
						.position(new LatLng(Latitude, Longitude))
						.title("User Location")
						.snippet(
								statusList.get(i).getTime() + " Status: "
										+ statusList.get(i).getStatus()));
			}// end of for
		} else {
			CameraUpdate update = CameraUpdateFactory.newLatLngZoom(CenterPoint, sp.getFloat("zoom", 14));
			map.animateCamera(update);
			Toast.makeText(ShowPathActivity.this, "No previous data found",
					Toast.LENGTH_LONG).show();
		}

		buttonClear.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				DatabaseHandler db = new DatabaseHandler(ShowPathActivity.this);
				db.createNewTableStatus();
				Toast.makeText(ShowPathActivity.this, "Data cleared",
						Toast.LENGTH_LONG).show();
			}
		});
	}
	@Override
	protected void onPause() {
		super.onPause();
		finish();
	}
}
