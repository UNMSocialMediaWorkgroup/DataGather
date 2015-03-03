package com.example.datagather;

import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.widget.Toast;

public class InsertDataPointTask extends TimerTask {
	private static final String TAG = ">>TIMER";
	private DatabaseHandler db;
	public DataPoint currentDataPoint;
    public MainActivity context;
    
    private boolean dataHasSpace = true; 
	
	public InsertDataPointTask( DatabaseHandler _db, Context _context) {
		db = _db;
		context = (MainActivity)_context; 
		
    }  
	   
    @Override
	public void run() {
		   //Toast.makeText(context, "TimerTick!", Toast.LENGTH_SHORT).show();
    	
    	if( context.currentDataPoint.isWritten() ){
	    	if (context.numberOfSavedDataPoints <= context.maxDataPoints) {
	    		
	    		dataHasSpace = true; //HACKY AS FUCK
	    		
	    		
				db.addDataPoint(context.currentDataPoint);
				context.numberOfSavedDataPoints = db.geDataPointCount();
				Log.d(TAG, " Inserted>"+context.currentDataPoint.toString());
				context.currentDataPoint.clear(); 
				
				
			} else {
				// TOO MUCH DATA!!!
				// --Turn off capture
				if ((context.isCapturingGPSData || context.isCapturingMotionData) && dataHasSpace) {
					dataHasSpace =  false; 						   //BUT THIS DOES!! WTF
					context.runOnUiThread(new Runnable() {
					     @Override
					     public void run() {
							context.isCapturingGPSData    = false;//this doesn't work for some reason......
							context.isCapturingMotionData = false;//this doesn't work for some reason......
									 
					    	context.turnOffGPSDataCapture();
					    	context.turnOffMotionDataCapture();
					    	context.updateUI_DataFull();
					    	

	
					    }
					});
				}
				

			}
    	}

    	
    		
	   }
	}
