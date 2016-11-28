package bcit.androidgpstracking;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
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
		final LocationListener locationListener = new MyLocationListener(output);

		if (!timerActive) {
			//Request single gps update every 5 seconds
			tim.scheduleAtFixedRate(new TimerTask() {
				@Override
				public void run() {
					Looper.prepare();
					locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, null);
					Looper.loop();
				}
			}, 0, 1000 * 5);
			timerActive = true;
		}
		//locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
		parseForDB();
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
//		String s = 	"Longitude: " + "123.123W" + " \n" +
//				"Latitude: " + "57.876N"	 +
//				" \n\nMy Current City is: " + "Burnaby" +
//				"\nhttp://maps.google.ca/maps/place/?q=" +
//				123.123 + "," + 57.876;
		String message = output.getText().toString();
		output.setText(message);

		String[] temp = output.getText().toString().split(" ");
		if(temp.length < 3) {
			Toast.makeText(this, "Not enough data coming from Location Listener: " + output.getText().toString(), Toast.LENGTH_LONG).show();
		} else{
			if(db.insertData(1, temp[1], temp[3])){
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
				buffer.append("ID: " + cursor.getString(0) + "\n"); //id
				buffer.append("TRIP ID: " + cursor.getString(1) + "\n"); //trip id
				buffer.append("Date/Time: " + cursor.getString(4) + "\n"); //date/time
				buffer.append("Lat: " + cursor.getString(2) + "\n"); //latitude
				buffer.append("Long: " + cursor.getString(3) + "\n\n\n"); //longitude
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
