package bcit.androidgpstracking;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;

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
		ArrayList<String> numbers = new ArrayList<>();
		String message = output.getText().toString();

		//String[] names = {"", ""};
		//numbers = getContactNumber(new ArrayList<String>(Arrays.asList(names)));

		numbers = getContactNumber("");

		if (numbers.isEmpty()) {
			output.setText("Contact list is empty");
		} else {
			output.setText(numbers.get(0));
			//smsManager.sendTextMessage(numbers.get(0), null, message, null, null);
		}

		String test = new String();

		for (int i = 0; i < numbers.size(); i++) {
			output.setText(String.valueOf(i + 1));
			test = test.concat(numbers.get(i));
			//smsManager.sendTextMessage(numbers.get(i), null, message, null, null);
		}
		Toast.makeText(this, test, Toast.LENGTH_LONG).show();
		//output.setText(test);
	}

	public ArrayList<String> getContactNumber(final Collection<String> NameList) {
		//
		//  Find contact based on name.
		//
		ArrayList<String> contactList = new ArrayList<>();
		for (String Name : NameList) {
			ContentResolver cr = getContentResolver();
			Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
					(Name.isEmpty()) ? null : "DISPLAY_NAME = '" + Name + "'", null, null);
			if (cursor.moveToFirst()) {
				String contactId =
						cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
				//
				//  Get all phone numbers.
				//
				Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
						(Name.isEmpty()) ? null : ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
				while (phones.moveToNext()) {
					String number = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
					contactList.add(number);

				/*
				int type = phones.getInt(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
				switch (type) {
					case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
						// do something with the Home number here...
						break;
					case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
						// do something with the Mobile number here...
						break;
					case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
						// do something with the Work number here...
						break;
				}
				*/
				}
				phones.close();
			}
			cursor.close();
		}
		return contactList;
	}

	public ArrayList<String> getContactNumber(final String Name) {
		//
		//  Find contact based on name.
		//
		ContentResolver cr = getContentResolver();
		ArrayList<String> numberList = new ArrayList<>();
		Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
				(Name.isEmpty()) ? null : "DISPLAY_NAME = '" + Name + "'", null, null);
		if (cursor.moveToFirst()) {
			String contactId =
					cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
			//
			//  Get all phone numbers.
			//
			Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
					(Name.isEmpty()) ? null : ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
			while (phones.moveToNext()) {
				String number = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
				numberList.add(number);

				/*
				int type = phones.getInt(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
				switch (type) {
					case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
						// do something with the Home number here...
						break;
					case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
						// do something with the Mobile number here...
						break;
					case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
						// do something with the Work number here...
						break;
				}
				*/
			}
			phones.close();
		}
		cursor.close();
		return numberList;
	}
}
