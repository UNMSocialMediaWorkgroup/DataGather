package com.example.datagather;

import org.json.JSONException;
import org.json.JSONObject;

public class DataPoint {

	//private vars
	private double  longitude = 0.0f;
	private double  latitude  = 0.0f;
	private double  altitude  = 0.0f;
	private long    time     = 0;
	private float 	accelx = 0.0f;
	private float 	accely = 0.0f;
	private float 	accelz = 0.0f;
	private float 	rotationx = 0.0f;
	private float 	rotationy = 0.0f;
	private float 	rotationz = 0.0f;
	private float 	brightness = 0.0f;
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
	
	
	public float getAccelx() {
		return accelx;
	}
	public void setAccelx(float accelx) {
		this.accelx = accelx;
	}
	public float getAccely() {
		return accely;
	}
	public void setAccely(float accely) {
		this.accely = accely;
	}
	public float getAccelz() {
		return accelz;
	}
	public void setAccelz(float accelz) {
		this.accelz = accelz;
	}
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
	
	public float getBrightness() {
		return brightness;
	}
	public void setBrightness(float brightness) {
		this.brightness = brightness;
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
		accelx = 0.0f;
		accely = 0.0f;
		accelz = 0.0f;
		rotationx = 0.0f;
		rotationy = 0.0f;
		rotationz = 0.0f;
		brightness = 0.0f;
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
				"accel:"+accelx+","+accely+","+accelz+"\n "+ 
				"rot:"+rotationz+","+rotationy+","+rotationz+"\n"+
				"light:"+brightness+"\n"+
				"pressure:"+pressure+"\n"+
				"time:" + time + ",owner:"+owner+"]";
				
	}
	
	
	
}
