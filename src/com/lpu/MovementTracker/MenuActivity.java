package com.lpu.MovementTracker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ToggleButton;

public class MenuActivity extends Activity implements OnClickListener {

	private ToggleButton buttonStart;
	private Button buttonShowPath;
	private Button buttonHelp;
	private Button buttonDefineRegion;
	private Button buttonSettings;
	private static UserTracker userTracker;
	private static boolean tbChecked = false;
	private static Thread userTrackerThread;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menulayout);
		buttonStart = (ToggleButton) findViewById(R.id.toggleButton);
		buttonShowPath = (Button) findViewById(R.id.buttonShowPath);
		buttonHelp = (Button) findViewById(R.id.buttonHelp);
		buttonDefineRegion = (Button) findViewById(R.id.buttonDefineRegion);
		buttonSettings = (Button) findViewById(R.id.buttonSettings);
		userTracker = new UserTracker(MenuActivity.this);

		buttonStart.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
				Log.d("log", "Monitoring : " + isChecked);
				tbChecked = isChecked;
				userTracker.setRunning(isChecked);
				if (isChecked) {
					buttonShowPath.setEnabled(false);
					buttonHelp.setEnabled(false);
					buttonDefineRegion.setEnabled(false);
					buttonSettings.setEnabled(false);

					userTracker.start();
					userTrackerThread = new Thread(userTracker);
					userTrackerThread.start();

				} else {
					buttonShowPath.setEnabled(true);
					buttonHelp.setEnabled(true);
					buttonDefineRegion.setEnabled(true);
					buttonSettings.setEnabled(true);

					userTracker.stop();
					userTrackerThread = null;
				}
			}
		});
		buttonShowPath.setOnClickListener(this);
		buttonHelp.setOnClickListener(this);
		buttonDefineRegion.setOnClickListener(this);
		buttonSettings.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		Intent i = null;
		switch (v.getId()) {
		case R.id.buttonShowPath:
			i = new Intent(MenuActivity.this, ShowPathActivity.class);
			break;
		case R.id.buttonHelp:
			i = new Intent(MenuActivity.this, HelpActivity.class);
			break;
		case R.id.buttonDefineRegion:
			i = new Intent(MenuActivity.this, DefineRegionActivity.class);
			break;
		case R.id.buttonSettings:
			i = new Intent(MenuActivity.this, SettingsActivity.class);
			break;
		}
		startActivity(i);
	}
	
	/*@Override
	protected void onRestart() {
		super.onRestart();
		Log.d("log", "MenuActivity restarted");
		Intent i = new Intent(MenuActivity.this, LoginActivity.class);
		startActivity(i);
	}*/

	@Override
	protected void onResume() {
		super.onResume();
		buttonStart.setChecked(tbChecked);
	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.d("log", "MenuActivity stopped");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d("log", "MenuActivity Destroyed");
		userTracker.stop();
		userTrackerThread = null;
	}
}
