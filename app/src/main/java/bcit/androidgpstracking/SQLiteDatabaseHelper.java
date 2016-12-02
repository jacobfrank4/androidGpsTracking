package bcit.androidgpstracking;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Aingaran on 2016-11-18.
 */

public class SQLiteDatabaseHelper extends SQLiteOpenHelper {



    public static final String DATABASE_NAME = "gps.db";
    public static final int DATABASE_VERSION = 15;
    public static final String TABLE_NAME = "gps_table";
    public static final String COL0 = "ID";
    public static final String COL1 = "TRIP_ID";
    public static final String COL2 = "LAT";
    public static final String COL3 = "LONG";
    public static final String COL4 = "DATE";
    public static final String COL5 = "START_DATE";
    public static final String COL6 = "END_DATE";
    public static final String COL7 = "FREQUENCY_NUMBER";
    public static final String COL8 = "FREQUENCY_TYPE";
    public static final String COL9 = "CONTACTS";
    public static final String COL10 = "TRIP_NAME";




    public SQLiteDatabaseHelper (Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("create table " + TABLE_NAME + "(" +
                                    COL0 + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                    COL1 + " INTEGER, " +
                                    COL2 + " TEXT, " +
                                    COL3 + " TEXT, " +
                                    COL4 + " DATE, " +
                                    COL5 + " DATE, " +
                                    COL6 + " DATE, " +
                                    COL7 + " INTEGER, " +
                                    COL8 + " TEXT, " +
                                    COL9 + " TEXT, " +
                                    COL10 + " TEXT" + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        Log.w(SQLiteDatabaseHelper.class.getName(),
//                "Upgrading database from version " + oldVersion + " to"
//                        + newVersion + ", which will destroy all old data");
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
//        onCreate(db);

        if(newVersion > oldVersion) {
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " +
                        COL10 + " TEXT");
        }
    }

    public boolean insertData(int trip_id, String dbLat, String dbLong, String start, String end,
                              String frequencyNumber, String frequencyType, String contacts, String trip_name){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL1, trip_id);
        values.put(COL2, dbLat);
        values.put(COL3, dbLong);

        //get current date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = sdf.format(new Date());
        values.put(COL4, date);

        values.put(COL5, start);    //start
        values.put(COL6, end);      //date
        values.put(COL7, frequencyNumber);
        values.put(COL8, frequencyType);
        values.put(COL9, contacts);
        values.put(COL10, trip_name);
        long insertId = db.insert(TABLE_NAME, null, values);
        db.close();

        return insertId != -1;
    }

    public Cursor getAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_NAME, null);

        return cursor;
    }

    public Cursor getAllTripNames(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select distinct " + COL10 + " from " + TABLE_NAME, null);

        return cursor;
    }

    public Cursor getTripData(String tripName){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * FROM " + TABLE_NAME + " WHERE " + COL10 + " = " + "\"" + tripName + "\"", null);

        return cursor;
    }
}
