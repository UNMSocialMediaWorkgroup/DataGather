package com.accelerama.datagather;

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
	private static final int DATABASE_VERSION = 7;
	private static final String DATABASE_NAME = "gathereddata";
	
	//GPS 
    private static final String TABLE_POINTS = "points";
    private static final String KEY_LON  = "longitude";
    private static final String KEY_LAT  = "latitude";
    private static final String KEY_ALT  = "altitude";
    private static final String KEY_TIME = " time";
    private static final String KEY_ACCEL = "accel";
    private static final String KEY_ROTX  = "rotationX";
    private static final String KEY_ROTY  = "rotationY";
    private static final String KEY_ROTZ  = "rotationZ";
    private static final String KEY_BRGHT = "Brightness";
    private static final String KEY_PRESS = "pressure";
	
    private static final String CREATE_POINTS_TABLE = "CREATE TABLE " + TABLE_POINTS + "( "
			+ KEY_LON   + " DOUBLE, "
            + KEY_LAT   + " DOUBLE, "
			+ KEY_ALT   + " DOUBLE, "
			+ KEY_TIME  + " INTEGER, "	
			+ KEY_ACCEL + " TEXT, "
			+ KEY_ROTX  + " FLOAT, "
			+ KEY_ROTY  + " FLOAT, "
			+ KEY_ROTZ  + " FLOAT, "
			+ KEY_BRGHT + " TEXT, "
			+ KEY_PRESS + " FLOAT "
			+ ")";
    
    
	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		//GPS Table
        db.execSQL(CREATE_POINTS_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		 db.execSQL("DROP TABLE IF EXISTS " + TABLE_POINTS);
		 onCreate(db);
	}
	
	
	//----------------------------------------------------------
	//GPS DATA POINTS
	
	// Adding new GPS Data Point
    void addDataPoint(DataPoint point) {
    	SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();
        values.put(KEY_LON ,   point.getLongitude()); 
        values.put(KEY_LAT ,   point.getLatitude()); 
        values.put(KEY_ALT ,   point.getAltitude()); 
        values.put(KEY_TIME,   point.getTime());
        values.put(KEY_ACCEL,  point.compressAccelValues());
		values.put(KEY_ROTX,   point.getRotationx());
		values.put(KEY_ROTY,   point.getRotationy());
		values.put(KEY_ROTZ,   point.getRotationz());
		values.put(KEY_BRGHT,  point.compressBrightnessValues());
		values.put(KEY_PRESS,  point.getPressure());
		
        db.insert(TABLE_POINTS, null, values);
        db.close(); // Closing database connection
        Log.d(TAG, "Saved GPS Data Point");
    }
    
    //Getting GPS Data Points
    public ArrayList<DataPoint> getAllDataPoints() {
    	ArrayList<DataPoint> pointList = new ArrayList<DataPoint>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_POINTS;
 
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
 
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
            	DataPoint point = new DataPoint();
            	point.setLongitude( cursor.getDouble(0));
            	point.setLatitude(  cursor.getDouble(1));
            	point.setAltitude(  cursor.getDouble(2));
            	point.setTime(      cursor.getLong(3));
            	point.setCompressedAccelString(cursor.getString(4));
            	point.setRotationx( cursor.getFloat(5));
            	point.setRotationy( cursor.getFloat(6));
            	point.setRotationz( cursor.getFloat(7));
            	point.setCompressedBrightnessString(cursor.getString(8));
            	point.setPressure(  cursor.getFloat(9));
            	// Adding data point to list
            	pointList.add(point);
            } while (cursor.moveToNext());
        }
 
        db.close();
        Log.d(TAG, "Getting All Data Points");
        // return contact list
        return pointList;
    }
    
    public int getDataPointCount()
    {
    	String selectQuery = "SELECT * FROM " + TABLE_POINTS;
    	SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        int count = cursor.getCount();;
        db.close();
        Log.d(TAG, "There are "+count+" Data Points");
        return count;
    }
    
    public void clearDataPoints()
    {
    	SQLiteDatabase db = this.getWritableDatabase();
    	db.execSQL("DROP TABLE IF EXISTS " + TABLE_POINTS);
    	db.execSQL(CREATE_POINTS_TABLE);
    	 db.close();
    	Log.d(TAG, "Data points cleared.");
    }
    
    //----------------------------------------------------------

}
