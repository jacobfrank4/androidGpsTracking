package bcit.androidgpstracking;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Jacob on 23/10/2016.
 */

public class DatabaseHelper  extends SQLiteOpenHelper{
    public static final String DATABASE_NAME = "locationData.db";
    public static final String TABLE_NAME = "location_table";
    public static final String COL_0 = "ID";
    public static final String COL_1 = "TRIP_ID";
    public static final String COL_2 = "LATITUDE";
    public static final String COL_3 = "LONGITUDE";
    public static final String COL_4 = "DATE";
    public static final String COL_5 = "TIME";
    public static final int DATABASE_VERSION = 1;
    public static final String TEXT_TYPE = " TEXT";
    public static final String DOUBLE_TYPE = " DOUBLE PRECISION";
    public static final String DATE_TYPE = " DATE";
    public static final String TIME_TYPE = " TIME";
    public static final String COMMA = ",";

    /**
     * Default Constructor for the database
     * it generates a the locationData database for the context defined
     */
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Creates the location table within the LocationData database
     * @param db The locationData database
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " (" + COL_0 + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                                         COL_1 + TEXT_TYPE + COMMA +
                                                         COL_2 + DOUBLE_TYPE + COMMA +
                                                         COL_3 + DOUBLE_TYPE + COMMA +
                                                         COL_4 + DATE_TYPE + COMMA +
                                                         COL_5 + TIME_TYPE + ")");
    }

    /**
     * Written to discard previous database and start over if called
     * @param db The locationData database
     * @param oldVersion old database version
     * @param newVersion new database version
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    /**
     * Calls onUpgrade to recreate database
     * @param db The locationData database
     * @param oldVersion old database version
     * @param newVersion new database version
     */
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
