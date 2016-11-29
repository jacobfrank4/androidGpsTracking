package bcit.androidgpstracking;


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
import java.util.List;
import java.util.Locale;

import static android.content.ContentValues.TAG;

/*---------- Listener class to get coordinates ------------- */
public class MyLocationListener implements LocationListener {

	TextView output;
	DecimalFormat df;
	String formattedLongitude;
	String formattedLatitude;
	double rawLongitude;
	double rawLatidude;

	public MyLocationListener() {
	}

	public MyLocationListener(TextView output) {
		this.output = output;
	}

	@Override
	public void onLocationChanged(Location loc) {
		if (loc == null) {
			Toast.makeText(output.getContext(), "GPS returned null", Toast.LENGTH_SHORT).show();
			return;
		}
		df = new DecimalFormat(("#.#####"));
		Log.d(TAG, "TEST");
		output.setText("");
		Toast.makeText(
				output.getContext(),
				"Location changed: Lat: " + loc.getLatitude() + " Lng: "
						+ loc.getLongitude(), Toast.LENGTH_SHORT).show();

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
		Geocoder gcd = new Geocoder(output.getContext(), Locale.getDefault());
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

		output.setText(s);
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
