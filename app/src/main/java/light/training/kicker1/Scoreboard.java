package light.training.kicker1;


import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
//import android.app.LoaderManager.LoaderCallbacks;
//import android.content.CursorLoader;
//import android.content.Loader;
//import android.database.Cursor;
import android.widget.Toast;

public class Scoreboard extends Fragment implements LoaderCallbacks<Cursor> {
	
	int tournament_id, tournament_type, games_per_match;
	String tournament_name;
	
	final static String LOG_TAG = "myLogs";
	MainActivity activity;
	DB db;
	View v;
	Boolean recountOnline;
	ListView lvDraw, lvResult;
	ScoreboardAdapter scAdapter;
	SimpleCursorAdapter scAdapterRes;
	LinearLayout layoutTournament;		
	/*
	final static String sqlQuery = "select t.rowid as _id, p1.name as player1, p2.name as player2, score1, score2 " +
			"from tournament t " +
			"left join (select pl1.* from players pl1 " +
			") p1 " +
			"on t.player1_id = p1.id " +
			"left join (select pl2.* from players pl2 " +
			") p2 " +
			"on t.player2_id = p2.id";*/

	/* WTF just a dummy for matches with 1 game, should be replaced after */
	final static String sqlQuery = "select t.rowid as _id, p1.name as player1, p2.name as player2, g.player1_score as score1, g.player2_score as score2 " +
			"from tournament t " +
			"left join (select pl1.* from players pl1 " +
			") p1 " +
			"on t.player1_id = p1.id " +
			"left join (select pl2.* from players pl2 " +
			") p2 " +
			"on t.player2_id = p2.id " +
			"left join (select * from games) g " +
			"on t.id = g.match_id;";

	final static String sqlResultsQuery = "select player_name as name, rowid as _id, games, wins as won, losses as lost, draws as draw, goals, missed, score, delta from results order by score desc, delta desc;";
	/*
	final static String sqlResultsQuery = "select name, _id, games, won, lost, draw, goals, missed, " +
			"(3*coalesce(won,0) + coalesce(draw,0)) as score, goals-missed as delta from" +
			"(select p.name, p.rowid _id, 0 as score, " +
			"ga.games as games, wo.won as won, lo.lost as lost, dr.draw as draw, " +
			"go.goals as goals, mi.missed as missed, 0 as delta " +
			"from players p "+
			"inner join tmp_pl_x_trnm_lnk lnk on p.id = lnk.player_id "+
			"left join (" +
			"select player1_id as name, sum(sum) as goals from (" +
			"select player1_id, sum(score1) as sum from tournament group by player1_id " +
			"union all select player2_id as player1_id, sum(score2) as sum from tournament group by player2_id) a group by player1_id) go " +
			"on go.name = p.id " +
			"left join (" +
			"select player1_id as name, sum(sum) as missed from (" +
			"select player1_id, sum(score2) as sum from tournament group by player1_id " +
			"union all select player2_id as player1_id, sum(score1) as sum from tournament group by player2_id) b group by player1_id) mi " +
			"on mi.name = p.id "+
			"left join (" +
			"select sum(draw) as draw, player1_id as name from (" +
			"select player1_id, count(*) as draw from tournament " +
			"where score1 is not null and score1!='' and score2 is not null and score2!='' and score1=score2 group by player1_id " +
			"union all select player2_id as player1_id, count(*) as draw from tournament " +
			"where score1 is not null and score1!='' and score2 is not null and score2!='' and score1=score2 group by player2_id " +
			") f group by player1_id) dr " +
			"on dr.name = p.id "+
			"left join (" +
			"select sum(lost) as lost, player1_id as name from (" +
			"select player1_id, count(*) as lost from tournament " +
			"where score1 is not null and score1!='' and score2 is not null and score2!='' and score1<score2 group by player1_id " +
			"union all select player2_id as player1_id, count(*) as lost from tournament " +
			"where score1 is not null and score1!='' and score2 is not null and score2!='' and score1>score2 group by player2_id " +
			") e group by player1_id) lo " +
			"on lo.name = p.id "+
			"left join (" +
			"select sum(won) as won, player1_id as name from (" +
			"select player1_id, count(*) as won from tournament " +
			"where score1 is not null and score1!='' and score2 is not null and score2!='' and score1>score2 group by player1_id " +
			"union all select player2_id as player1_id, count(*) as won from tournament where " +
			"score1 is not null and score1!='' and score2 is not null and score2!='' and score1<score2 group by player2_id " +
			") d group by player1_id) wo " +
			"on wo.name = p.id "+
			"left join (" +
			"select sum(games1)+sum(games2) as games, player1_id as name from (" +
			"select player1_id, count(*) as games1, 0 as games2 from tournament " +
			"where score1 is not null and score1!='' and score2 is not null and score2!='' group by player1_id " +
			"union all select player2_id as player1_id, count(*) as games2, 0 as games1 from tournament " +
			"where score1 is not null and score1!='' and score2 is not null and score2!='' group by player2_id " +
			") c group by player1_id) ga " +
			"on ga.name = p.id ) lol order by score desc, delta desc;";*/
	
	
	
	@Override
	  public View onCreateView(LayoutInflater inflater, ViewGroup container,
	      Bundle savedInstanceState) {
		v = inflater.inflate(R.layout.scoreboard, null);
		activity = (MainActivity) getActivity();
		db = activity.db;		
		//Log.d(LOG_TAG, "SC onCreateView db: "+db);
		Log.d(LOG_TAG, "SC onCreateView. id: "+tournament_id+". Type: "+tournament_type+". Name: "+tournament_name);
				
		//Log.d(LOG_TAG,"Recount online: "+recountOnline);
		Button btnRecount = (Button) v.findViewById(R.id.btnRecount);
		if (recountOnline) {
			btnRecount.setVisibility(View.GONE);
		} else {
			btnRecount.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					reloadDataForResults();
					//debugTables();
					requeryLoader(1);	
					checkAndFinishTournament();
				}
			});
		}
		initialize();
	    return v;
	  }
	
	@Override 
	public void onResume() {
		super.onResume();
		Log.d(LOG_TAG,"SC on resume");
		requeryLoader(0);		
		requeryLoader(1);
	}
	
	public void initialize() {
		Log.d(LOG_TAG,"SC: initializing. GPM = "+games_per_match);
		if (db.selectCount("tournament")==0) {
			Log.d(LOG_TAG,"SC No tournament found, getting draw");
			getDraw();
		} else {
			Log.d(LOG_TAG,"SC found tournament");
		}

		printResults();
		printDraw();
	}
	
	public void printResults() {
		String[] from = new String[] { "name", "score", "games", "won", "lost", "draw", "goals", "missed", "delta" };
	    int[] to = new int[] { R.id.tvResultsPlayer, R.id.tvResultsScores, R.id.tvResultsGames, R.id.tvResultsWon, R.id.tvResultsLost, R.id.tvResultsDraw, R.id.tvResultsGoals, R.id.tvResultsMissed, R.id.tvResultsDelta };

	    scAdapterRes = new SimpleCursorAdapter((Context)activity, R.layout.results_item, null, from, to, 0);
	    lvResult = (ListView) v.findViewById(R.id.lvResults);
	    lvResult.addHeaderView(createHeader(), null, false);
	    
	    lvResult.setAdapter(scAdapterRes);
	    
	    activity.getSupportLoaderManager().initLoader(1, null, this);
	}
	
	View createHeader() {
	      View v = activity.getLayoutInflater().inflate(R.layout.results_item, null);
	      ((TextView)v.findViewById(R.id.tvResultsPlayer)).setText("Player");
	      ((TextView)v.findViewById(R.id.tvResultsScores)).setText("S");
	      ((TextView)v.findViewById(R.id.tvResultsGames)).setText("G");
	      ((TextView)v.findViewById(R.id.tvResultsWon)).setText("W");
	      ((TextView)v.findViewById(R.id.tvResultsLost)).setText("L");
	      ((TextView)v.findViewById(R.id.tvResultsDraw)).setText("D");
	      ((TextView)v.findViewById(R.id.tvResultsGoals)).setText("G");
	      ((TextView)v.findViewById(R.id.tvResultsMissed)).setText("M");
	      ((TextView)v.findViewById(R.id.tvResultsDelta)).setText("D");
	      return v;
	    }
		
	public void printDraw() {
		/* Here should be branching for different ammount of games per match */
		// формируем столбцы сопоставления
		String[] from = new String[] { "player1", "player2", "score1", "score2" };
	    int[] to = new int[] { R.id.tvDrawPlayer1, R.id.tvDrawPlayer2, R.id.etPlayer1Score, R.id.etPlayer2Score };

	    // создааем адаптер и настраиваем список
	    scAdapter = new ScoreboardAdapter((Context)activity, R.layout.draw_item, null, from, to, this);
	    lvDraw = (ListView) v.findViewById(R.id.lvDraw);
	    lvDraw.setAdapter(scAdapter);    
	    
	    // создаем лоадер для чтения данных
	    activity.getSupportLoaderManager().initLoader(0, null, this);			
	}

	public void debugTables() {
		Log.d(LOG_TAG, "SC: debugTables.");

		Cursor c0 = db.rawQuery("select match_id, sum(coalesce(player1_score,0)) as goals1, sum(player2_score) as goals2 from games group by match_id", null);

		if (c0.moveToFirst()) {
			int match_id_ColIndex = c0.getColumnIndex("match_id");
			int goals1_ColIndex = c0.getColumnIndex("goals1");
			int goals2_ColIndex = c0.getColumnIndex("goals2");
			do {
				Log.d(LOG_TAG, "SC: debugTable0. match_id = " + c0.getInt(match_id_ColIndex)+
						", goals1 = " + c0.getInt(goals1_ColIndex)+
						", goals2 = " + c0.getInt(goals2_ColIndex));
			}
			while (c0.moveToNext());
		}
		else Log.d(LOG_TAG, "SC: debugTable0 0 rows");

		c0.close();

		Cursor c = db.rawQuery(
				"select m.id as match_id, player1_id, player2_id, pl.played as played, "
						+ "case when ((coalesce(won1,0)>coalesce(won2,0)) or (coalesce(won1,0)=coalesce(won2,0) and goals1>goals2)) then 3 "
						+ "when (coalesce(won1,0)=coalesce(won2,0) and goals1=goals2 and played>0) then 1 "
						+ "else 0 end as player1_score, "
						+ "case when ((coalesce(won1,0)>coalesce(won2,0)) or (coalesce(won1,0)=coalesce(won2,0) and goals1>goals2)) then 1 "
						+ "else 0 end as player1_wins, "
						+ "case when ((coalesce(won1,0)<coalesce(won2,0)) or (coalesce(won1,0)=coalesce(won2,0) and goals1<goals2)) then 1 "
						+ "else 0 end as player1_lost, "
						+ "case when ((coalesce(won1,0)<coalesce(won2,0)) or (coalesce(won1,0)=coalesce(won2,0) and goals1<goals2)) then 3 "
						+ "when (coalesce(won1,0)=coalesce(won2,0) and goals1=goals2 and played>0) then 1 "
						+ "else 0 end as player2_score, "
						+ "case when ((coalesce(won1,0)<coalesce(won2,0)) or (coalesce(won1,0)=coalesce(won2,0) and goals1<goals2)) then 1 "
						+ "else 0 end as player2_wins, "
						+ "case when ((coalesce(won1,0)>coalesce(won2,0)) or (coalesce(won1,0)=coalesce(won2,0) and goals1>goals2)) then 1 "
						+ "else 0 end as player2_lost, "
						+ "case when (coalesce(won1,0)=coalesce(won2,0) "
						+ "and goals1=goals2 and played>0) then 1 "
						+ "else 0 end as draws, "
						+ "		goals1 as player1_goals, "
						+ "goals2 as player2_goals "
						+ "		from "
						+ "(select * from tournament) m "
						+ "left join "
						+ "(select match_id, count(*) as won1 from games where player1_score>player2_score group by match_id) w1 "
						+ "on m.id = w1.match_id "
						+ "left join "
						+ "(select match_id, count(*) as won2 from games where player2_score>player1_score group by match_id) w2 "
						+ "on m.id = w2.match_id "
						+ "left join "
						+ "(select match_id, count(*) as played from games where player1_score is not null and player1_score!='' and player2_score is not null and player2_score!='' group by match_id) pl "
						+ "on m.id = pl.match_id "
						+ "left join "
						+ "(select match_id, sum(player1_score) as goals1, sum(player2_score) as goals2 from games group by match_id) g "
						+ "on m.id = g.match_id;", null);
		/* Print results */
		if (c.moveToFirst()) {
			int match_id_ColIndex = c.getColumnIndex("match_id");
			int player1_id_ColIndex = c.getColumnIndex("player1_id");
			int player2_id_ColIndex = c.getColumnIndex("player2_id");
			int played_ColIndex = c.getColumnIndex("played");
			int player1_score_ColIndex = c.getColumnIndex("player1_score");
			int player1_wins_ColIndex = c.getColumnIndex("player1_wins");
			int player1_lost_ColIndex = c.getColumnIndex("player1_lost");
			int player2_score_ColIndex = c.getColumnIndex("player2_score");
			int player2_wins_ColIndex = c.getColumnIndex("player2_wins");
			int player2_lost_ColIndex = c.getColumnIndex("player2_lost");
			int draws_ColIndex = c.getColumnIndex("draws");
			int player1_goals_ColIndex = c.getColumnIndex("player1_goals");
			int player2_goals_ColIndex = c.getColumnIndex("player2_goals");
			do {
				Log.d(LOG_TAG, "SC: debugTable. match_id = " + c.getInt(match_id_ColIndex)+
							", player1_id = " + c.getInt(player1_id_ColIndex)+
						", player2_id = " + c.getInt(player2_id_ColIndex)+
						", played = " + c.getInt(played_ColIndex)+
						", player1_score = " + c.getInt(player1_score_ColIndex)+
						", player1_wins = " + c.getInt(player1_wins_ColIndex)+
						", player1_lost = " + c.getInt(player1_lost_ColIndex)+
						", player2_score = " + c.getInt(player2_score_ColIndex)+
						", player2_wins = " + c.getInt(player2_wins_ColIndex)+
						", player2_lost = " + c.getInt(player2_lost_ColIndex)+
						", draws = " + c.getInt(draws_ColIndex)+
						", player1_goals = " + c.getInt(player1_goals_ColIndex)+
						", player2_goals = " + c.getInt(player2_goals_ColIndex));

			}
			while (c.moveToNext());
		}
		else Log.d(LOG_TAG, "SC: debugTable 0 rows");

		c.close();
	}

	
	public void reloadDataForResults() {
		Log.d(LOG_TAG, "SC reloadDataForResults started");


		/* WTF just a dummy for a match with 1 game, will need more processing for more matches */
		for(int i=0;i<scAdapter.count;i++) {
			 Log.d(LOG_TAG, "SC: Res: "+scAdapter.arrPlayer1[i]+" "+scAdapter.arrScore1[i]+" : "+scAdapter.arrScore2[i]+" "+scAdapter.arrPlayer2[i]);
				int player1_id = db.getIntValue("select id from players where name = '"+scAdapter.arrPlayer1[i]+"';", "id");
				int player2_id = db.getIntValue("select id from players where name = '"+scAdapter.arrPlayer2[i]+"';", "id");
				String sqlText1 = "update games set player2_score ='"+scAdapter.arrScore2[i]+"' where match_id in (select id from matches where " +
						" player1_id = "+player1_id+" and player2_id = "+player2_id+" and tournament_id = "+tournament_id+");";
				/*String sqlText1 = "update games set score2 ='"+scAdapter.arrScore2[i]+"' where player1_id = "+
						player1_id+" and player2_id = "+player2_id+" and tournament_id = "+tournament_id+";";*/
			    Log.d(LOG_TAG,"SC "+sqlText1);
		        db.execSQL(sqlText1);
				String sqlText2 = "update games set player1_score ='"+scAdapter.arrScore1[i]+"' where match_id in (select id from matches where " +
						" player1_id = "+player1_id+" and player2_id = "+player2_id+" and tournament_id = "+tournament_id+");";
		        /*String sqlText2 = "update games set score1 ='"+scAdapter.arrScore1[i]+"' where player1_id = "+
		        		player1_id+" and player2_id = "+player2_id+" and tournament_id = "+tournament_id+";";*/
		        db.execSQL(sqlText2);
		 }
	}
	
	public void requeryLoader(int id) {
		Log.d(LOG_TAG, "SC requery loader " + id);
		try {
		activity.getSupportLoaderManager().getLoader(id).forceLoad();
		} catch (NullPointerException e) {
			Log.d(LOG_TAG, "SC Loader "+id+" is null");
		}
	}	
	
	// Делаем жеребьёвку
	public void getDraw() {
		// Get Draw
		// Here must be branching for gpm too
		int playersCount = db.selectCount("tmp_pl_x_trnm_lnk");
		int matchesCount = 0;
		int tmp = playersCount - 1;
		do {
			matchesCount += tmp--;
		} while (tmp > 0);
		Log.d(LOG_TAG, "SC Count of matches: " + matchesCount);
		tmp = matchesCount;
		/**/
		db.execSQL("drop table if exists tmp_games;");
		db.execSQL("create table tmp_games ("
				+ "player1 integer, "
				+ "player2 integer, "
				+ "tournament_id integer, "
				+ "score1 integer, "
				+ "score2 integer" + ");"	
			);


		/*****************/
		db.execSQL("drop table if exists players_weights;");
		db.execSQL("create table players_weights ("
				+ "random integer, "
				+ "player integer" + ");"	
			);
		db.execSQL("insert into players_weights (player) select player_id from tmp_pl_x_trnm_lnk;");
		db.execSQL("update players_weights set random = random();");
		int[] players = new int[playersCount];
		int k=0;
		String sqlQuery = "select player from players_weights order by random;";
		Cursor c2 = db.rawQuery(sqlQuery, null);
		if (c2.moveToFirst()) {
			int playerColIndex = c2.getColumnIndex("player");
			do {
				players[k] = c2.getInt(playerColIndex);
				k++;
			}
			while (c2.moveToNext());
		}
		else Log.d(LOG_TAG, "SC 0 rows");
		
		c2.close();
		
		int i = 0;
		int j;	
		do {
			j = i+1;
			do {
				Log.d(LOG_TAG,"i = "+i+", j = "+j);
				Log.d(LOG_TAG,"SC insert into tmp_games (player1, player2) values ("+players[i]+", "+players[j]+");");
				db.execSQL("insert into tmp_games (player1, player2) values ("+players[i]+", "+players[j]+");");
				j++;
			} while (j<playersCount);			
			i++;
		} while (i<playersCount-1);
		Log.d(LOG_TAG, "SC insert into matches (player1_id, player2_id, tournament_id) select player1, player2, " + tournament_id + " as tournament_id from tmp_games order by Random();");
		db.execSQL("insert into matches (player1_id, player2_id, tournament_id) select player1, player2, " + tournament_id + " as tournament_id from tmp_games order by Random();");
		Log.d(LOG_TAG, "SC insert into games (match_id) select id from matches where tournament_id = " + tournament_id + ";");
		db.execSQL("insert into games (match_id) select id from matches where tournament_id = " + tournament_id + ";");
		db.execSQL("insert into tournament select * from matches where tournament_id = "+tournament_id+";");
/*
		Log.d(LOG_TAG,"SC select m.id, m.player1_id, m.player2_id, g.player1_score as score1, g.player2_score as score2 from (select * from matches where tournament_id = "+tournament_id+") m left join games g on m.id = g.match_id;");
		Cursor c = db.rawQuery("select m.id, m.player1_id, m.player2_id, g.player1_score as score1, g.player2_score as score2 from (select * from matches where tournament_id = "+tournament_id+") m left join games g on m.id = g.match_id;", null);

		if (c.moveToFirst()) {
			int idColIndex = c.getColumnIndex("id");
			int player1idColIndex = c.getColumnIndex("player1_id");
			int player2idColIndex = c.getColumnIndex("player2_id");
			int score1ColIndex = c.getColumnIndex("score1");
			int score2ColIndex = c.getColumnIndex("score2");
			do {
			Log.d(LOG_TAG,
				"id = " + c.getInt(idColIndex) +
				", player1 = " + c.getInt(player1idColIndex) +
				", player2 = " + c.getInt(player2idColIndex) +
				", score1 = " + c.getInt(score1ColIndex) +
				", score2 = " + c.getInt(score2ColIndex));
			}
			while (c.moveToNext());
		}
		else Log.d(LOG_TAG, "SC 0 rows");
		c.close();			*/
			
		//db.execSQL("drop table if exists players_weights;");
		//db.execSQL("drop table if exists tournament;");
	  			
	}
	
	public void onLeftSwipe() {
		// do nothing
	}
	
	public void checkAndFinishTournament() {
		//int count = db.getIntValue("select count(*) as cnt from tournament where score1 = '' or score1 is null or score2 = '' or score2 is null;", "cnt");
		int count = db.getIntValue("select count(*) as cnt from tournament t left join games g on t.id = g.match_id where g.player1_score = '' or g.player1_score is null or g.player2_score = '' or g.player2_score is null;", "cnt");

		Log.d(LOG_TAG,"SC checkAndFinishTournament count = "+count);
		if (count == 0) {
			Tournament trn1 = new Tournament(tournament_id,db);
			trn1.closeTournament();
		}
	}
	
	public void requeryAllLoaders() {
		Log.d(LOG_TAG,"SC Requery All Loaders Called");
		/*try {
			getDraw();
		} catch (NullPointerException e) {
			Log.d(LOG_TAG, "SC tried to getDraw, but seems frScoreboard is null");
		}*/
		requeryLoader(0);		
		requeryLoader(1);
	}
	
	
	 @Override
	  public Loader<Cursor> onCreateLoader(int id, Bundle bndl) {
		 Log.d(LOG_TAG,"onCreateLoader");
	    return new MyCursorLoader(activity, db);
	  }

	  @Override
	  public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		  Log.d(LOG_TAG,"onLoadFinished. id: "+loader.getId());
		  if (loader.getId()==0) {			  
			  scAdapter.swapCursor(cursor);
			  Log.d(LOG_TAG,"On Load Finished cursor count: "+cursor.getCount());
			  
			/*if (cursor.moveToFirst()) {			
				int player1ColIndex = cursor.getColumnIndex("player1");
				int player2ColIndex = cursor.getColumnIndex("player2");
				do {
					Log.d(LOG_TAG,"SC onLoadFinished Position:"+cursor.getPosition()+". player1 = "+cursor.getString(player1ColIndex)+". player2 = "+cursor.getString(player2ColIndex));
				}
				while (cursor.moveToNext());			
			}
			else Log.d(LOG_TAG,"SCA No rows in tournament?!");*/
			  
			  scAdapter.updateCursor();
		  } else {
			  scAdapterRes.swapCursor(cursor);
		  }
	  }

	  @Override
	  public void onLoaderReset(Loader<Cursor> loader) {
	  }
	  
	  static class MyCursorLoader extends CursorLoader {

	    DB db;
	    
	    public MyCursorLoader(Context context, DB db) {
	      super(context);
	      this.db = db;
	      Log.d(LOG_TAG,"MyCursorLoader");
	    }
	    
	    @Override
	    public Cursor loadInBackground() {
	      
	      Cursor cursor;
	      if (this.getId()==0) {	    	  
	    	  Log.d(LOG_TAG,"SC: Draw. Load in background: "+sqlQuery);
	    	  cursor = db.rawQuery(sqlQuery, null);
/*
	    	  Log.d(LOG_TAG,"Load in background cursor count: "+cursor.getCount());
	    	  Cursor c = db.rawQuery("select * from tournament;", null);
	    	  if (c.moveToFirst()) {
				    int idColIndex = c.getColumnIndex("id");
				    int tournament_idColIndex = c.getColumnIndex("tournament_id");
		  			int player1_idColIndex = c.getColumnIndex("player1_id");
		  			int player2_idColIndex = c.getColumnIndex("player2_id");			
		  			do {
		  			Log.d(LOG_TAG,
		  				"SC. player1_id = " + c.getInt(player1_idColIndex) +
		  				", player2_id = " + c.getInt(player2_idColIndex) +
						", id = " + c.getInt(idColIndex) +
						", tournament_id = " + c.getInt(tournament_idColIndex));
		  			}
		  			while (c.moveToNext());
		  		}
		  		else Log.d(LOG_TAG, "0 rows");
		  		c.close();	
		  		
	  		  c = db.rawQuery("select * from players;", null);
	    	  if (c.moveToFirst()) {
		  			int idColIndex = c.getColumnIndex("id");
		  			int nameColIndex = c.getColumnIndex("name");			
		  			do {
		  			Log.d(LOG_TAG,
		  				"SC. id = " + c.getInt(idColIndex) +
		  				", name = " + c.getString(nameColIndex));
		  			}
		  			while (c.moveToNext());
		  		}
		  		else Log.d(LOG_TAG, "0 rows");
		  		c.close();	
		  		
		  		c = db.rawQuery("select * from tmp_pl_x_trnm_lnk;", null);
		    	  if (c.moveToFirst()) {
			  			int player_idColIndex = c.getColumnIndex("player_id");		
			  			do {
			  			Log.d(LOG_TAG,
			  				"SC. id = " + c.getInt(player_idColIndex));
			  			}
			  			while (c.moveToNext());
			  		}
			  		else Log.d(LOG_TAG, "0 rows");
			  		c.close();
	    	  */
	    	  
	      } else {
	    	  Log.d(LOG_TAG,"SC: Results. Load in background: "+sqlResultsQuery);
	    	  cursor = db.rawQuery(sqlResultsQuery, null);
	      }
	      
	      return cursor;
	    }   	    
	  }	 
}












