package light.training.kicker1;

import java.util.Stack;

import android.annotation.SuppressLint;
import android.app.FragmentTransaction;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.widget.CheckBox;
import android.widget.TextView;

public class MainActivity extends FragmentActivity {

  Scoreboard frScoreboard;
  Initial frInitial;
  TournamentMainFragment frTournament;
  OldTournamentsFragment frOldTournament;
  TextView tv;
  Stack<String> backstack;
  
  public FragmentTransaction fTrans;
  
  private GestureDetector gestureDetector;

  DB db;
  
  final String LOG_TAG = "myLogs";
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    
    frScoreboard = new Scoreboard();
    frInitial = new Initial();
    frTournament = new TournamentMainFragment();
    frOldTournament = new OldTournamentsFragment();
    
    Log.d(LOG_TAG,"MA scoreboard = "+getResources().getIdentifier("frScoreboard", "id", getPackageName())+
    		" tournament = "+getResources().getIdentifier("frTournament", "id", getPackageName())+
    			" initial = "+getResources().getIdentifier("frInitial", "id", getPackageName()));
    backstack = new Stack<String>();
    
    db = new DB(this);   
    db.open();
    
    gestureDetector = new GestureDetector(new SwipeGestureDetector());
    
    fTrans = getFragmentManager().beginTransaction();
    fTrans.add(R.id.frTournament, frTournament);
    backstack.push("frTournament");
    fTrans.commit();  
  }  
  
  @SuppressLint("NewApi") public void onClickNewTournament() {
	  fTrans = getFragmentManager().beginTransaction();
	  fTrans.addToBackStack(null);
	  fTrans.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, 0, 0);
	  fTrans.remove(frTournament);
	  preparePlayerLinks();
	  fTrans.add(R.id.frInitial, frInitial);
	  backstack.push("frInitial");
      fTrans.commit(); 
  }
  
  public void preparePlayerLinks() {
	  db.execSQL("drop table if exists tmp_pl_x_trnm_lnk;");
	  db.execSQL("create table tmp_pl_x_trnm_lnk ("    			
  			+ "tournament_id integer, "	    			
  			+ "player_id integer" + ");"	
  		);	  
	  db.execSQL("insert into tmp_pl_x_trnm_lnk (player_id) " +
	  		"select player_id from player_x_tournament_link " +
	  		"where tournament_id = (select max(tournament_id) from player_x_tournament_link);");
	  //WTF
	  //db.execSQL("insert into tmp_pl_x_trnm_lnk (player_id) values (1);");
  }
  
  @SuppressLint("NewApi") public void onClkOldTournaments() {
	  fTrans = getFragmentManager().beginTransaction();
	  fTrans.addToBackStack(null);
	  fTrans.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, 0, 0);
	  fTrans.remove(frTournament);
	  preparePlayerLinks();
	  fTrans.add(R.id.frOldTournament, frOldTournament);
	  backstack.push("frOldTournament");
      fTrans.commit(); 
  }
  
  @SuppressLint("NewApi") public void onClkStartTournament (String mode, int id) {
  		CheckBox chbRecountOnline = (CheckBox) findViewById(R.id.onlineTournamentCheckbox);
  		try {
  			frScoreboard.recountOnline = chbRecountOnline.isChecked();
  		} catch (NullPointerException e) {
  			Log.d(LOG_TAG, "MA could not find recountOnline. Making it false.");
  			frScoreboard.recountOnline = false;
  		}
		fTrans = getFragmentManager().beginTransaction();
		fTrans.addToBackStack(null);
		fTrans.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, 0, 0);		
		if (frInitial.isVisible()) {
			Log.d(LOG_TAG,"MA removing frInitial");
			fTrans.remove(frInitial);
		}
		if (frTournament.isVisible()) {
			Log.d(LOG_TAG,"MA removing frTournament");
			fTrans.remove(frTournament);
		}
		if (frOldTournament.isVisible()) {
			Log.d(LOG_TAG,"MA removing frOldTournament");
			fTrans.remove(frOldTournament);
		}
		
		Tournament trn1;
		if (mode.equals("new")) {
			trn1 = new Tournament(1, "Ololo",db);		
			getPlayerXTournamentLink(trn1.id, "new");
		} else {
			/**int tournament_id = db.getIntValue("select max(id) as id from tournaments", "id");*/
			int tournament_id = id;
			trn1 = new Tournament(tournament_id,db);
			getPlayerXTournamentLink(trn1.id, "load");
		}
		frScoreboard.tournament_id = trn1.id;
		frScoreboard.tournament_name = trn1.name;
		frScoreboard.tournament_type = trn1.type;
		prepareScoreboard(trn1.id);
		printLink();
		fTrans.add(R.id.frScoreboard, frScoreboard);
		backstack.push("frScoreboard");
	    fTrans.commit();	  	
} 
  
  public void printLink() {
	  Cursor c = db.rawQuery("select * from tmp_pl_x_trnm_lnk;", null);
	  if (c.moveToFirst()) {
			int tournamentIdColIndex = c.getColumnIndex("tournament_id");
			int playerIdColIndex = c.getColumnIndex("player_id");			
			do {
			Log.d(LOG_TAG,
				"MA printLink. tournament_id = " + c.getInt(tournamentIdColIndex) +
				", player_id = " + c.getInt(playerIdColIndex));
			}
			while (c.moveToNext());
		}
		else Log.d(LOG_TAG, "MA printLink 0 rows");
		c.close();
	  c = db.rawQuery("select * from tournament;", null);
	  if (c.moveToFirst()) {
		  int idColIndex = c.getColumnIndex("id");
		  int tournament_idColIndex = c.getColumnIndex("tournament_id");
		  int player1_idColIndex = c.getColumnIndex("player1_id");
		  int player2_idColIndex = c.getColumnIndex("player2_id");
		  do {
			  Log.d(LOG_TAG,
					  "MA. player1_id = " + c.getInt(player1_idColIndex) +
							  ", player2_id = " + c.getInt(player2_idColIndex) +
							  ", id = " + c.getInt(idColIndex) +
							  ", tournament_id = " + c.getInt(tournament_idColIndex));
		  }
		  while (c.moveToNext());
	  }
	  else Log.d(LOG_TAG, "0 rows");
	  c.close();
  }
  
  public void getPlayerXTournamentLink(int tournament_id, String mode){
	  
	  if (mode.equals("new")) {		
		  Log.d(LOG_TAG,"MA creating players x tournament link");
		/* Здесь надо будет заменить на выбранных игроков, а не всех активных */		  
		  db.execSQL("update tmp_pl_x_trnm_lnk set tournament_id = " + tournament_id+";");		 
		  db.execSQL("insert into player_x_tournament_link select * from tmp_pl_x_trnm_lnk;");
	  } else {
		  Log.d(LOG_TAG,"MA loading players x tournament link");
		  //db.execSQL("delete from player_x_tournament_link where tournament_id = " + tournament_id+";");
		  db.execSQL("delete from tmp_pl_x_trnm_lnk;");
		  db.execSQL("insert into tmp_pl_x_trnm_lnk select * from player_x_tournament_link where tournament_id = " + tournament_id+";"); 
	  }
  }
  
  public void prepareScoreboard(int tournament_id) {
		Log.d(LOG_TAG,"MA Prepare Scoreboard");		
		db.execSQL("delete from tournament;");
		db.execSQL("insert into tournament select * from matches where tournament_id = "+tournament_id+";");
	}
    
  	protected void onDestroy() {
	    super.onDestroy();
	    // закрываем подключение при выходе
	    db.close();
	  }
  	
  	@SuppressLint("NewApi") @Override
  	public void onBackPressed() {  		
  		String last = backstack.pop();
  		if (backstack.empty()) {
  			super.onBackPressed();
  		} else {	
	  		String prev = backstack.pop();
	  		backstack.push(prev);
	  		
	  		fTrans = getFragmentManager().beginTransaction();
	  		fTrans.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, 0, 0);
	  		Log.d(LOG_TAG,"MA Value Prev: "+prev+", Value Last: "+last);
	  		int prev_id = getResources().getIdentifier(prev, "id", getPackageName());
	  		int last_id = getResources().getIdentifier(last, "id", getPackageName());
	  		Log.d(LOG_TAG,"MA prev_id: "+prev_id+", last_id: "+last_id);
	  		Log.d(LOG_TAG,"MA scoreboard = "+getResources().getIdentifier("frScoreboard", "id", getPackageName())+
	  	    		" tournament = "+getResources().getIdentifier("frTournament", "id", getPackageName())+
	  	    			" initial = "+getResources().getIdentifier("frInitial", "id", getPackageName()));
	  		
	  		fTrans.remove(getFragmentManager().findFragmentById(last_id));
	  	    fTrans.add(prev_id, getFragmentManager().findFragmentById(prev_id));  	    
	  		fTrans.commit();  
  		}
  	}  	
  	
  	 @Override
  	  public boolean onTouchEvent(MotionEvent event) {
  	    if (gestureDetector.onTouchEvent(event)) {
  	      return true;
  	    }
  	    return super.onTouchEvent(event);
  	  }

  	  private void onLeftSwipe() {
  		Log.d(LOG_TAG, "Tournament Main: "+frTournament.isVisible()+". Scoreboard: "+frScoreboard.isVisible()+". frInitial: "+frInitial.isVisible());
  		if (frTournament.isVisible()) {
  			frTournament.onLeftSwipe();  		
  		} else if (frInitial.isVisible()) {
  			frInitial.onLeftSwipe();
  		} else if (frScoreboard.isVisible()) {
  			frScoreboard.onLeftSwipe();
  		}
  		Log.d(LOG_TAG, "Left Swipe");
  	  }

  	  private void onRightSwipe() {
  		if (frTournament.isVisible()) {  			
  		} else onBackPressed();
  		Log.d(LOG_TAG, "Right Swipe");
  	  }

  	  // Private class for gestures
  	  private class SwipeGestureDetector 
  	          extends SimpleOnGestureListener {
  	    // Swipe properties, you can change it to make the swipe 
  	    // longer or shorter and speed
  	    private static final int SWIPE_MIN_DISTANCE = 120;
  	    private static final int SWIPE_MAX_OFF_PATH = 200;
  	    private static final int SWIPE_THRESHOLD_VELOCITY = 200;

  	    @Override
  	    public boolean onFling(MotionEvent e1, MotionEvent e2,
  	                         float velocityX, float velocityY) {
  	      try {
  	        float diffAbs = Math.abs(e1.getY() - e2.getY());
  	        float diff = e1.getX() - e2.getX();

  	        if (diffAbs > SWIPE_MAX_OFF_PATH)
  	          return false;

  	        // Left swipe
  	        if (diff > SWIPE_MIN_DISTANCE
  	        && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
  	        	MainActivity.this.onLeftSwipe();
  	        	

  	        // Right swipe
  	        } else if (-diff > SWIPE_MIN_DISTANCE
  	        && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
  	        	MainActivity.this.onRightSwipe();
  	        	
  	        }
  	      } catch (Exception e) {
  	        Log.d(LOG_TAG, "Error on gestures");
  	      }
  	      return false;
  	    }
  	  }
  	

  
  	
}












