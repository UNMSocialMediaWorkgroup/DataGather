package com.example.datagather;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends Activity {

	private Activity self = this;
    private static final String TAG = ">>MAIN";
	
	private RunManager mRunManager;
	
	private Location mLastLocation;

	private ToggleButton tbtn_gps;
	private TextView txtview_CurrentLocation;
	private TextView txtview_CurrentLon, txtview_CurrentLat, txtview_CurrentAlt;

	private LocationReceiver mLocationReceiver = new LocationReceiver();
	
	
	void onLocationReceived(Location loc) {
		mLastLocation = loc;
		// [Database insert Here]
        //if (isActivityVisible()){
        	updateUI();	
        //}
        
	}

	void onProviderEnabledChanged(boolean enabled) {
		int toastText = enabled ? R.string.gps_enabled : R.string.gps_disabled;
		Toast.makeText(self, toastText, Toast.LENGTH_LONG).show();
	}
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		

		mLocationReceiver.setActionActivity(this);
		
		mRunManager = RunManager.get(this);

		tbtn_gps = (ToggleButton) findViewById(R.id.toggleButton_gatherGPSData);
		txtview_CurrentLon = (TextView) findViewById(R.id.t_longetude);
		txtview_CurrentLat = (TextView) findViewById(R.id.t_latitude);
		txtview_CurrentAlt = (TextView) findViewById(R.id.t_altitude);

		mRunManager.startLocationUpdates();
		

		updateUI();

	}

	@Override
	public void onStart() {
		super.onStart();
		registerReceiver(mLocationReceiver, new IntentFilter(
				RunManager.ACTION_LOCATION));
	}

	@Override
	public void onStop() {
		unregisterReceiver(mLocationReceiver);
		mRunManager.stopLocationUpdates();

		super.onStop();
	}

	public void updateUI() {
		
		Log.d(TAG, "UI Update");
		
		if (mLastLocation != null) {
			// durationSeconds = getDurationSeconds(mLastLocation.getTime());
			txtview_CurrentLon.setText(Double.toString(mLastLocation.getLongitude())+ "°");
			txtview_CurrentLat.setText(Double.toString(mLastLocation.getLatitude())+ "°");
			txtview_CurrentAlt.setText(Double.toString(mLastLocation.getAltitude()));
		}
		
		
		
		

	}

	private static boolean activityVisible;

	public static boolean isActivityVisible() {
		return activityVisible;
	}

	public static void activityResumed() {
		activityVisible = true;
	}

	public static void activityPaused() {
		activityVisible = false;
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
