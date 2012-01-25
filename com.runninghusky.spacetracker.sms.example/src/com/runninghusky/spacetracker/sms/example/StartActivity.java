package com.runninghusky.spacetracker.sms.example;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class StartActivity extends Activity {
	private EditText mTo, mMessage;
	private Button mSend;
	private Context ctx = this;
	private BroadcastReceiver mBroadcastReceiver;

	/** Allows the orientation to change without disrupting the activity */
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		mTo = (EditText) findViewById(R.id.EditTextTo);
		mMessage = (EditText) findViewById(R.id.EditTextMessage);
		mSend = (Button) findViewById(R.id.ButtonSend);
		mSend.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (String.valueOf(mTo.getText()).length() > 0
						&& String.valueOf(mMessage.getText()).length() > 0) {
					try {
						sendSMS(String.valueOf(mTo.getText()), String
								.valueOf(mMessage.getText()));
					} catch (Exception e) {
						Toast.makeText(ctx, "Error sending message...",
								Toast.LENGTH_SHORT).show();
					}
				} else {
					Toast.makeText(ctx,
							"Please enter both a phone number and message",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	private void sendSMS(String pn, String m) {
		final String phoneNumber = pn;
		final String message = m;
		String SENT = "SMS_SENT";

		PendingIntent sentPI = PendingIntent.getBroadcast(ctx, 0, new Intent(
				SENT), 0);

		// ---when the SMS has been sent---
		mBroadcastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context arg0, Intent arg1) {
				String msg = "";
				switch (getResultCode()) {
				case Activity.RESULT_OK:
					msg = "SMS sent";
					break;
				case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
					msg = "Generic failure";
					break;
				case SmsManager.RESULT_ERROR_NO_SERVICE:
					msg = "No service";
					break;
				case SmsManager.RESULT_ERROR_NULL_PDU:
					msg = "Null PDU";
					break;
				case SmsManager.RESULT_ERROR_RADIO_OFF:
					msg = "Radio off";
					break;
				}
				Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
			}
		};
		registerReceiver(mBroadcastReceiver, new IntentFilter(SENT));

		SmsManager sms = SmsManager.getDefault();
		Toast.makeText(ctx, "Sending sms message...", Toast.LENGTH_SHORT)
				.show();
		sms.sendTextMessage(phoneNumber, "", message, sentPI, null);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			unregisterReceiver(mBroadcastReceiver);
		} catch (Exception e) {
		}
	}
}