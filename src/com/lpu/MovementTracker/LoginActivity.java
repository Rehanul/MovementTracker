package com.lpu.MovementTracker;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends Activity {

	private Button buttonLogin;
	private TextView textViewUsername;
	private EditText editTextPassword;
	private TextView textViewInvalidPassword;
	private SharedPreferences sp;

	// TODO onsStart checks if the application is initialised or not
	// if not them we move to SignupActivity
	@Override
	protected void onStart() {
		super.onStart();
		sp = getSharedPreferences("AppData", MODE_PRIVATE);
		String name = sp.getString("username", null);
		String pass = sp.getString("password", null);
		if (name == null || pass == null) {
			Log.d("log", "No User Exist");
			Intent i = new Intent(LoginActivity.this, SignupActivity.class);
			startActivity(i);
			finish();
		} else {
			Log.d("log", "User " + name + " Exist");
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		buttonLogin = (Button) findViewById(R.id.buttonLogin);
		textViewUsername = (TextView) findViewById(R.id.textViewUsername);
		editTextPassword = (EditText) findViewById(R.id.editTextPassword);
		textViewInvalidPassword = (TextView) findViewById(R.id.textViewInvalidPassword);
		sp = getSharedPreferences("AppData", MODE_PRIVATE);
		textViewUsername.setText("Welcome " + sp.getString("username", "user"));

		buttonLogin.setOnClickListener(new OnClickListener() {
			// TODO check password
			// if correct move to MenuActivity
			@Override
			public void onClick(View v) {

				String etpassword = editTextPassword.getText().toString();
				String savedpassword = sp.getString("password", "");
				if (etpassword.equals(savedpassword)) {
					Intent i = new Intent(LoginActivity.this,
							MenuActivity.class);
					startActivity(i);
					finish();
				} else {
					textViewInvalidPassword.setVisibility(View.VISIBLE);
					editTextPassword.setBackgroundColor(Color
							.rgb(255, 160, 160));
				}
			}
		});
		editTextPassword.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				textViewInvalidPassword.setVisibility(View.INVISIBLE);
				editTextPassword.setBackgroundColor(Color.WHITE);
				return false;
			}
		});
	}
}
