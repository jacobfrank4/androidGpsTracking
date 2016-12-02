package bcit.androidgpstracking;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class TripView extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    SQLiteDatabaseHelper db;
    String trip;
    TableLayout tl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_view);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        db = new SQLiteDatabaseHelper(this);
        tl = (TableLayout) findViewById(R.id.gpsDataTable);

        Intent intent = getIntent();
        trip = intent.getStringExtra("tripName");

        addData();
    }


    public void addData(){
        Cursor cursor = db.getTripData(trip);
        if(cursor.getCount() == 0){
            Log.d(TAG, "No data found in db");
        }else{
            StringBuffer latlongbuff = new StringBuffer();
            StringBuffer timebuff = new StringBuffer();

            while(cursor.moveToNext()){

                TableRow row = new TableRow(this);
                TextView latlong = new TextView(this);
                TextView time = new TextView(this);

                TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                row.setLayoutParams(lp);

                timebuff.append(cursor.getString(4)); //date/time
                latlongbuff.append(cursor.getString(2)); //latitude
                latlongbuff.append("; "); //latitude
                latlongbuff.append(cursor.getString(3)); //longitude

                latlong.setText(latlongbuff);
                time.setText(timebuff);

                row.addView(latlong);
                row.addView(time);

                tl.addView(row);
                latlongbuff.delete(0, latlongbuff.length());
                timebuff.delete(0, timebuff.length());
            }
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Cursor cursor = db.getTripData(trip);
        List<Double> latArray;
        List<Double> longArray;
        latArray = new ArrayList<Double>();
        longArray = new ArrayList<Double>();
        double longitudeAverage = 0;
        double latitudeAverage = 0;

        if(cursor.getCount() == 0){
            Log.d(TAG, "No data found in db");
        }else{
            StringBuffer namebuff = new StringBuffer();
            StringBuffer latbuff = new StringBuffer();
            StringBuffer longbuff = new StringBuffer();
            String lat;
            String lon;
            double latitude;
            double longitude;

            while(cursor.moveToNext()){

                namebuff.append(cursor.getString(10)); //trip name
                latbuff.append(cursor.getString(2)); //latitude
                longbuff.append(cursor.getString(3)); //longitude

                lat =  latbuff.toString();
                lon =  longbuff.toString();

                if(lat.charAt(lat.length()-1) == 'S') {
                    lat = lat.substring(0, lat.length()-1);
                    latitude = -Double.parseDouble(lat);

                } else {
                    lat = lat.substring(0, lat.length()-1);
                    latitude = Double.parseDouble(lat);
                }

                if(lon.charAt(lon.length()-1) == 'W') {
                    lon = lon.substring(0, lon.length()-1);
                    longitude = -Double.parseDouble(lon);

                } else {
                    lon = lon.substring(0, lon.length()-1);
                    longitude = Double.parseDouble(lon);
                }

                latArray.add(latitude);
                longArray.add(longitude);


                // Add a trip marker
                LatLng tripMarker = new LatLng(latitude,longitude);
                mMap.addMarker(new MarkerOptions().position(tripMarker).title(namebuff.toString()));

                namebuff.delete(0, namebuff.length());
                latbuff.delete(0, latbuff.length());
                longbuff.delete(0, longbuff.length());
            }
            for(int i = 0; i < latArray.size(); i++) {
                latitudeAverage += latArray.get(i);
            }
            latitudeAverage = (latitudeAverage/latArray.size());
            for(int i = 0; i < longArray.size(); i++) {
                longitudeAverage += longArray.get(i);
            }
            longitudeAverage = (longitudeAverage/longArray.size());


            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitudeAverage, longitudeAverage), 8.0f));
        }
    }
}
