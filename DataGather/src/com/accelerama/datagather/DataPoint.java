package com.accelerama.datagather;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import android.util.Base64;

import com.accelerama.acceleration.AccelerationCollection;
import com.accelerama.acceleration.AccelerationCompressor;
import com.accelerama.acceleration.AccelerationPoint;
import com.accelerama.light.LightCompressor;

public class DataPoint {

	//private vars
	private double  longitude = 0.0f;
	private double  latitude  = 0.0f;
	private double  altitude  = 0.0f;
	private long    time     = 0;
	
	private float 	accelx_current = 0.0f;
	private float 	accely_current = 0.0f;
	private float 	accelz_current = 0.0f;
	private AccelerationCollection accel_values = new AccelerationCollection();
	private String	accel_compressedstring  = "";
	
	
	private float 	rotationx = 0.0f;
	private float 	rotationy = 0.0f;
	private float 	rotationz = 0.0f;
	
	private float 	         brightness_current = 0.0f;
	private ArrayList<Float> brightness_values  = new ArrayList<Float>();
	private String 			 brightness_compressedstring  = "";
	
	
	private float 	pressure = 0.0f;
	
	boolean written = false;
	
	String owner = "";
	
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getAltitude() {
		return altitude;
	}
	public void setAltitude(double altitude) {
		this.altitude = altitude;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	
	
	//----Accel
	public void setAccelXYZ(float[] values, long timestamp) {
		this.accelx_current = values[0];
		this.accely_current = values[1];
		this.accelz_current = values[2];
		setTime(timestamp);
		AccelerationPoint accel_point = new AccelerationPoint(values, timestamp);
		this.accel_values.addPoint(accel_point);
	}	
	public float getCurrentAccelX() {
		return accelx_current;
	}	
	public float getCurrentAccelY() {
		return accely_current;
	}	
	public float getCurrentAccelZ() {
		return accelz_current;
	}
	public String compressAccelValues()
	{
		if(this.accel_values.size() > 0)
		{
			ByteArrayOutputStream compressed_bytes = new ByteArrayOutputStream();
			AccelerationCompressor accelcompressor = AccelerationCompressor.NybbleDownsamplingGZip;
			try {
				accelcompressor.write(compressed_bytes, this.accel_values);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			accel_compressedstring = Base64.encodeToString(compressed_bytes.toByteArray(), Base64.DEFAULT);
		}
		else
		{
			accel_compressedstring = "";
			
		}
		
		return accel_compressedstring;

	}
	public String getCompressedAccelString()
	{
		return accel_compressedstring;
	}
	public void setCompressedAccelString(String _accel_compressedstring)
	{
		accel_compressedstring = _accel_compressedstring;
	}
	
	
	//----Rotation
	public float getRotationx() {
		return rotationx;
	}
	public void setRotationx(float rotationx) {
		this.rotationx = rotationx;
	}
	public float getRotationy() {
		return rotationy;
	}
	public void setRotationy(float rotationy) {
		this.rotationy = rotationy;
	}
	public float getRotationz() {
		return rotationz;
	}
	public void setRotationz(float rotationz) {
		this.rotationz = rotationz;
	}
	
	//----Brightness
	public float getCurrentBrightness() {
		return brightness_current;
	}
	public ArrayList<Float> getBrightnessValues() {
		return brightness_values;
	}
	public void setBrightness(float brightness) {
		this.brightness_current = brightness;
		this.brightness_values.add(brightness);
	}
	public String compressBrightnessValues() {
		 
		if(this.brightness_values.size() > 0)
		{
			ByteArrayOutputStream compressed_bytes = new ByteArrayOutputStream();
			LightCompressor  lightcompressor = LightCompressor.NybbleDownsamplingGZIP;
			try {
				
				lightcompressor.write(compressed_bytes, this.brightness_values);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			brightness_compressedstring = Base64.encodeToString(compressed_bytes.toByteArray(), Base64.DEFAULT);
		}
		else
		{
			brightness_compressedstring ="";
		}
		return brightness_compressedstring;
		
	}
	public String getCompressedBrightnessString()
	{
		return brightness_compressedstring;
	}
	public void setCompressedBrightnessString(String _brightness_compressedstring)
	{
		brightness_compressedstring = _brightness_compressedstring;
	}
	
	
	
	public float getPressure() {
		return pressure;
	}
	public void setPressure(float pressure) {
		this.pressure = pressure;
	}
	public void clear()
	{
		longitude = 0.0f;
		latitude  = 0.0f;
		altitude  = 0.0f;
		
		time     = 0;
			
		accelx_current = 0.0f;
		accely_current = 0.0f;
		accelz_current = 0.0f;
		accel_values.clear();
		accel_compressedstring = "";
		
		rotationx = 0.0f;
		rotationy = 0.0f;
		rotationz = 0.0f;
		
		brightness_current = 0.0f;
		brightness_values.clear();
		brightness_compressedstring  = "";
		
		pressure = 0.0f;
		
		written = false; 
	}
	
	
	
	
	
	
	public boolean isWritten() {
		return written;
	}
	public void setWritten() {
		this.written = true;
	}
	
	
	@Override
	public String toString() {
		return "[lon:" + longitude + ",lat:" + latitude + ",alt:" + altitude + "\n"+
				"accel:"+accelx_current+","+accely_current+","+accelz_current+"\n"+ 
				"rot:"+rotationz+","+rotationy+","+rotationz+"\n"+
				"light:"+brightness_current+"\n"+
				"pressure:"+pressure+"\n"+
				"time:" + time + ",owner:"+owner+"]";
				
	}
	
	
	
}
