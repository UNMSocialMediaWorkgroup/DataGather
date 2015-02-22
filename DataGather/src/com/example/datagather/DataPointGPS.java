package com.example.datagather;

import org.json.JSONException;
import org.json.JSONObject;

public class DataPointGPS {

	//private vars
	double  longitude = 0.0f;
	double  latitude  = 0.0f;
	double  altitude  = 0.0f;
	long     time     = 0;
	
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
	
	/*
	public JSONObject getJsonObject() throws JSONException
	{
		JSONObject jsonGPSpoint = new JSONObject();
		jsonGPSpoint.accumulate("lon", longitude);
		jsonGPSpoint.accumulate("lat", latitude);
		jsonGPSpoint.accumulate("alt", altitude);
		jsonGPSpoint.accumulate("time", time);
		return jsonGPSpoint;
		
	}
	*/
	
	@Override
	public String toString() {
		return "{\\\"lon\\\":" + longitude + ",\\\"lat\\\":" + latitude + ",\\\"alt\\\":" + altitude + ",\\\"time\\\":" + time + ",\\\"owner\\\": \\\"+"+owner+"\\\" }";
	}
	
	
	
}
