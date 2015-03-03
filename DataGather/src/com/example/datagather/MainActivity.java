package com.example.datagather;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Timer;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.GpsStatus.Listener;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends Activity implements SensorEventListener{

	private Activity self = this;
	private static final String TAG = ">>MAIN";
	

	private boolean activityVisible;
	
	//-----Network
	private boolean networkConnected   = false;
	private  String dataPostUrl = "http://104.236.211.212:3000/submitdatapoint";
	private  String thisPhoneNumber = "";

	//-----Prefs
	private static final String DATA_PREFS_NAME = "CurrentData";
	
	//-----DB
	DatabaseHandler db;
	public int numberOfSavedDataPoints = 0;
	public int maxDataPoints = 50;
	
	public DataPoint currentDataPoint = new DataPoint();
	
	//-----Data
	Timer timer = new Timer();
	
	private Time time = new Time();
	private long timeOfLastSave_MiliSec = 0;
	private long saveFrequency_MiliSec = 1000;
	
	//-----GPS
	public boolean isCapturingGPSData   = false;
	
	private LocationManager locationManager;
	private LocationListener locationListener;
	private boolean gpsProviderEnabled = true;
	private Location lastLocation;
	
	
	
	//-----Motion
	public boolean isCapturingMotionData = false;
	
	private SensorManager sensorManager;
	
	private Sensor accelSensor;
	private float[] gravity = {0.0f,0.0f,0.0f};
	private float[] linear_acceleration = {0.0f,0.0f,0.0f};
	
	private Sensor rotationSensor;
	private float[] rotation = {0.0f,0.0f,0.0f};
	

	//-----UI
	private ToggleButton tbtn_gps, tbtn_motion;
	private TableLayout gpstable, motiontable;
	private TextView txtview_CurrentLocationTitle;
	private TextView txtview_CurrentLon, txtview_CurrentLat, txtview_CurrentAlt, txtview_CurrentTime, txtview_PointsSaved;
	private TextView txtview_accelX, txtview_accelY, txtview_accelZ, txtview_rotationX,txtview_rotationY,txtview_rotationZ;
	private TextView txtview_httpReult;

	void onLocationReceived(Location loc) {
		lastLocation = loc;
		time.set(lastLocation.getTime());
		currentDataPoint.setLongitude(lastLocation.getLongitude());
		currentDataPoint.setLatitude(lastLocation.getLatitude());
		currentDataPoint.setAltitude(lastLocation.getAltitude());
		currentDataPoint.setTime(lastLocation.getTime());
		currentDataPoint.setWritten();
		
		

		if (activityVisible) {
			updateUI();
		}

	}

	@Override
	public void onSensorChanged(SensorEvent event){
        Sensor sensor = event.sensor;

        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
        	//Log.d(TAG, "Got Accel x:"+event.values[0]+" y:"+event.values[1]+" z:"+event.values[2]);
    		// In this example, alpha is calculated as t / (t + dT),
    		// where t is the low-pass filter's time-constant and
    		// dT is the event delivery rate.
    		
    		final float alpha = 0.8f;
    		
    		// Isolate the force of gravity with the low-pass filter.
    		gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
    		gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
    		gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];
    		
    		// Remove the gravity contribution with the high-pass filter.
    		linear_acceleration[0] = event.values[0] - gravity[0];
    		linear_acceleration[1] = event.values[1] - gravity[1];
    		linear_acceleration[2] = event.values[2] - gravity[2];
    		
    		time.setToNow();
    		currentDataPoint.setTime(time.toMillis(false));
			currentDataPoint.setAccelx(linear_acceleration[0]);
			currentDataPoint.setAccely(linear_acceleration[1]);
			currentDataPoint.setAccelz(linear_acceleration[2]);
			currentDataPoint.setWritten();

        }
        else if (sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
        
        	//Log.d(TAG, "Got Rotation x:"+event.values[0]+" y:"+event.values[1]+" z:"+event.values[2]);

        	rotation[0] = event.values[0];
        	rotation[1] = event.values[1];
        	rotation[2] = event.values[2];
        	
			currentDataPoint.setRotationx(rotation[0]);
			currentDataPoint.setRotationy(rotation[1]);
			currentDataPoint.setRotationz(rotation[2]);
			currentDataPoint.setWritten();
        	
        
        }
        
       
        
        if (activityVisible) {
			updateUI();
		}
		
		
		
		  
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Log.d(TAG, "Created");
		// ----------------------------------------------------------
		// Get Telephone Number for ID. 
		TelephonyManager tMgr = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
		thisPhoneNumber = tMgr.getLine1Number();
		
		
		// ----------------------------------------------------------
		// Location Manager
		locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		locationListener = new LocationListener() {

			// Called when a new location is found by the network location
			// provider.
			public void onLocationChanged(Location location) {
				Log.d(TAG, "Got location from " + location.getProvider() + ": " + location.getLatitude() + ", " + location.getLongitude());
				onLocationReceived(location);
			}

			public void onStatusChanged(String provider, int status, Bundle extras) {
				Log.d(TAG, "Status Changed " + provider + " " + status);
				Toast.makeText(self, provider + " " + status, Toast.LENGTH_LONG).show();
			}

			public void onProviderEnabled(String provider) {
				Log.d(TAG, provider + " enabled");
				if (provider.equals("gps")) {
					gpsProviderEnabled = true;
				}
				updateGPSViewStateStyle();
				Toast.makeText(self, provider + " enabled", Toast.LENGTH_LONG).show();
			}

			public void onProviderDisabled(String provider) {

				Log.d(TAG, provider + " disabled");
				if (provider.equals("gps")) {
					gpsProviderEnabled = false;
				}
				updateGPSViewStateStyle();
				Toast.makeText(self, provider + " disabled", Toast.LENGTH_LONG).show();
			}
		};

		
		// ----------------------------------------------------------
		// Set up motion sensors. 
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
	
		// ----------------------------------------------------------
		// new database
		db = new DatabaseHandler(this);
		numberOfSavedDataPoints = db.geDataPointCount();
		
		currentDataPoint = new DataPoint();
		timer.scheduleAtFixedRate( new InsertDataPointTask(db,this), 1000,1000);

		// ----------------------------------------------------------
		// Get Stored data
		SharedPreferences mostRecentData = getSharedPreferences(DATA_PREFS_NAME, 0);
		float lastupdated_longitude = mostRecentData.getFloat("longitude", 0.0f);
		float lastupdated_latitude = mostRecentData.getFloat("latitude", 0.0f);
		float lastupdated_altitude = mostRecentData.getFloat("altitude", 0.0f);
		String lastupdated_time = mostRecentData.getString("time", "--:--:-- --/--/----");

		// ----------------------------------------------------------
		// set up UI
		tbtn_gps    = (ToggleButton) findViewById(R.id.tb_gatherGPSData);
		tbtn_motion = (ToggleButton) findViewById(R.id.tb_gatherMotionDataX);
		gpstable    = (TableLayout) findViewById(R.id.table_GPSData);
		motiontable = (TableLayout) findViewById(R.id.table_MotionData);
		txtview_CurrentLocationTitle = (TextView) findViewById(R.id.t_gpsTitleTextView);
		txtview_CurrentLon = (TextView) findViewById(R.id.t_longitudeTextView);
		txtview_CurrentLat = (TextView) findViewById(R.id.t_latitudeTextView);
		txtview_CurrentAlt = (TextView) findViewById(R.id.t_altitudeTextView);
		txtview_CurrentTime = (TextView) findViewById(R.id.t_lastUpdatedTextView);
		txtview_PointsSaved = (TextView) findViewById(R.id.t_pointsSavedTextView);
		
		txtview_accelX = (TextView) findViewById(R.id.t_accelX);
		txtview_accelY = (TextView) findViewById(R.id.t_accelY);
		txtview_accelZ = (TextView) findViewById(R.id.t_accelZ);
		txtview_rotationX = (TextView) findViewById(R.id.t_rotationX);
		txtview_rotationY = (TextView) findViewById(R.id.t_rotationY);
		txtview_rotationZ = (TextView) findViewById(R.id.t_rotationZ);
		
		txtview_httpReult   = (TextView) findViewById(R.id.t_httpResultTextView);

		// ----------------------------------------------------------
		// fill UI with saved old data.
		txtview_CurrentLon.setText(Float.toString(lastupdated_longitude) + "°");
		txtview_CurrentLat.setText(Float.toString(lastupdated_latitude) + "°");
		txtview_CurrentAlt.setText(Float.toString(lastupdated_altitude) + "m");
		txtview_CurrentTime.setText(lastupdated_time);
		txtview_PointsSaved.setText("" + numberOfSavedDataPoints);

		// ----------------------------------------------------------
		// check if you are connected or not
		if (isConnected()) {
			Toast.makeText(self, "Network : Connected", Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(self, "Network : Not Connected", Toast.LENGTH_LONG).show();
		}

	}

	@Override
	public void onStart() {
		super.onStart();
		Log.d(TAG, "Started");
		activityVisible = true;
		
	}
	
	@Override
	protected void onResume() {
	    super.onResume();
	    Log.d(TAG, "Resumed");
	   // sensorManager.registerListener(this, mLight, SensorManager.SENSOR_DELAY_NORMAL);
	  }
	
	@Override
	protected void onPause() {
	    super.onPause();
	    Log.d(TAG, "Paused");
	   // sensorManager.unregisterListener(this);
	  }
	 
	@Override
	public void onStop() {
			Log.d(TAG, "Stopped");
			activityVisible = false;

			// ---Save View Info
			if (lastLocation != null) {
				// save the Last location into Shared Preferences so they can be
				// displayed the next time the app starts
				SharedPreferences mostRecentData = getSharedPreferences(DATA_PREFS_NAME, 0);
				SharedPreferences.Editor editor = mostRecentData.edit();

				editor.putFloat("longitude", (float) lastLocation.getLongitude());
				editor.putFloat("latitude", (float) lastLocation.getLatitude());
				editor.putFloat("altitude", (float) lastLocation.getAltitude());
				time.set(lastLocation.getTime());
				editor.putString("time", time.format("%H:%M:%S %m/%d/%Y"));

				// Commit the edits!
				editor.commit();
			}

			super.onStop();
		}


	
	// ----------------------------------------------------------
	// Network Functions
	public boolean isConnected() {
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected())
		{
			networkConnected = true;
			return true;
		}
		else
		{
			networkConnected = false;
			return false;
		}
	}
	
	public void httpPOSTResult(String result)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(result.toString()+"\n\nDo you want to delete all locally saved GPS Data?");
		builder.setTitle("GPS Data");

		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				clearGPSData();
			}
		});
		builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				// User cancelled the dialog
			}
		});

		AlertDialog dialog = builder.create();
		dialog.show();
	}
	
	// ----------------------------------------------------------
	// Data Functions
	public void turnOnGPSDataCapture() {
		if (numberOfSavedDataPoints < maxDataPoints) {   //There is room for more points. 
			
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
			isCapturingGPSData = true;
	
			updateGPSViewStateStyle();
			Toast.makeText(self, "Storing GPS Location Data on Local Device.", Toast.LENGTH_LONG).show();
		}
		else
		{
			updateUI_DataFull();
		}
	}

	public void turnOffGPSDataCapture() {
		
		isCapturingGPSData = false;
		locationManager.removeUpdates(locationListener);

		updateGPSViewStateStyle();
		Toast.makeText(self, "No longer storing GPS Location Data.", Toast.LENGTH_LONG).show();
	}

	public void turnOnMotionDataCapture() {
		if (numberOfSavedDataPoints < maxDataPoints) {   //There is room for more points. 
			
			isCapturingMotionData = true;
			sensorManager.registerListener(this, accelSensor, SensorManager.SENSOR_DELAY_NORMAL);
			sensorManager.registerListener(this, rotationSensor, SensorManager.SENSOR_DELAY_NORMAL);
			setTextViewStyle_Active(txtview_accelX);
			setTextViewStyle_Active(txtview_accelY);
			setTextViewStyle_Active(txtview_accelZ);	
			setTextViewStyle_Active(txtview_rotationX);
			setTextViewStyle_Active(txtview_rotationY);
			setTextViewStyle_Active(txtview_rotationZ);	
			motiontable.setBackgroundColor(Color.argb(255, 157, 255, 208));
			Toast.makeText(self, "Storing Motion Data on Local Device.", Toast.LENGTH_LONG).show();
		}
		else
		{
			
			updateUI_DataFull();
		}
	}

	public void turnOffMotionDataCapture() {
		isCapturingMotionData = true;
		sensorManager.unregisterListener(this,accelSensor);
		sensorManager.unregisterListener(this, rotationSensor);
		setTextViewStyle_Inactive(txtview_accelX);
		setTextViewStyle_Inactive(txtview_accelY);
		setTextViewStyle_Inactive(txtview_accelZ);
		setTextViewStyle_Inactive(txtview_rotationX);
		setTextViewStyle_Inactive(txtview_rotationY);
		setTextViewStyle_Inactive(txtview_rotationZ);
		motiontable.setBackgroundColor(Color.argb(255, 214, 214, 214));
		Toast.makeText(self, "No longer storing Motion Data.", Toast.LENGTH_LONG).show();
	}
	
	public void packDataPoints() {

		ArrayList<DataPoint> datapoints = db.getAllDataPoints(); 
		
    	JSONObject dataJSONObj = new JSONObject();
    	JSONArray pointsJsonArray =  new JSONArray();
        try {

			for (DataPoint  point : datapoints)
			{
				point.setOwner(thisPhoneNumber);
				
				JSONObject pointJsonObject =  new JSONObject();
				//{"lon":10.0011001, "lat": 11.00111001, "alt" : 4.32220002, "time" : 1424635760000, "owner" : "+15054591694" }
				
				pointJsonObject.put("owner", point.getOwner());
	
				pointJsonObject.put("lon" , point.getLongitude());
				pointJsonObject.put("lat" , point.getLatitude());
				pointJsonObject.put("alt" , point.getAltitude());
				
				pointJsonObject.put("time", point.getTime());
				
				pointJsonObject.put("accelx" , point.getAccelx());
				pointJsonObject.put("accely" , point.getAccely());
				pointJsonObject.put("accelz" , point.getAccelz());
				
				pointJsonObject.put("rotx" , point.getRotationx());
				pointJsonObject.put("roty" , point.getRotationy());
				pointJsonObject.put("rotz" , point.getRotationz());
				
				pointsJsonArray.put( pointJsonObject );
				
			}
			
			dataJSONObj.put("data", pointsJsonArray);
		
        } catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        HttpAsyncTask httptask = new HttpAsyncTask(this);
        httptask.setJsonObjectToPost(dataJSONObj);
        //txtview_httpReult.setText( ">>"+GPSData.toString());
        httptask.execute(dataPostUrl);
	}
	
	public void clearGPSData() {
		db.clearDataPoints();
		numberOfSavedDataPoints = db.geDataPointCount();
		lastLocation = null;
		txtview_CurrentLocationTitle.setText("GPS Location - None");
		txtview_CurrentLocationTitle.setTextColor(Color.argb(255, 0, 0, 0));
		updateUI();
		Toast.makeText(self, "Data deleted.", Toast.LENGTH_LONG).show();
	}

	// ----------------------------------------------------------
	// Button Functions
	public void onToggleClicked_gatherGPSData(View view) {
		if (((ToggleButton) view).isChecked()) {
			turnOnGPSDataCapture();
		} else {
			turnOffGPSDataCapture();
		}
	}

	public void onToggleClicked_gatherMotionData(View view) {
		if (((ToggleButton) view).isChecked()) {
			turnOnMotionDataCapture();
			
		} else {
			turnOffMotionDataCapture();
		}
	}
	
	public void onClickedSendDataPoints(View view) {
		
		if (isConnected())
		{
			numberOfSavedDataPoints = db.geDataPointCount();
			
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Sending " + numberOfSavedDataPoints + "  Data Points. Would you like to continue?");
			builder.setTitle("Data");

			builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					packDataPoints();
				}
			});
			builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					// User cancelled the dialog
				}
			});

			AlertDialog dialog = builder.create();
			dialog.show();
			
		}
		else
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("You are not connected to a network. Please enable your Wireless connection or Data Service and try again.");
			builder.setTitle("Data");

			builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
				}
			});
			
			AlertDialog dialog = builder.create();
			dialog.show();
			
		}
		
		

	}

	public void onClickedClearDataPoints(View view) {
		

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Are you sure you want to delete all saved GPS Data?");
		builder.setTitle("GPS Data");

		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				clearGPSData();
			}
		});
		builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				// User cancelled the dialog
			}
		});

		AlertDialog dialog = builder.create();
		dialog.show();
	}

	public void onClickedViewDataPoints(View view) {
		Intent intent = new Intent(this, DataViewActivity.class);
		startActivity(intent);
	}

	// ----------------------------------------------------------
	// View Functions

	
 	private void updateGPSViewStateStyle() {
		if (isCapturingGPSData) {
			if (gpsProviderEnabled) {

				txtview_CurrentLocationTitle.setText("GPS Location - Pending ...");
				txtview_CurrentLocationTitle.setTextColor(Color.argb(255, 0, 0, 0));
				setTextViewStyle_Active(txtview_CurrentLon);
				setTextViewStyle_Active(txtview_CurrentLat);
				setTextViewStyle_Active(txtview_CurrentAlt);
				gpstable.setBackgroundColor(Color.argb(255, 157, 255, 208));
			} else {
				txtview_CurrentLocationTitle.setText("GPS Location - GPS Disabled!");
				txtview_CurrentLocationTitle.setTextColor(Color.argb(255, 150, 0, 0));
				setTextViewStyle_ActiveError(txtview_CurrentLon);
				setTextViewStyle_ActiveError(txtview_CurrentLat);
				setTextViewStyle_ActiveError(txtview_CurrentAlt);
				gpstable.setBackgroundColor(Color.argb(255, 255, 167, 167));
			}
		} else {
			txtview_CurrentLocationTitle.setText("GPS Location - Most Recent");
			txtview_CurrentLocationTitle.setTextColor(Color.argb(255, 0, 0, 0));
			setTextViewStyle_Inactive(txtview_CurrentLon);
			setTextViewStyle_Inactive(txtview_CurrentLat);
			setTextViewStyle_Inactive(txtview_CurrentAlt);
			gpstable.setBackgroundColor(Color.argb(255, 214, 214, 214));
		}

	}

	private void setTextViewStyle_Active(TextView textview) {
		textview.setTextColor(Color.argb(255, 0, 88, 38));
		// textview.setBackgroundColor(Color.argb(255, 157, 255, 208));
	}

	private void setTextViewStyle_ActiveError(TextView textview) {
		textview.setTextColor(Color.argb(255, 88, 0, 0));
		// textview.setBackgroundColor(Color.argb(255, 255, 167, 167));
	}

	private void setTextViewStyle_Inactive(TextView textview) {
		textview.setTextColor(Color.argb(255, 110, 110, 110));
		// textview.setBackgroundColor(Color.argb(255, 214, 214, 214));
	}

	public void updateUI() {

		//Log.d(TAG, "UI Update");

		if (lastLocation != null) {
			// durationSeconds = getDurationSeconds(mLastLocation.getTime());
			txtview_CurrentLocationTitle.setText("GPS Location - Current");
			txtview_CurrentLon.setText(Double.toString(lastLocation.getLongitude()) + "°");
			txtview_CurrentLat.setText(Double.toString(lastLocation.getLatitude()) + "°");
			txtview_CurrentAlt.setText(Double.toString(lastLocation.getAltitude()) + "m");
			txtview_PointsSaved.setText("" + numberOfSavedDataPoints);	
		} else {
			txtview_CurrentLon.setText(0.0f + "°");
			txtview_CurrentLat.setText(0.0f + "°");
			txtview_CurrentAlt.setText(0.0f + "m");
			txtview_PointsSaved.setText("" + numberOfSavedDataPoints);
		}
		
		txtview_CurrentTime.setText(time.format("%H:%M:%S %m/%d/%Y"));
		
  		txtview_accelX.setText(Float.toString(linear_acceleration[0]) + "m/s\u00B2");
  		txtview_accelY.setText(Float.toString(linear_acceleration[1]) + "m/s\u00B2");
  		txtview_accelZ.setText(Float.toString(linear_acceleration[2]) + "m/s\u00B2");
		txtview_rotationX.setText(Float.toString(rotation[0]));
		txtview_rotationY.setText(Float.toString(rotation[1]));
		txtview_rotationZ.setText(Float.toString(rotation[2]));
		
	}

	public void updateUI_DataFull()
	{
		tbtn_gps.setChecked(false);
		tbtn_motion.setChecked(false);
		txtview_CurrentLocationTitle.setText("GPS Location - DATA FULL!");
		txtview_CurrentLocationTitle.setTextColor(Color.argb(255, 150, 0, 0));
		
		// ---Build Alert
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("You have used up all your space for data (" + maxDataPoints
				+ " data Points).\nYou must Send your data or Delete it before continuing to capture.");
		builder.setTitle("Data");
		builder.setPositiveButton("Ok", null);
		AlertDialog dialog = builder.create();
		dialog.show();
		
		
		
	}
	// ----------------------------------------------------------
	// Auto Gen Menu stuff.
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

}
