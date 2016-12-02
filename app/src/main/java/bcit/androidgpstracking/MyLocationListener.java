package bcit.androidgpstracking;

import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.content.ContentValues.TAG;

/*---------- Listener class to get coordinates ------------- */
public class MyLocationListener implements LocationListener {

	Context context;
	DecimalFormat df;
	String formattedLongitude;
	String formattedLatitude;
	double rawLongitude;
	double rawLatidude;
	boolean lastLocationAccurate = false;
	String formattedOutput = new String();
	String tripID;
	SQLiteDatabaseHelper dbh;

	public MyLocationListener(final Context context, final String tripID, final SQLiteDatabaseHelper dbh) {
		this.context = context;
		this.tripID = tripID;
		this.dbh = dbh;
	}

	public boolean getLocationAccurate() {
		return lastLocationAccurate;
	}

	public String getLastLocationTextMessage() {
		return formattedOutput;
	}

	@Override
	public void onLocationChanged(Location loc) {
		if (loc == null) {
			//Toast.makeText(output.getContext(), "GPS returned null", Toast.LENGTH_SHORT).show();
			return;
		}
		lastLocationAccurate = loc.getAccuracy() < 50;
		df = new DecimalFormat(("#.#####"));
		Log.d(TAG, "TEST");
		//output.setText("");
//		Toast.makeText(
//				output.getContext(),
//				"Location changed: Lat: " + loc.getLatitude() + " Lng: "
//						+ loc.getLongitude() + "\n" + String.valueOf(loc.getAccuracy()), Toast.LENGTH_SHORT).show();

		//JF: Nov 13, 2016
		//The raw double longitude and latitude variables.
		//Can use for getters/setters if need be.
		//If never needed, can be removed from code
		rawLongitude =  loc.getLongitude();
		Log.v(TAG, "" + rawLongitude);
		rawLatidude = loc.getLatitude();
		Log.v(TAG, "" + rawLatidude);

		/*JF: Nov 13, 2016
		Extracts the raw longitude and latitude doubles
		Formats the long/lat as Strings with the correct Compass direction*/
		formattedLatitude = String.valueOf(df.format(Math.abs(loc.getLatitude())));
		formattedLongitude = String.valueOf(df.format(Math.abs(loc.getLongitude())));

		if(loc.getLatitude() < 0) {
			formattedLatitude = formattedLatitude + "S";
		} else {
			formattedLatitude = formattedLatitude + "N";
		}
		if(loc.getLongitude() <= 0) {
			formattedLongitude = formattedLongitude + "W";
		} else {
			formattedLongitude = formattedLongitude + "E";
		}

        /*------- To get city name from coordinates -------- */
		String cityName = null;
		Geocoder gcd = new Geocoder(context, Locale.getDefault());
		List<Address> addresses;
		try {
			addresses = gcd.getFromLocation(loc.getLatitude(),
					loc.getLongitude(), 1);
			if (addresses.size() > 0) {
				System.out.println(addresses.get(0).getLocality());
				cityName = addresses.get(0).getLocality();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		//JF: Nov 13, 2016
		//String now contains google maps query that functions with
		//Users browser and google maps App
		String s = 	"Longitude: " + formattedLongitude + " \n" +
					"Latitude: " + formattedLatitude +
					" \n\nMy Current City is: " + cityName +
					"\nhttp://maps.google.ca/maps/place/?q=" +
				formattedLatitude + "," + formattedLongitude;

		formattedOutput = s;


		Cursor prev = dbh.getWritableDatabase().query(SQLiteDatabaseHelper.TABLE_NAME,
				new String[]{SQLiteDatabaseHelper.COL5, SQLiteDatabaseHelper.COL6, SQLiteDatabaseHelper.COL7,
						SQLiteDatabaseHelper.COL8, SQLiteDatabaseHelper.COL9, SQLiteDatabaseHelper.COL10},
				SQLiteDatabaseHelper.COL1 + " = " + tripID, null, null, null, null);

//		prev.moveToFirst();

		SQLiteDatabase db = dbh.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(SQLiteDatabaseHelper.COL1, tripID);
		values.put(SQLiteDatabaseHelper.COL2, formattedLatitude);
		values.put(SQLiteDatabaseHelper.COL3, formattedLongitude);

		//get current date
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String date = sdf.format(new Date());
		values.put(SQLiteDatabaseHelper.COL4, date);

		values.put(SQLiteDatabaseHelper.COL5, prev.getString(0));    //start
		values.put(SQLiteDatabaseHelper.COL6, prev.getString(1));      //date
		values.put(SQLiteDatabaseHelper.COL7, prev.getString(2));
		values.put(SQLiteDatabaseHelper.COL8, prev.getString(3));
		values.put(SQLiteDatabaseHelper.COL9, prev.getString(4));
		values.put(SQLiteDatabaseHelper.COL10, prev.getString(5));
		long insertId = db.insert(SQLiteDatabaseHelper.TABLE_NAME, null, values);

//		dbh.insertData(Integer.parseInt(tripID), String.valueOf(rawLatidude), String.valueOf(rawLongitude), prev.getString(0),
//				prev.getString(1), prev.getString(2), prev.getString(3), prev.getString(4), prev.getString(5));

		//output.setText(s);
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}
}
