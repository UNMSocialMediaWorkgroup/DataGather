package com.accelerama.datagather;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import com.accelerama.datagather.R;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

public class DataViewActivity extends Activity {
	private static final String TAG = ">>Data View";
	private Activity self = this;

	private DatabaseHandler db;

	private ArrayList<DataPoint> gpsPointList;
	private ListView listview;
	private DataPointArrayAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gpsdataview);


		db = new DatabaseHandler(this);
		gpsPointList = db.getAllDataPoints();

		listview = (ListView) findViewById(R.id.listview);
		adapter = new DataPointArrayAdapter(this, gpsPointList);
		listview.setAdapter(adapter);

		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, final View view,int position, long id) {
				//final String item = (String) parent.getItemAtPosition(position);
				//Java can't cast the Object, a DataPoint object, to a String. *crashes!*
				final String item = parent.getItemAtPosition(position).toString();

				//System.out.println("TODO: make this do something legit! Has item string: " + item);
			}

		});
	}

	private class DataPointArrayAdapter extends BaseAdapter {

		private final Context context;
		private LayoutInflater inflater;
		private ArrayList<DataPoint> gpsPointList;
		private Time time = new Time();



		public DataPointArrayAdapter(Context _context, ArrayList<DataPoint> _gpsPointList) {
			super();
			context = _context;
			gpsPointList = _gpsPointList;
			inflater = LayoutInflater.from(context);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = inflater.inflate (R.layout.list_item_gpspoint, parent, false);

				//This assumes layout/row_left.xml includes a TextView with an id of "textview"
				convertView.setTag (R.id.gpsDataPoint_list_item_DataTextView, convertView.findViewById(R.id.gpsDataPoint_list_item_DataTextView));
			}


			TextView data = (TextView) convertView.getTag(R.id.gpsDataPoint_list_item_DataTextView);
			time.set(gpsPointList.get(position).getTime());

			//TODO: Include the Locale as the first argument to String, for i18n purposes
			String longitude = String.format("%.2f", gpsPointList.get(position).getLongitude());
			String latitude  = String.format("%.2f", gpsPointList.get(position).getLatitude());
			String altitude  = String.format("%.2f", gpsPointList.get(position).getAltitude());

			data.setText(time.format("  %m/%d/%y %H:%M:%S [ "+longitude+"° "+latitude+"° "+altitude+"m ]"));

			//This string is just the current time, wrapped around the gpsPointList's DataPoint toString() output, ending after the altitude, where accel data begins.
			//Let's make this easier then: (possible incomplete remedies in DataPoint left commented out for now)

			//TODO: make this capable of providing general sensor data in addition to (or instead of) just GPS data. (perhaps allowing the user to pick which bits are displayed?)

			//data.setText(" >"+longitude+"� "+latitude+"� "+altitude+"m");
			//data.setText("0.0� 0.0� 0.0m");

			return convertView;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return  gpsPointList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return gpsPointList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

	}

}
