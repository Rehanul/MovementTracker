package com.lpu.MovementTracker;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class SignupActivity extends Activity {

	private Button buttonDone;
	private EditText editTextUsername;
	private EditText editTextPassword;
	private EditText editTextContactNumber;
	private EditText editTextEmailID;
	private ScrollView firstLayer;
	private TextView textViewRequired;
	private ScrollView secondLayer;
	private Button buttonVerify;
	private EditText editTextVerify;
	private Button buttonResend;
	private TextView textViewVreify;
	private Button buttonBack;
	private int verificationCode;
	private String username;
	private String password;
	private String contactNumber;
	private String emailID;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.signuplayout);
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
					// sending verification code
					firstLayer.setVisibility(View.INVISIBLE);
					secondLayer.setVisibility(View.VISIBLE);
					sendSms();
				}
			}

		});// end of buttonDone onClick()

		buttonVerify.setOnClickListener(new OnClickListener() {
			// TODO check if verification code is correct then Saving data in
			// shared preferences and going to DefineRegionActivity
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

					editor.putFloat("zoom", 15);
					editor.putInt("range", 500);
					editor.putString("centerlatitude", "0");
					editor.putString("centerlongitude", "0");

					editor.putBoolean("location", true);
					editor.putBoolean("motion", true);
					editor.putBoolean("sms", true);
					editor.putBoolean("email", true);
					editor.putInt("deviation", 40);

					editor.commit();
					Intent i = new Intent(SignupActivity.this,
							MenuActivity.class);
					startActivity(i);
					i = new Intent(SignupActivity.this,
							DefineRegionActivity.class);
					startActivity(i);
					finish();
				} else {
					Toast.makeText(SignupActivity.this, "Incorrect Code",
							Toast.LENGTH_SHORT).show();
				}
			}
		});// end of buttonVerify onClick()
		buttonResend.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
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
		
		  SmsManager sms = SmsManager.getDefault(); textViewVreify
		  .setText("A text message with your code has been sent to: " +
		 contactNumber); sms.sendTextMessage("+91" + contactNumber, null,
		 "Your Movement Tracker verification code is " + verificationCode,
		  null, null);
		 
		textViewVreify.setText(textViewVreify.getText()+"->"+verificationCode);
		Log.d("log", "Verification code: " + verificationCode);
	}
}
