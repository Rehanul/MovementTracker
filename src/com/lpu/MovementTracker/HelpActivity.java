package com.lpu.MovementTracker;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class HelpActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.helplayout);
	}

	@Override
	protected void onPause() {
		super.onPause();
		finish();
	}
}
