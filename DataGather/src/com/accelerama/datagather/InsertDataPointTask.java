package com.accelerama.datagather;

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
	    	if (context.numberOfSavedDataPoints < context.maxDataPoints) {
	    		
	    		dataHasSpace = true; //HACKY AS FUCK
	    		
	    		
				db.addDataPoint(context.currentDataPoint);
				context.numberOfSavedDataPoints = db.getDataPointCount();
				Log.d(TAG, "Inserted >"+context.currentDataPoint.toString());
				context.currentDataPoint.clear(); 
				
				
			} else {
				// TOO MUCH DATA!!!
				// --Turn off capture
				if (context.currentDataPoint.isWritten()  && dataHasSpace) {
					dataHasSpace =  false; 						   
					context.runOnUiThread(new Runnable() {
					     @Override
					     public void run() {
							
					    	context.turnOffAllDataCapture(); 
					    	context.updateUI_DataFull();
					    	

	
					    }
					});
				}
				

			}
    	}

    	
    		
	   }
	}
