package light.training.kicker1;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
 
public class ScoreboardAdapter extends SimpleCursorAdapter {
	
 final static String LOG_TAG = "myLogs";
 Context ctx;
 LayoutInflater lInflater;
 Cursor c, cursor;
 private int layout;
 DB db;
 MainActivity activity;
 Scoreboard sc;
 public String[] arrPlayer1, arrPlayer2, arrScore1, arrScore2;
 int count = 0;
 int tournament_id;
 
 
 
 public ScoreboardAdapter(Context _context, int _layout, Cursor _cursor, String[] _from, int[] _to, Scoreboard _sc) {
	 super(_context, _layout, _cursor, _from, _to);
	 ctx = _context;
	 //layout = _layout;
	 c = _cursor;
     sc = _sc;
     activity = (MainActivity) ctx;     
     db = activity.db;
 }
 
 public void updateCursor() {
	 cursor = getCursor();
	 count = cursor.getCount();
	 tournament_id = sc.tournament_id;
	 Log.d(LOG_TAG,"SCA Cursor count = "+count);
     
     arrPlayer1 = new String[count];
     arrPlayer2 = new String[count];
     arrScore1 = new String[count];
     arrScore2 = new String[count];
     
     if (cursor.moveToFirst()) {			
			int player1ColIndex = cursor.getColumnIndex("player1");
			int player2ColIndex = cursor.getColumnIndex("player2");
			int score1ColIndex = cursor.getColumnIndex("score1");
			int score2ColIndex = cursor.getColumnIndex("score2");
			do {
				arrPlayer1[cursor.getPosition()] = cursor.getString(player1ColIndex);
				arrPlayer2[cursor.getPosition()] = cursor.getString(player2ColIndex);
				arrScore1[cursor.getPosition()] = cursor.getString(score1ColIndex);
				arrScore2[cursor.getPosition()] = cursor.getString(score2ColIndex);
				Log.d(LOG_TAG,"SCA arrPlayer1["+cursor.getPosition()+"] = "+cursor.getString(player1ColIndex)+". arrPlayer2["+cursor.getPosition()+"] = "+
						cursor.getString(player2ColIndex)+". Score: "+arrScore1[cursor.getPosition()]+" : "+arrScore2[cursor.getPosition()]);
			}
			while (cursor.moveToNext());			
		}
		else Log.d(LOG_TAG,"SCA No rows in tournament?!");
     	//cursor.close();
     this.notifyDataSetChanged();
 }
 
 
 public View getView(int position, View convertView, ViewGroup parent) {
	    // используем созданные, но не используемые view	   
	 	final game _game;
	 	if (convertView == null) {
	 		_game = new game();
	 		LayoutInflater inflater = activity.getLayoutInflater();
	 		convertView = inflater.inflate(R.layout.draw_item, null);
	 		_game.tvPlayer1 = (TextView) convertView.findViewById(R.id.tvDrawPlayer1);
	 		_game.tvPlayer2 = (TextView) convertView.findViewById(R.id.tvDrawPlayer2);
	 		_game.etScore1 = (EditText) convertView.findViewById(R.id.etPlayer1Score);
	 		_game.etScore2 = (EditText) convertView.findViewById(R.id.etPlayer2Score);
	 		
	 		convertView.setTag(_game);
	 	} else {
	 		_game = (game) convertView.getTag();
	 	}
	 	
	 	_game.ref = position;	 	
	 		 	
	 	_game.tvPlayer1.setText(arrPlayer1[position]);
	 	_game.tvPlayer2.setText(arrPlayer2[position]);
	 	_game.etScore1.setText(arrScore1[position]);
	 	_game.etScore2.setText(arrScore2[position]);
	 	
	 	_game.etScore1.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {			
			}			
			@Override
			public void afterTextChanged(Editable s) {
				if (s.toString().equals(arrScore1[_game.ref])) {
					Log.d(LOG_TAG,"SCA duplicate entry, aborting update");
				} else 
				{
					arrScore1[_game.ref] = s.toString();
						if (sc.recountOnline) {
						  try{           
					            if (!s.toString().equals("") && s.toString()!=null) {
					            	Log.d(LOG_TAG,"SCA Connection open. Value for update: "+Integer.parseInt(s.toString()));
									//db.open();	
					            	int player1_id = db.getIntValue("select id from players where name = '"+arrPlayer1[_game.ref]+"';", "id");
									int player2_id = db.getIntValue("select id from players where name = '"+arrPlayer2[_game.ref]+"';", "id");
									/*String sqlText = "update games set score1 ='"+s.toString()+"' where player1 = '"+
											arrPlayer1[_game.ref]+"' and player2 = '"+arrPlayer2[_game.ref]+"' and tournament_id = "+tournament_id+";";*/
									String sqlText = "update games set score1 ='"+s.toString()+"' where player1_id = "+
											player1_id+" and player2_id = "+player2_id+" and tournament_id = "+tournament_id+";";
							        db.execSQL(sqlText);
							        //db.close();	
							        sc.requeryLoader(1);
							        sc.checkAndFinishTournament();
					            }
							//printResults();
							} catch(NumberFormatException e){
					        	Log.d(LOG_TAG,"SCA Not parsable");
					        }
						}
				   }	
			}
		});
	 	
	 	_game.etScore2.addTextChangedListener(new TextWatcher() {			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {			
			}			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {		
			}
			/* WTF need to change afterTextChanged to work not with tournament_id!!! */
			@Override
			public void afterTextChanged(Editable s) {
				if (s.toString().equals(arrScore2[_game.ref])) {
					Log.d(LOG_TAG,"SCA duplicate entry, aborting update");
				} else 
				{
					arrScore2[_game.ref] = s.toString();					
						if (sc.recountOnline) {
							try{	          
						        if (!s.toString().equals("") && s.toString()!=null) {
						        	Log.d(LOG_TAG,"SCA Connection open. Value for update: "+Integer.parseInt(s.toString()));
									//db.open();	
						        	int player1_id = db.getIntValue("select id from players where name = '"+arrPlayer1[_game.ref]+"';", "id");
									int player2_id = db.getIntValue("select id from players where name = '"+arrPlayer2[_game.ref]+"';", "id");
									/*String sqlText = "update games set score2 ='"+s.toString()+"' where player1 = '"+
											arrPlayer1[_game.ref]+"' and player2 = '"+arrPlayer2[_game.ref]+"' and tournament_id = "+tournament_id+";";*/
									String sqlText = "update games set score2 ='"+s.toString()+"' where player1_id = "+
											player1_id+" and player2_id = "+player2_id+" and tournament_id = "+tournament_id+";";
							        db.execSQL(sqlText);
							        //db.close();	
							        sc.requeryLoader(1);	
							        sc.checkAndFinishTournament();
						        }
							//printResults();
							} catch(NumberFormatException e){
					        	Log.d(LOG_TAG,"SCA Not parsable");
					        }
						 }
				}
			}
		});
	 	
	 	return convertView;
	  }
 
 
 
 public void printResults() {
	 for(int i=0;i<count;i++) {
		 Log.d("results", "Res: "+arrPlayer1[i]+" "+arrScore1[i]+" : "+arrScore2[i]+" "+arrPlayer2[i]);
	 }
 }

 
 public class game {
	 String player1,player2;
	 int ref;	 
	 TextView tvPlayer1, tvPlayer2;
	 EditText etScore1, etScore2;
 }

}