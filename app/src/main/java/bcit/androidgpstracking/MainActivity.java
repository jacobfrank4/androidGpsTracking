package bcit.androidgpstracking;

import android.content.Context;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

	LocationManager locationManager;
	DatabaseHelper myDb;

	TextView output;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.activity_main);
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		output = (TextView) findViewById(R.id.textView);
	}

	public void getGPS(final View view) {
		LocationListener locationListener = new MyLocationListener(output);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
	}

	/*
	Method for sending a text to a list of recipients that contains the GPS location.
	Future improvements are:
	1) convert method of handling databases into a class for handling texting
	2) pull recipient list from Trip list.
	 */
	public void sendGPSText(final View view) {
		SmsManager smsManager = SmsManager.getDefault();
		String numbers[] = {};
		String message = output.getText().toString();
		for(int i = 0; i < numbers.length; i++) {
			smsManager.sendTextMessage(numbers[i], null, message, null, null);
		}
	}
}
