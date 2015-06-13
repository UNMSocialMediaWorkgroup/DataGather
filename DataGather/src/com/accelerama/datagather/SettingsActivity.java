package com.accelerama.datagather;



import com.accelerama.datagather.R;













import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;




public class SettingsActivity  extends Activity {
	private static final String TAG = ">>Settings";
	private Activity self = this;
	
	private static final String PREFS_NETWORK = "Network";
	private static final String PREFS_NETWORK_OWNERID = "ownerID";
	private static final String PREFS_NETWORK_POSTURL = "postURL";
	
	//---------------------
	private Button btn_save;
	private Button btn_cancel;
	private EditText etxt_posturl;
	private TextView txt_id;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		
		
		//--UI
		btn_save 	= (Button) findViewById(R.id.btn_settings_save);
		btn_cancel  = (Button) findViewById(R.id.btn_settings_cancel);
		etxt_posturl = (EditText) findViewById(R.id.etxt_settings_posturl);
		txt_id = (TextView) findViewById(R.id.txt_settings_id); 
		
		
		//--Load current settings
		SharedPreferences networkPrefs = getSharedPreferences(PREFS_NETWORK, 0);

		String ownerID = networkPrefs.getString(PREFS_NETWORK_OWNERID, "NO_ID");
		txt_id.setText(ownerID);
		
		String dataPostUrl = networkPrefs.getString(PREFS_NETWORK_POSTURL, "NO_POSTURL");
		etxt_posturl.setText(dataPostUrl);
		
		//vvvvvvv Text color change on modified Not Working vvvvvv
//		etxt_posturl.setOnFocusChangeListener(new OnFocusChangeListener() {          
//
//	        public void onFocusChange(View v, boolean hasFocus) {
//	            
//	        	EditText edittxt_posturl = (EditText)v;
//	        	
//	        	if(!hasFocus) {
//	        		edittxt_posturl.setTextColor(Color.argb(255, 0, 144, 255));
//	            }
//	        	else
//	        	{
//	        		edittxt_posturl.setTextColor(Color.argb(255, 67, 255, 213));
//	        	}
//	        }
//	    });
		//^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
		
	}
	
	
	public void onClickSaveSettings(View view) {
		
		SharedPreferences networkPrefs = getSharedPreferences(PREFS_NETWORK, 0);
		
		String dataPostUrl = etxt_posturl.getText().toString();
		
		SharedPreferences.Editor editor = networkPrefs.edit();
		editor.putString(PREFS_NETWORK_POSTURL,dataPostUrl);
		// Commit the edits!
		editor.commit();
		Log.d(TAG, "Post Url Saved :"+dataPostUrl);
		finish();
		
	}
	
	public void onClickCancel(View view) {
		
		finish();
		
	}
	
	
	
	
	
	
	
	
}
