package com.example.datagather;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
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
	private  String ownerID = "";
	
	private static final String PREFS_NETWORK = "Network";
	private static final String PREFS_NETWORK_OWNERID = "ownerID";

	//-----DB
	DatabaseHandler db;
	public int numberOfSavedDataPoints = 0;
	public int maxDataPoints = 50000;
	
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
	
	//-----Light
	public boolean isCapturingLightData = false;
	private Sensor lightSensor;
	
	//-----Pressure
	public boolean isCapturingPressureData = false;
	private Sensor pressureSensor;
	

	//-----UI
	private ToggleButton tbtn_gps, tbtn_motion, tbtn_light,  tbtn_pressure;
	private TableLayout gpstable, motiontable, lighttable, pressuretable;
	private TextView txtview_CurrentLocationTitle;
	private TextView txtview_CurrentLon, txtview_CurrentLat, txtview_CurrentAlt, txtview_CurrentTime, txtview_PointsSaved;
	private TextView txtview_accelX, txtview_accelY, txtview_accelZ, txtview_rotationX,txtview_rotationY,txtview_rotationZ;
	private TextView txtview_light;
	private TextView txtview_pressure;
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
        	
        	time.setToNow();
    		currentDataPoint.setTime(time.toMillis(false));
			currentDataPoint.setRotationx(rotation[0]);
			currentDataPoint.setRotationy(rotation[1]);
			currentDataPoint.setRotationz(rotation[2]);
			currentDataPoint.setWritten();
        	
        
        }else  if(event.sensor.getType() == Sensor.TYPE_LIGHT){
        	//Log.d(TAG, "Got light: " + event.values[0]);
        	
        	time.setToNow();
    		currentDataPoint.setTime(time.toMillis(false));
			currentDataPoint.setBrightness(event.values[0]);
			currentDataPoint.setWritten();
        	
        }else  if(event.sensor.getType() == Sensor.TYPE_PRESSURE){
    	//Log.d(TAG, "Got Pressure: " + event.values[0]);    	
	    	time.setToNow();
			currentDataPoint.setTime(time.toMillis(false));
			currentDataPoint.setPressure(event.values[0]);
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
		// Get Owner Hash or generate a new one if it hasn't been created. 
		SharedPreferences networkPrefs = getSharedPreferences(PREFS_NETWORK, 0);
		ownerID = networkPrefs.getString(PREFS_NETWORK_OWNERID, "NO_ID");
		//if no ID generate new one. 
		if(ownerID.equals("NO_ID") || ownerID == null )
		{
			Log.d(TAG, "Generating new owner ID.");
			ownerID = generateOwnerHash(thisPhoneNumber);
			SharedPreferences.Editor editor = networkPrefs.edit();
			editor.putString(PREFS_NETWORK_OWNERID,ownerID);
			// Commit the edits!
			editor.commit();
		}
		Log.d(TAG, "OWNER ID : "+ownerID);
		
		
		
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
		// Set up Light sensors.
		lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
		
		// ----------------------------------------------------------
		// Set up Pressure sensor.
		pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
		
		// ----------------------------------------------------------
		// new database
		db = new DatabaseHandler(this);
		numberOfSavedDataPoints = db.geDataPointCount();
		
		currentDataPoint = new DataPoint();
		timer.scheduleAtFixedRate( new InsertDataPointTask(db,this), 1000,1000);

		// ----------------------------------------------------------
		// set up UI
		tbtn_gps    = (ToggleButton) findViewById(R.id.tb_gatherGPSData);
		tbtn_motion = (ToggleButton) findViewById(R.id.tb_gatherMotionDataX);
		tbtn_light  = (ToggleButton) findViewById(R.id.tb_gatherLightData);
		tbtn_pressure  = (ToggleButton) findViewById(R.id.tb_gatherPressureData);
		
		gpstable    = (TableLayout) findViewById(R.id.table_GPSData);
		motiontable = (TableLayout) findViewById(R.id.table_MotionData);
		lighttable  = (TableLayout) findViewById(R.id.table_LightData);
		pressuretable  = (TableLayout) findViewById(R.id.table_PressureData);
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
		
		txtview_light	    = (TextView)findViewById(R.id.t_light);
		
		txtview_pressure	= (TextView)findViewById(R.id.t_pressure);
		
		txtview_httpReult   = (TextView) findViewById(R.id.t_httpResultTextView);
		turnOffAllDataCapture();
		updateUI();
		
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
	
	//http://howtodoinjava.com/2013/07/22/how-to-generate-secure-password-hash-md5-sha-pbkdf2-bcrypt-examples/
    private static String getSalt() throws NoSuchAlgorithmException, NoSuchProviderException
    {
        //Always use a SecureRandom generator
        SecureRandom sr = new SecureRandom();
        //Create array for salt
        byte[] salt = new byte[16];
        //Get a random salt
        sr.nextBytes(salt);
        //return salt
        return salt.toString();
    }

	public String generateOwnerHash(String _phonenmber)
	{
		 String generatedHash = null;
		 try {
	           
		 	String salt = getSalt();
		 	// Create MessageDigest instance for MD5
            MessageDigest md = MessageDigest.getInstance("MD5");
            //Add password bytes to digest
            md.update(salt.getBytes());
            //Get the hash's bytes
            byte[] bytes = md.digest(_phonenmber.getBytes());
            //This bytes[] has bytes in decimal format;
            //Convert it to hexadecimal format
            StringBuilder sb = new StringBuilder();
            for(int i=0; i< bytes.length ;i++)
            {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            //Get complete hashed password in hex format
            generatedHash = sb.toString();
        }
        catch (NoSuchAlgorithmException e) {
        	// TODO Auto-generated catch block
        	e.printStackTrace();
        } catch (NoSuchProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return generatedHash;
	}
	
	// ----------------------------------------------------------
	// Data Functions
	public void turnOffAllDataCapture(){
    	turnOffGPSDataCapture();
    	turnOffMotionDataCapture();
    	turnOffLightDataCapture();
    	turnOffPressureDataCapture();
	}
	
	public void turnOnGPSDataCapture() {
		if (numberOfSavedDataPoints < maxDataPoints) {   //There is room for more points. 
			
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
			isCapturingGPSData = true;
	
			updateGPSViewStateStyle();
			
		}
		else
		{
			updateUI_DataFull();
		}
	}

	public void turnOffGPSDataCapture() {
		
		isCapturingGPSData = false;
		locationManager.removeUpdates(locationListener);
		tbtn_gps.setChecked(false);
		updateGPSViewStateStyle();
		
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
		tbtn_motion.setChecked(false);
		motiontable.setBackgroundColor(Color.argb(255, 214, 214, 214));
		
	}
	
	public void turnOnLightDataCapture() {
		if (numberOfSavedDataPoints < maxDataPoints) {   //There is room for more points. 
			
			isCapturingLightData = true;
			sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
			setTextViewStyle_Active(txtview_light);
			
			lighttable.setBackgroundColor(Color.argb(255, 157, 255, 208));
			
		}
		else
		{
			
			updateUI_DataFull();
		}
	}

	public void turnOffLightDataCapture() {
		isCapturingLightData = false;
		sensorManager.unregisterListener(this,lightSensor);
		setTextViewStyle_Inactive(txtview_light);
		tbtn_light.setChecked(false);
		
		lighttable.setBackgroundColor(Color.argb(255, 214, 214, 214));
		
	}
	
	public void turnOnPressureDataCapture() {
		if (numberOfSavedDataPoints < maxDataPoints) {   //There is room for more points. 
			
			isCapturingPressureData = true;
			sensorManager.registerListener(this, pressureSensor, SensorManager.SENSOR_DELAY_NORMAL);
			setTextViewStyle_Active(txtview_light);
			
			pressuretable.setBackgroundColor(Color.argb(255, 157, 255, 208));
			
		}
		else
		{
			
			updateUI_DataFull();
		}
	}

	public void turnOffPressureDataCapture() {
		isCapturingPressureData = false;
		sensorManager.unregisterListener(this,pressureSensor);
		setTextViewStyle_Inactive(txtview_pressure);
		tbtn_pressure.setChecked(false);
		
		pressuretable.setBackgroundColor(Color.argb(255, 214, 214, 214));
		
	}

	public void packDataPoints() {

		ArrayList<DataPoint> datapoints = db.getAllDataPoints(); 
		
    	JSONObject dataJSONObj = new JSONObject();
    	JSONArray pointsJsonArray =  new JSONArray();
        
    	
    	try {	
        	for (DataPoint  point : datapoints)
			{
				point.setOwner(ownerID);
				
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
				
				pointJsonObject.put("light", point.getBrightness());
				
				pointJsonObject.put("press", point.getPressure());
				
				pointsJsonArray.put( pointJsonObject );
				
			}
			
			dataJSONObj.put("data", pointsJsonArray);
		
        } catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        HttpAsyncTask httptask = new HttpAsyncTask(this);
        httptask.setJsonObjectToPost(dataJSONObj);
        //txtview_httpReult.setText( dataJSONObj.toString());
        httptask.execute(dataPostUrl);
	}
	
	public void clearGPSData() {
		db.clearDataPoints();
		numberOfSavedDataPoints = db.geDataPointCount();
		currentDataPoint.clear();
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
			Toast.makeText(self, "Storing GPS Data on Local Device.", Toast.LENGTH_SHORT).show();
		} else {
			turnOffGPSDataCapture();
			Toast.makeText(self, "No longer storing GPS Data.", Toast.LENGTH_SHORT).show();
		}
	}

	public void onToggleClicked_gatherMotionData(View view) {
		if (((ToggleButton) view).isChecked()) {
			turnOnMotionDataCapture();
			Toast.makeText(self, "Storing Motion Data on Local Device.", Toast.LENGTH_SHORT).show();	
		} else {
			turnOffMotionDataCapture();
			Toast.makeText(self, "No longer storing Motion Data.", Toast.LENGTH_SHORT).show();
		}
	}
	
	public void onToggleClicked_gatherLightData(View view) {
		if (((ToggleButton) view).isChecked()) {
			turnOnLightDataCapture();
			Toast.makeText(self, "Storing Light Data on Local Device.", Toast.LENGTH_SHORT).show();
		} else {
			turnOffLightDataCapture();
			Toast.makeText(self, "No longer storing Light Data.", Toast.LENGTH_SHORT).show();
		}
	}
	
	public void onToggleClicked_gatherPressureData(View view) {
		if (((ToggleButton) view).isChecked()) {
			turnOnPressureDataCapture();
			Toast.makeText(self, "Storing Pressure Data on Local Device.", Toast.LENGTH_SHORT).show();
		} else {
			turnOffPressureDataCapture();
			Toast.makeText(self, "No longer storing Pressure Data.", Toast.LENGTH_SHORT).show();
		}
	}
	
	
	
	public void onClickedSendDataPoints(View view) {
		//So we are not writing to the db while sensitive stuff is happening. 
		turnOffAllDataCapture();
		
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
		//So we are not writing to the db while sensitive stuff is happening. 
		turnOffAllDataCapture();

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

		// durationSeconds = getDurationSeconds(mLastLocation.getTime());
		txtview_CurrentLocationTitle.setText("GPS Location - Current");
		txtview_CurrentLon.setText(Double.toString(currentDataPoint.getLongitude()) + "°");
		txtview_CurrentLat.setText(Double.toString(currentDataPoint.getLatitude()) + "°");
		txtview_CurrentAlt.setText(Double.toString(currentDataPoint.getAltitude()) + "m");
		
		txtview_CurrentTime.setText(time.format("%H:%M:%S %m/%d/%Y"));
		
  		txtview_accelX.setText(Float.toString(currentDataPoint.getAccelx()) + "m/s\u00B2");
  		txtview_accelY.setText(Float.toString(currentDataPoint.getAccely()) + "m/s\u00B2");
  		txtview_accelZ.setText(Float.toString(currentDataPoint.getAccelz()) + "m/s\u00B2");
		txtview_rotationX.setText(Float.toString(currentDataPoint.getRotationx()));
		txtview_rotationY.setText(Float.toString(currentDataPoint.getRotationy()));
		txtview_rotationZ.setText(Float.toString(currentDataPoint.getRotationz()));
		
		txtview_light.setText(Float.toString(currentDataPoint.getBrightness()));
		
		txtview_pressure.setText(Float.toString(currentDataPoint.getPressure())+"hPa");
		
		txtview_PointsSaved.setText("" + numberOfSavedDataPoints);
	}

	public void updateUI_DataFull()
	{
		tbtn_gps.setChecked(false);
		tbtn_motion.setChecked(false);
		tbtn_light.setChecked(false);
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
