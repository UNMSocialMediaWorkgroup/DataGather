package com.example.datagather;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.GpsStatus.Listener;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
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

public class MainActivity extends Activity {

	private Activity self = this;
    private static final String TAG = ">>MAIN";
    private static final String DATA_PREFS_NAME = "CurrentData";

	
    private LocationManager  locationManager;
    private LocationListener locationListener;
    
	private Location lastLocation;
	private Time time = new Time();
	
	private boolean activityVisible;
	private boolean gpsProviderEnabled = true;
	private boolean gatheringGPSData = false; 
	

	private ToggleButton tbtn_gps;
	private TableLayout gpstable;
	private TextView txtview_CurrentLocationTitle;
	private TextView txtview_CurrentLon, txtview_CurrentLat, txtview_CurrentAlt, txtview_CurrentTime;
	
	

	
	

	void onLocationReceived(Location loc) {
		lastLocation = loc;
		
		// [Database insert Here]?
       
		if (activityVisible){
			txtview_CurrentLocationTitle.setText("GPS Location - Current");
			updateUI();	
        	
        }
        
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//----------------------------------------------------------
		//Location Manager
		locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		
		locationListener = new LocationListener() {
		    
			//Called when a new location is found by the network location provider.
			public void onLocationChanged(Location location) {
				Log.d(TAG, "Got location from " + location.getProvider() + ": " + location.getLatitude() + ", " + location.getLongitude());
		    	onLocationReceived(location);
		    }

		    public void onStatusChanged(String provider, int status, Bundle extras) {
		    	Log.d(TAG, "Status Changed " + provider + " "+status);
		    	Toast.makeText(self, provider+" "+status, Toast.LENGTH_LONG).show();
		    }

		    public void onProviderEnabled(String provider) {
		    	Log.d(TAG, provider+" enabled");
		    	if(provider.equals("gps"))
		    	{
		    		gpsProviderEnabled = true; 
		    	}
		    	updateGPSViewStateStyle();
		    	Toast.makeText(self, provider+" enabled", Toast.LENGTH_LONG).show();
		    }

		    public void onProviderDisabled(String provider) {
		    	
		    	Log.d(TAG, provider+" disabled");
		    	if(provider.equals("gps"))
		    	{
		    		gpsProviderEnabled = false; 
		    	}
		    	updateGPSViewStateStyle();
		    	Toast.makeText(self, provider+" disabled", Toast.LENGTH_LONG).show();
		    }
		};
		  
		

		//----------------------------------------------------------
		//Get Stored data
		SharedPreferences mostRecentData = getSharedPreferences(DATA_PREFS_NAME, 0);
		float  lastupdated_longitude = mostRecentData.getFloat("longitude", 0.0f);
		float  lastupdated_latitude  = mostRecentData.getFloat("latitude", 0.0f);
		float  lastupdated_altitude  = mostRecentData.getFloat("altitude", 0.0f);
		String lastupdated_time      = mostRecentData.getString("time", "--:--:-- --/--/----");

		//----------------------------------------------------------
		//set up UI
		tbtn_gps = (ToggleButton) findViewById(R.id.tb_gatherGPSData);
		gpstable = (TableLayout)findViewById(R.id.table_GPSData);
		txtview_CurrentLocationTitle = (TextView)findViewById(R.id.t_gpsTitleTextView);
		txtview_CurrentLon  = (TextView) findViewById(R.id.t_longitudeTextView);
		txtview_CurrentLat  = (TextView) findViewById(R.id.t_latitudeTextView);
		txtview_CurrentAlt  = (TextView) findViewById(R.id.t_altitudeTextView);
		txtview_CurrentTime = (TextView) findViewById(R.id.t_lastUpdatedTextView);
		
		//----------------------------------------------------------
		//fill UI with saved old data. 
		txtview_CurrentLon.setText(Float.toString(lastupdated_longitude)+ "°");
		txtview_CurrentLat.setText(Float.toString(lastupdated_latitude)+ "°");
		txtview_CurrentAlt.setText(Float.toString(lastupdated_altitude)+ "m");
		txtview_CurrentTime.setText(lastupdated_time);
	}

	@Override
	public void onStart() {
		super.onStart();
		Log.d(TAG, "Started");
		activityVisible = true;
	}

	@Override
	public void onStop() {
		Log.d(TAG, "Stopped");
		activityVisible = false;
		
		//save the Last location into Shared Preferences so they can be displayed the next time the app starts
		SharedPreferences mostRecentData = getSharedPreferences(DATA_PREFS_NAME, 0);
		SharedPreferences.Editor editor = mostRecentData.edit();
      
		editor.putFloat("longitude", (float)lastLocation.getLongitude());
		editor.putFloat("latitude",  (float)lastLocation.getLatitude());
		editor.putFloat("altitude",  (float)lastLocation.getAltitude());
		time.set(lastLocation.getTime());
		editor.putString("time", time.format("%H:%M:%S %m/%d/%Y"));

		// Commit the edits!
		editor.commit();
		
		super.onStop();
	}

	
	public void onToggleClicked_gatherGPSData(View view){
	    if (((ToggleButton) view).isChecked()) {
	    	locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
	    	gatheringGPSData = true;
	    	
	    	updateGPSViewStateStyle();
	    	Toast.makeText(self, "Storing GPS Location Data on Local Device.", Toast.LENGTH_LONG).show();
	    } else {
	    	gatheringGPSData = false;
	    	locationManager.removeUpdates(locationListener);
	    	
	    	updateGPSViewStateStyle();
	    	Toast.makeText(self, "No longer storing GPS Location Data.", Toast.LENGTH_LONG).show();
	    }
	}
	
	private void updateGPSViewStateStyle()
	{
		if(gatheringGPSData)
		{
	    	if(gpsProviderEnabled)
	    	{
	    		
	    		txtview_CurrentLocationTitle.setText("GPS Location - Pending ...");
	    		txtview_CurrentLocationTitle.setTextColor(Color.argb(255, 0, 0, 0));
	    		setTextViewStyle_Active(txtview_CurrentLon);
		    	setTextViewStyle_Active(txtview_CurrentLat);
		    	setTextViewStyle_Active(txtview_CurrentAlt);
		    	setTextViewStyle_Active(txtview_CurrentTime);
		    	gpstable.setBackgroundColor(Color.argb(255, 157, 255, 208));
	    	}
	    	else
	    	{
	    		txtview_CurrentLocationTitle.setText("GPS Location - GPS Disabled");
	    		txtview_CurrentLocationTitle.setTextColor(Color.argb(255, 150, 0, 0));
	    		setTextViewStyle_ActiveError(txtview_CurrentLon);
		    	setTextViewStyle_ActiveError(txtview_CurrentLat);
		    	setTextViewStyle_ActiveError(txtview_CurrentAlt);
		    	setTextViewStyle_ActiveError(txtview_CurrentTime);
		    	gpstable.setBackgroundColor(Color.argb(255, 255, 167, 167));
	    	}
		}
		else
		{
			txtview_CurrentLocationTitle.setText("GPS Location - Most Recent");
			txtview_CurrentLocationTitle.setTextColor(Color.argb(255, 0, 0, 0));
			setTextViewStyle_Inactive(txtview_CurrentLon);
	    	setTextViewStyle_Inactive(txtview_CurrentLat);
	    	setTextViewStyle_Inactive(txtview_CurrentAlt);
	    	setTextViewStyle_Inactive(txtview_CurrentTime);
	    	gpstable.setBackgroundColor(Color.argb(255, 214, 214, 214));
		}
    	
		
	}
	private void setTextViewStyle_Active(TextView textview)
	{
		textview.setTextColor(Color.argb(255, 0, 88, 38));
		//textview.setBackgroundColor(Color.argb(255, 157, 255, 208));
	}
	private void setTextViewStyle_ActiveError(TextView textview)
	{
		textview.setTextColor(Color.argb(255, 88, 0, 0));
		//textview.setBackgroundColor(Color.argb(255, 255, 167, 167));
	}
	private void setTextViewStyle_Inactive(TextView textview)
	{
		textview.setTextColor(Color.argb(255, 110, 110, 110));
		//textview.setBackgroundColor(Color.argb(255, 214, 214, 214));
	}
	
	
	
	
	public void updateUI() {
		
		Log.d(TAG, "UI Update");
		
		if (lastLocation != null) {
			// durationSeconds = getDurationSeconds(mLastLocation.getTime());
			
			txtview_CurrentLon.setText(Double.toString(lastLocation.getLongitude())+ "°");
			txtview_CurrentLat.setText(Double.toString(lastLocation.getLatitude())+ "°");
			txtview_CurrentAlt.setText(Double.toString(lastLocation.getAltitude())+"m");
			time.set(lastLocation.getTime());
			txtview_CurrentTime.setText(time.format("%H:%M:%S %m/%d/%Y"));
		}
	}

	// /----Auto Gen Menu stuff.
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

}
