package bcit.androidgpstracking;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Aingaran on 2016-11-18.
 */

public class SQLiteDatabaseHelper extends SQLiteOpenHelper {



    public static final String DATABASE_NAME = "gps.db";
    public static final int DATABASE_VERSION = 5;
    public static final String TABLE_NAME = "gps_table";
    public static final String COL0 = "ID";
    public static final String COL1 = "TRIP_ID";
    public static final String COL2 = "LAT";
    public static final String COL3 = "LONG";



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
                                    COL3 + " TEXT)");
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
                        COL3 + " TEXT");
        }

    }

    public boolean insertData(int trip_id, String dbLat, String dbLong){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL1, trip_id);
        values.put(COL2, dbLat);
        values.put(COL3, dbLong);

        long insertId = db.insert(TABLE_NAME, null, values);
        db.close();

        if(insertId == -1)
            return false;
        return true;
    }

    public Cursor getAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_NAME, null);

        return cursor;
    }
}
