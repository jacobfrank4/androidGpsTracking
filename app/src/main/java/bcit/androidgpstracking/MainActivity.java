package bcit.androidgpstracking;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Timer;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {

	LocationManager locationManager;
	SQLiteDatabaseHelper db;
	TextView output;

	Timer tim = new Timer();
	boolean timerActive = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.activity_main);
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		output = (TextView) findViewById(R.id.textView);
		db = new SQLiteDatabaseHelper(this);
	}

	public void goToActvity(final View view){
		int buttonClicked = view.getId();
		Intent intent;

		switch(buttonClicked) {
			case R.id.planTrip:
				intent = new Intent(this, PlanTrip.class);
				startActivityForResult(intent, 2);
				break;
			case R.id.previousTrips:
				//intent = new Intent(this, PreviousTrips.class);
				//startActivity(intent);
				break;
			case R.id.tripView:
				intent = new Intent(this, TripView.class);
				startActivity(intent);
				break;
		}
	}

	public void getGPS(final View view) {
		tripMessageLoop("1234");
	}

	public void tripMessageLoop(final String tripID) {
		final MyLocationListener locationListener = new MyLocationListener(output);
		final SmsManager smsManager = SmsManager.getDefault();

		final SQLiteDatabase database = db.getReadableDatabase();

		Cursor dateRange = database.query(SQLiteDatabaseHelper.TABLE_NAME,
				new String[]{SQLiteDatabaseHelper.COL5, SQLiteDatabaseHelper.COL6,
						SQLiteDatabaseHelper.COL7, SQLiteDatabaseHelper.COL8},
				SQLiteDatabaseHelper.COL1 + " = " + tripID, null, null, null, null);

		if (dateRange.moveToFirst()) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:MM");
			try {
				Date start = sdf.parse(dateRange.getString(0));
				Date end = sdf.parse(dateRange.getString(1));

				//Remove this if when validation is in place since all trips will have proper start and end times
				long duration;
				if (end.getTime() > start.getTime()) {
					duration = end.getTime() - start.getTime();
				} else {
					duration = start.getTime() - end.getTime();
				}

				long frequencyLength;
				switch (dateRange.getString(dateRange.getColumnIndex(SQLiteDatabaseHelper.COL8))) {
					case "Minutes":
						frequencyLength = 1000 * 60;
						break;
					case "Hours":
						frequencyLength = 1000 * 60 * 60;
						break;
					case "Days":
						frequencyLength = 1000 * 60 * 60 * 24;
						break;
					default:
						frequencyLength = 1000 * 60 * 60;
						break;
				}
				long tickLength = dateRange.getInt(dateRange.getColumnIndex(SQLiteDatabaseHelper.COL7)) * frequencyLength;

				CountDownTimer cdt = new CountDownTimer(duration, tickLength) {
					@Override
					public void onTick(long l) {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locationListener);
								if (locationListener.getLocationAccurate()) {
									locationManager.removeUpdates(locationListener);
								}
								//Change where clause to current trip id instead of -1
								Cursor contacts = database.query(SQLiteDatabaseHelper.TABLE_NAME,
										new String[]{SQLiteDatabaseHelper.COL9}, SQLiteDatabaseHelper.COL1 + " = " + tripID, null, null, null, null);

								if (contacts.moveToFirst()) {
									List<String> numberList = Arrays.asList(contacts.getString(0).split("\\s*,\\s*"));
									for (String number : numberList) {
										if (!locationListener.getLastLocationTextMessage().isEmpty()) {
											smsManager.sendTextMessage(number, null, locationListener.getLastLocationTextMessage(), null, null);
										}
									}
								}
								contacts.close();
							}
						});
					}

					@Override
					public void onFinish() {

					}
				};
				cdt.start();

			} catch (Exception e) {
				//Database contains badly formatted date strings
			}

		} else {
			//Database returned nothing
		}
		dateRange.close();
	}

	/*
	Method for sending a text to a list of recipients that contains the GPS location.
	Future improvements are:
	1) convert method of handling databases into a class for handling texting
	2) pull recipient list from Trip list.
	 */
	public void sendGPSText(final View view) {
//		SmsManager smsManager = SmsManager.getDefault();
//		ArrayList<String> numbers = new ArrayList<>();
//		String message = output.getText().toString();

		//JA - Nov 25 2016 - Calls intent which prompts user to choose contacts and then calls method below to message them
		Intent intent = new Intent(this, contactListActivity.class);
		startActivityForResult(intent, 1);

		/*
		JF: Nov 13, 2016
		To test Sending text message with location data,
		uncomment the below block and add cell phone numebrs to the phoneNumbers array
		ex: phoneNumbers[] = {"1112223333","5556662222"};
		 */
//		String phoneNumbers[] = {};
//		for(int i = 0; i < phoneNumbers.length; i++) {
//			smsManager.sendTextMessage(phoneNumbers[i], null, message, null, null);
//		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1) {
			if (resultCode == Activity.RESULT_OK) {
				SmsManager smsManager = SmsManager.getDefault();
				ArrayList<String> numbers = data.getStringArrayListExtra("numbers");
				String message = output.getText().toString();

				for (String number : numbers) {
					smsManager.sendTextMessage(number, null, message, null, null);
				}
			}
			if (resultCode == Activity.RESULT_CANCELED) {
				//Write your code if there's no result
			}
		} else if (requestCode == 2) {
			if (resultCode == Activity.RESULT_OK) {
				String tripID = String.valueOf(data.getIntExtra("ID", -1));
				Toast.makeText(output.getContext(), "Got trip ID: " + tripID, Toast.LENGTH_SHORT).show();
				tripMessageLoop(tripID);
			}
			if (resultCode == Activity.RESULT_CANCELED) {
				//Write your code if there's no result
			}
		}
	}

	public void parseForDB(){
//TESTING
		String s = 	"Longitude: " + "123.123W" + " \n" +
				"Latitude: " + "57.876N"	 +
				" \n\nMy Current City is: " + "Burnaby" +
				"\nhttp://maps.google.ca/maps/place/?q=" +
				123.123 + "," + 57.876;
		String[] temp = s.split(" ");

//END PRODUCT
//		String message = output.getText().toString();
//		output.setText(message);
//		String[] temp = output.getText().toString().split(" ");

		if(temp.length < 3) {
			Toast.makeText(this, "Not enough data coming from Location Listener: " + output.getText().toString(), Toast.LENGTH_LONG).show();
		} else{
			//if(db.insertData(1, temp[1], temp[3], "", "", "1", "", "", "")){
			if(db.insertData(1, "567.123N", "123.123W", "", "", "1", "", "", "")){
					Toast.makeText(this, "insert success", Toast.LENGTH_LONG).show();
			}else{
				Toast.makeText(this, "insert failed", Toast.LENGTH_LONG).show();
			}
		}
	}

	public void viewAll(final View view){
		Cursor cursor = db.getAllData();
		if(cursor.getCount() == 0){
			Log.d(TAG, "No data found in db");
		}else{
			StringBuffer buffer = new StringBuffer();
			while(cursor.moveToNext()){
				buffer.append("ID: " + cursor.getString(0) + "\n");
				buffer.append("TRIP ID: " + cursor.getString(1) + "\n");
				buffer.append("Trip Name: " + cursor.getString(10) + "\n"); //trip name
				buffer.append("Date/Time: " + cursor.getString(4) + "\n");
				buffer.append("Lat: " + cursor.getString(2) + "\n");
				buffer.append("Long: " + cursor.getString(3) + "\n");
				buffer.append("Start: " + cursor.getString(5) + "\n");
				buffer.append("End: " + cursor.getString(6) + "\n");
				buffer.append("Frequency Number: " + cursor.getString(7) + "\n");
				buffer.append("Frequency Type: " + cursor.getString(8) + "\n");
				buffer.append("Contacts: " + cursor.getString(9) + "\n\n\n");

			}

			showMessage("Data", buffer.toString());
		}
	}

	public void showMessage(String title, String message){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setCancelable(true);
		builder.setTitle(title);
		builder.setMessage(message);
		builder.show();
	}
}
