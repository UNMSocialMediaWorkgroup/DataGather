package com.example.datagather;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHandler extends SQLiteOpenHelper {

	private static final String TAG = ">>DB Handeler";
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "gathereddata";
	
	//GPS 
    private static final String TABLE_GPSPOINTS = "GPSpoints";
    private static final String KEY_LON  = "longitude";
    private static final String KEY_LAT  = "latitude";
    private static final String KEY_ALT  = "altitude";
    private static final String KEY_TIME = " time";
	
    private static final String CREATE_GPSPOINTS_TABLE = "CREATE TABLE " + TABLE_GPSPOINTS + "( "
			+ KEY_LON   + " DOUBLE, "
            + KEY_LAT   + " DOUBLE, "
			+ KEY_ALT   + " DOUBLE, "
			+ KEY_TIME  + " INTEGER "	
			+ ")";
    
    
	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		//GPS Table
        db.execSQL(CREATE_GPSPOINTS_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		 db.execSQL("DROP TABLE IF EXISTS " + TABLE_GPSPOINTS);
		 onCreate(db);
	}
	
	
	//----------------------------------------------------------
	//GPS DATA POINTS
	
	// Adding new GPS Data Point
    void addGPSDataPoint(DataPointGPS gpspoint) {
    	SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();
        values.put(KEY_LON , gpspoint.getLongitude()); 
        values.put(KEY_LAT , gpspoint.getLatitude()); 
        values.put(KEY_ALT , gpspoint.getAltitude()); 
        values.put(KEY_TIME, gpspoint.getTime()); 
        db.insert(TABLE_GPSPOINTS, null, values);
        db.close(); // Closing database connection
        Log.d(TAG, "Saved GPS Data Point");
    }
    
    //Getting GPS Data Points
    public ArrayList<DataPointGPS> getAllGPSDataPoints() {
    	ArrayList<DataPointGPS> gpsPointList = new ArrayList<DataPointGPS>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_GPSPOINTS;
 
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
 
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
            	DataPointGPS gpspoint = new DataPointGPS();
            	gpspoint.setLongitude(cursor.getDouble(0));
            	gpspoint.setLatitude(cursor.getDouble(1));
            	gpspoint.setAltitude(cursor.getDouble(2));
            	gpspoint.setTime(cursor.getLong(3));
              
                // Adding gps point to list
            	gpsPointList.add(gpspoint);
            } while (cursor.moveToNext());
        }
 
        db.close();
        Log.d(TAG, "Getting All GPS Data Points");
        // return contact list
        return gpsPointList;
    }
    
    public int getGPSDataPointCount()
    {
    	String selectQuery = "SELECT * FROM " + TABLE_GPSPOINTS;
    	SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        int count = cursor.getCount();;
        db.close();
        Log.d(TAG, "There are "+count+" GPS Data Points");
        return count;
    }
    
    public void clearGPSDataPoints()
    {
    	SQLiteDatabase db = this.getWritableDatabase();
    	db.execSQL("DROP TABLE IF EXISTS " + TABLE_GPSPOINTS);
    	db.execSQL(CREATE_GPSPOINTS_TABLE);
    	 db.close();
    	Log.d(TAG, "GPS data points cleared.");
    }
    
    //----------------------------------------------------------

}
