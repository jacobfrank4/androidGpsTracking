package bcit.androidgpstracking;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


public class PlanTrip extends AppCompatActivity {

    SQLiteDatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_trip);

        db = new SQLiteDatabaseHelper(this);

    }
}
