package bcit.androidgpstracking;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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
				startActivity(intent);
				break;
			case R.id.previousTrips:
				intent = new Intent(this, PreviousTrips.class);
				startActivity(intent);
				break;
			case R.id.tripView:
				intent = new Intent(this, TripView.class);
				startActivity(intent);
				break;
		}
	}


	public void getGPS(final View view) {
		final MyLocationListener locationListener = new MyLocationListener(output);
		final SmsManager smsManager = SmsManager.getDefault();
		if (!timerActive) {
			//Request single gps update every 5 seconds
			tim.scheduleAtFixedRate(new TimerTask() {
				@Override
				public void run() {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locationListener);
							if (locationListener.getLocationAccurate()) {
								locationManager.removeUpdates(locationListener);
								Toast.makeText(output.getContext(), "Removing updates", Toast.LENGTH_SHORT).show();
							}
							//Change where clause to current trip id instead of -1
							Cursor contacts = db.getReadableDatabase().query(SQLiteDatabaseHelper.TABLE_NAME,
									new String[]{SQLiteDatabaseHelper.COL9}, SQLiteDatabaseHelper.COL1 + " = -1", null, null, null, null);

							if (contacts.moveToFirst()) {
								List<String> numberList = Arrays.asList(contacts.getString(0).split("\\s*,\\s*"));
								for (String number : numberList) {
									smsManager.sendTextMessage(number, null, locationListener.getLastLocationTextMessage(), null, null);
								}
							}
						}
					});
				}
			}, 0, 1000 * 20);
			timerActive = true;
		}
		//parseForDB();
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

    public void viewAllTrips(){
        Cursor cursor = db.getAllTripNames();
        if(cursor.getCount() == 0){
            Log.d(TAG, "No data found in db");
        }else{
            StringBuffer buffer = new StringBuffer();
            while(cursor.moveToNext()){
                buffer.append("Trip Name: " + cursor.getString(0) + "\n");
            }

            showMessage("Data", buffer.toString());
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
