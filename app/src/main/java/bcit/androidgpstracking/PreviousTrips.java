package bcit.androidgpstracking;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.app.ListActivity;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by frank on 01/12/2016.
 */

public class PreviousTrips extends ListActivity {

    SQLiteDatabaseHelper db;
    private List<String> trips;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_previous_trips);

        db = new SQLiteDatabaseHelper(this);
        trips = new ArrayList<String>();

        getAllTrips();

        ArrayAdapter<String> listAdapter = new ArrayAdapter<>(this, R.layout.row_layout, R.id.listText, trips);
        setListAdapter(listAdapter);

    }

    public void getAllTrips(){
        Cursor cursor = db.getAllTripNames();
        if(cursor.getCount() == 0){
            Log.d(TAG, "No data found in db");
        }else{
            StringBuffer tripBuffer = new StringBuffer();
            while(cursor.moveToNext()){
                tripBuffer.append(cursor.getString(0));
                trips.add(tripBuffer.toString());
                tripBuffer.delete(0, tripBuffer.length());
            }
        }
    }


    /**
     * Handles the clicking of a course the user would like to view
     * When user clicks on a course, a new Intent is created
     * the course ID/name and description are added to the intent
     * The Course description activity is then started
     *
     * @param list The List of courses
     * @param view The current view
     * @param position Position in the list of the item that was selected
     * @param id The id of the item selected
     */
    @Override
    protected void onListItemClick(final ListView list, View view, final int position, long id)
    {
        super.onListItemClick(list, view, position, id);

        // Gets a string of the item clicked
        final String selectedItem = (String) getListView().getItemAtPosition(position);

        //Creates a new intent when the course is clicked
        Intent intent = new Intent(PreviousTrips.this, TripView.class);
        intent.putExtra("tripName", selectedItem);

        if(intent != null) {
            startActivity(intent);
        }
    }
}
