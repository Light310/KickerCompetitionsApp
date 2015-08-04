package light.training.kicker1;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SimpleCursorAdapter;

public class TournamentListAdapter extends SimpleCursorAdapter {
	
 final static String LOG_TAG = "myLogs";
 Context ctx;
 LayoutInflater lInflater;
 Cursor c, cursor;
 private int layout;
 DB db;
 MainActivity activity;
 public String[] arrButton, arrName;
 public int [] arrId;
 int count = 0;
 
 
 
 public TournamentListAdapter(Context _context, int _layout, Cursor _cursor, String[] _from, int[] _to) {
	 super(_context, _layout, _cursor, _from, _to);
	 ctx = _context;
	 //layout = _layout;
	 c = _cursor;
     activity = (MainActivity) ctx;     
     db = activity.db;
     /*
     cursor = getCursor();
	 count = cursor.getCount();
	 Log.d(LOG_TAG,"TLA Cursor count = "+count);
     
	 arrButton = new String[count];
	 arrId = new int[count];
	 arrName = new String[count];
     
     if (cursor.moveToFirst()) {			
			int idColIndex = cursor.getColumnIndex("id");
			do {
				arrId[cursor.getPosition()] = cursor.getInt(idColIndex);
				Log.d(LOG_TAG,"TLA arrId["+cursor.getPosition()+"] = "+cursor.getInt(idColIndex));
			}
			while (cursor.moveToNext());			
		}
		else Log.d(LOG_TAG,"TLA No rows in tournament?!");
     	*/
     //this.notifyDataSetChanged();
     
     
 }

	public void updateList() {
		cursor = getCursor();
		count = cursor.getCount();
		Log.d(LOG_TAG,"TLA Cursor count = "+count);

		arrButton = new String[count];
		arrId = new int[count];
		arrName = new String[count];

		if (cursor.moveToFirst()) {
			int idColIndex = cursor.getColumnIndex("id");
			do {
				arrId[cursor.getPosition()] = cursor.getInt(idColIndex);
				Log.d(LOG_TAG,"TLA arrId["+cursor.getPosition()+"] = "+cursor.getInt(idColIndex));
			}
			while (cursor.moveToNext());
		}
		else Log.d(LOG_TAG,"TLA No rows in tournament?!");
		this.notifyDataSetChanged();
	}
 
 public View getView(int position, View convertView, ViewGroup parent) {
	    // используем созданные, но не используемые view	   
	 	final trnm _trnm;
	 	final int tournament_id;
	 	if (convertView == null) {
	 		_trnm = new trnm();
	 		LayoutInflater inflater = activity.getLayoutInflater();
	 		convertView = inflater.inflate(R.layout.active_tournament_item, null);
	 		_trnm.btnTournament = (Button) convertView.findViewById(R.id.barActiveTournament);
	 		
	 		convertView.setTag(_trnm);
	 	} else {
	 		_trnm = (trnm) convertView.getTag();
	 	}
	 	
	 	_trnm.ref = position;	 	
	 		 	
	 	_trnm.btnTournament.setText(""+arrId[position]);
	 	tournament_id = arrId[position];
	 	//Log.d(LOG_TAG,"TLA getView position: "+position+". Id = "+arrId[position]);
	 	_trnm.btnTournament.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {	
				Log.d(LOG_TAG,"TLA onClick launching load id = "+tournament_id);
			    activity.onClkStartTournament("load", tournament_id);
			}
		});
	 	
	 	return convertView;
	  }
 
 /*
 
 public void printResults() {
	 for(int i=0;i<count;i++) {
		 Log.d("results", "Res: "+arrPlayer1[i]+" "+arrScore1[i]+" : "+arrScore2[i]+" "+arrPlayer2[i]);
	 }
 }
*/
 
 public class trnm {
	 String name;
	 int ref, id;	 
	 Button btnTournament;	 
 }

}
