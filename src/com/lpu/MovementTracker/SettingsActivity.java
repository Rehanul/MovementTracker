package com.lpu.MovementTracker;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends Activity {

	private Button buttonDone;
	private EditText editTextUsername;
	private EditText editTextPassword;
	private EditText editTextContactNumber;
	private EditText editTextEmailID;
	private CheckBox checkBoxLocation;
	private CheckBox checkBoxMotion;
	private CheckBox checkBoxSms;
	private CheckBox checkBoxEmail;
	private ScrollView firstLayer;
	private TextView textViewRequired;
	private ScrollView secondLayer;
	private Button buttonVerify;
	private EditText editTextVerify;
	private Button buttonResend;
	private TextView textViewVreify;
	private Button buttonBack;
	private SeekBar seekBarDeviation;
	private int verificationCode;
	private String username;
	private String password;
	private String contactNumber;
	private String emailID;
	SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settingslayout);
		textViewRequired = (TextView) findViewById(R.id.textViewRequired);
		buttonDone = (Button) findViewById(R.id.buttonDone);
		editTextUsername = (EditText) findViewById(R.id.editTextUsername);
		editTextPassword = (EditText) findViewById(R.id.editTextPassword);
		editTextContactNumber = (EditText) findViewById(R.id.editTextContactNumber);
		editTextEmailID = (EditText) findViewById(R.id.editTextEmailID);
		buttonVerify = (Button) findViewById(R.id.buttonVerify);
		editTextVerify = (EditText) findViewById(R.id.editTextVerify);
		buttonResend = (Button) findViewById(R.id.buttonResend);
		firstLayer = (ScrollView) findViewById(R.id.layoutLayerSignup);
		secondLayer = (ScrollView) findViewById(R.id.layoutLayer2);
		textViewVreify = (TextView) findViewById(R.id.textViewVerify);
		buttonBack = (Button) findViewById(R.id.buttonBack);
		checkBoxLocation = (CheckBox) findViewById(R.id.checkBoxLocation);
		checkBoxMotion = (CheckBox) findViewById(R.id.checkBoxMotion);
		checkBoxSms = (CheckBox) findViewById(R.id.checkBoxSms);
		checkBoxEmail = (CheckBox) findViewById(R.id.checkBoxEmail);
		seekBarDeviation = (SeekBar) findViewById(R.id.seekBarDeviation);

		sp = getSharedPreferences("AppData", MODE_PRIVATE);
		editTextUsername.setText(sp.getString("username", ""));
		editTextPassword.setText(sp.getString("password", ""));
		editTextContactNumber.setText(sp.getString("contactnumber", ""));
		editTextEmailID.setText(sp.getString("emailid", ""));
		checkBoxLocation.setChecked(sp.getBoolean("location", true));
		checkBoxMotion.setChecked(sp.getBoolean("motion", true));
		checkBoxSms.setChecked(sp.getBoolean("sms", true));
		checkBoxEmail.setChecked(sp.getBoolean("email", true));
		seekBarDeviation.setProgress(sp.getInt("deviation", 40));

		buttonDone.setOnClickListener(new OnClickListener() {
			// TODO vadidate given data then verify contact number
			@Override
			public void onClick(View v) {
				username = editTextUsername.getText().toString();
				password = editTextPassword.getText().toString();
				contactNumber = editTextContactNumber.getText().toString();
				emailID = editTextEmailID.getText().toString();

				// check for valid data
				if (username.equals("")) {
					textViewRequired.setVisibility(View.VISIBLE);
					textViewRequired.setText("*Username Required");
				} else if (password.equals("")) {
					textViewRequired.setVisibility(View.VISIBLE);
					textViewRequired.setText("*Password Required");
				} else if (contactNumber.equals("")) {
					textViewRequired.setVisibility(View.VISIBLE);
					textViewRequired.setText("*Contact Number Required");
				} else if (emailID.equals("")) {
					textViewRequired.setVisibility(View.VISIBLE);
					textViewRequired.setText("*Email-ID Required");
				} else {
					// Details are valid
					// sending verification code if previous no. is changed
					if (!(contactNumber.equals(sp
							.getString("contactnumber", "")))) {
						firstLayer.setVisibility(View.INVISIBLE);
						secondLayer.setVisibility(View.VISIBLE);
						sendSms();
					} else {
						SharedPreferences sp = getSharedPreferences("AppData",
								MODE_PRIVATE);
						SharedPreferences.Editor editor = sp.edit();
						editor.putString("username", username);
						editor.putString("password", password);
						editor.putString("contactnumber", contactNumber);
						editor.putString("emailid", emailID);

						editor.putBoolean("location",
								checkBoxLocation.isChecked());
						editor.putBoolean("motion", checkBoxMotion.isChecked());
						editor.putBoolean("sms", checkBoxSms.isChecked());
						editor.putBoolean("email", checkBoxEmail.isChecked());
						editor.putInt("deviation",
								seekBarDeviation.getProgress());
						editor.commit();

						finish();
					}
				}
			}

		});// end of buttonDone onClick()

		buttonVerify.setOnClickListener(new OnClickListener() {
			// TODO check if verification code is correct then Saving data in
			// shared preferences and going to login page
			@Override
			public void onClick(View v) {
				if ((verificationCode + "").equals(editTextVerify.getText()
						.toString())) {
					SharedPreferences sp = getSharedPreferences("AppData",
							MODE_PRIVATE);
					SharedPreferences.Editor editor = sp.edit();
					editor.putString("username", username);
					editor.putString("password", password);
					editor.putString("contactnumber", contactNumber);
					editor.putString("emailid", emailID);

					editor.putBoolean("location", checkBoxLocation.isChecked());
					editor.putBoolean("motion", checkBoxMotion.isChecked());
					editor.putBoolean("sms", checkBoxSms.isChecked());
					editor.putBoolean("email", checkBoxEmail.isChecked());
					editor.putInt("deviation", seekBarDeviation.getProgress());
					editor.commit();

					finish();
				} else {
					Toast.makeText(SettingsActivity.this, "Incorrect Code",
							Toast.LENGTH_SHORT).show();
				}
			}
		});// end of buttonVerify onClick()
		buttonResend.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sendSms();
			}
		});// end of buttonResend onClick()
		buttonBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				firstLayer.setVisibility(View.VISIBLE);
				secondLayer.setVisibility(View.INVISIBLE);
			}
		});
	}

	private void sendSms() {
		// sending verification code to cellphone
		verificationCode = (int) (Math.random() * 10000);
		SmsManager sms = SmsManager.getDefault();
		textViewVreify
				.setText("A text message with your code has been sent to: "
						+ contactNumber);
		sms.sendTextMessage("+91" + contactNumber, null,
				"Your Movement Tracker verification code is "
						+ verificationCode, null, null);
		Log.d("log", "Verification code: " + verificationCode);
	}

	@Override
	protected void onPause() {
		super.onPause();
		finish();
	}
}
