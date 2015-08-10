package light.training.kicker1;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class DB {
	  
	  private static final String DB_NAME = "mydb";
	  private static final int DB_VERSION = 1;
	  final String LOG_TAG = "myLogs";
	  public String prevSql = "";
	  
	  private final Context mCtx;
	  
	  
	  private DBHelper mDBHelper;
	  private SQLiteDatabase mDB;
	  
	  public DB(Context ctx) {
	    mCtx = ctx;
	  }
	  
	  // открыть подключение
	  public void open() {
	    mDBHelper = new DBHelper(mCtx, DB_NAME, null, DB_VERSION);
	    mDB = mDBHelper.getWritableDatabase();
	  }
	  
	  // закрыть подключение
	  public void close() {
	    if (mDBHelper!=null) mDBHelper.close();
	  }
	  
	  // получить все данные из таблицы DB_TABLE
	  public Cursor getTableData(String table) {
	    return mDB.query(table, null, null, null, null, null, null);
	  }
	  
	  public Cursor rawQuery(String sqlQuery, String[] selectionArgs) {
	    return mDB.rawQuery(sqlQuery, selectionArgs);
	  }
	  
	  public void execSQL(String sqlQuery) {
		   if (prevSql.equals(sqlQuery)) {
		    	Log.d(LOG_TAG,"DB: Dublicate query, aborting: "+sqlQuery);
		    } else {
			    mDB.execSQL(sqlQuery);
			    prevSql = new String (sqlQuery);
			    Log.d(LOG_TAG, "DB: "+sqlQuery);
		    }
		  }
	  
	  public int clearTable(String table) {
		  return mDB.delete(table, null, null);
		  }
	  
	  public int selectCount(String tableName) {			
			String sqlQuery = "select * from " + tableName + ";";
			Cursor c = mDB.rawQuery(sqlQuery, null);	
			int cnt = c.getCount();
		    c.close();
		    return cnt;
		  }
	  
	  // класс по созданию и управлению БД
	  private class DBHelper extends SQLiteOpenHelper {

	    public DBHelper(Context context, String name, CursorFactory factory,
	        int version) {
	      super(context, name, factory, version);
	    }

	    // создаем и заполняем БД
	    @Override
	    public void onCreate(SQLiteDatabase db) {
	    	db.execSQL("create table players ("
	    			+ "id integer primary key autoincrement, "
	    			+ "active_flg integer default 1, "
	    			+ "name text" + ");"	
	    		);
	    	db.execSQL("create view active_players as select * from players where active_flg = 1;"	
	    		);
	    	db.execSQL("create table tournaments ("
	    			+ "id integer primary key autoincrement, "
	    			+ "name text, "
	    			+ "type integer, "
					+ "games_per_match int, "
	    			+ "start_date timestamp, "
	    			+ "end_date timestamp" + ");"	
	    		);
			db.execSQL("create table matches ("
					+ "id integer primary key autoincrement, "
					+ "tournament_id integer, "
					+ "player1_id integer, "
					+ "player2_id integer" + ");");
	    	db.execSQL("create table games ("
	    			+ "id integer primary key autoincrement, "
	    			+ "match_id integer, "
	    			+ "player1_score integer, "
	    			+ "player2_score integer" + ");"
	    		);
	    	db.execSQL("create table player_x_tournament_link ("
	    			//+ "id integer primary key autoincrement, "
	    			+ "tournament_id integer, "	    			
	    			+ "player_id integer" + ");"	
	    		);

			/* Views for scoreboard */
			db.execSQL("create table tournament as select * from matches;");

			db.execSQL(
			"create view tmp_match_ext_view as "
			+ "select m.id as match_id, player1_id, player2_id, "
			+ "case when ((coalesce(won1,0)>coalesce(won2,0)) or (coalesce(won1,0)=coalesce(won2,0) and goals1>goals2)) then 3 "
			+ "when (coalesce(won1,0)=coalesce(won2,0) and goals1=goals2) then 1 "
			+ "else 0 end as player1_score, "
			+ "case when ((coalesce(won1,0)>coalesce(won2,0)) or (coalesce(won1,0)=coalesce(won2,0) and goals1>goals2)) then 1 "
			+ "else 0 end as player1_wins, "
			+ "case when ((coalesce(won1,0)<coalesce(won2,0)) or (coalesce(won1,0)=coalesce(won2,0) and goals1<goals2)) then 1 "
			+ "else 0 end as player1_lost, "
			+ "case when ((coalesce(won1,0)<coalesce(won2,0)) or (coalesce(won1,0)=coalesce(won2,0) and goals1<goals2)) then 3 "
			+ "when (coalesce(won1,0)=coalesce(won2,0) and goals1=goals2) then 1 "
			+ "else 0 end as player2_score, "
			+ "case when ((coalesce(won1,0)<coalesce(won2,0)) or (coalesce(won1,0)=coalesce(won2,0) and goals1<goals2)) then 1 "
			+ "else 0 end as player2_wins, "
			+ "case when ((coalesce(won1,0)>coalesce(won2,0)) or (coalesce(won1,0)=coalesce(won2,0) and goals1>goals2)) then 1 "
			+ "else 0 end as player2_lost, "
			+ "case when (coalesce(won1,0)=coalesce(won2,0) and goals1=goals2) then 1 "
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
			+ "(select match_id, sum(player1_score) as goals1, sum(player2_score) as goals2 from games group by match_id) g "
			+ "on m.id = g.match_id;");

			db.execSQL("create view tmp_players as select player1_id from matches union select player2_id from tournament");

			db.execSQL(
			"create view results as "
			+ "select player_id, sum(score) as score, sum(wins)+sum(lost)+sum(draws) as games, sum(wins) as wins, "
			+ "sum(lost) as losses, sum(draws) as draws, sum(goals) as goals, sum(missed) as missed, sum(goals)-sum(missed) as delta from "
			+ "(select player1_id as player_id, sum(player1_score) as score, sum(player1_wins) as wins, "
			+ "sum(player1_lost) as lost, sum(draws) as draws, sum(player1_goals) as goals, sum(player2_goals) as missed from tmp_match_ext_view group by player1_id "
			+ "union all "
			+ "select player2_id as player_id, sum(player2_score) as score, sum(player2_wins) as wins, "
			+ "sum(player2_lost) as lost, sum(draws) as draws, sum(player2_goals) as goals, sum(player1_goals) as missed from tmp_match_ext_view group by player2_id) a group by player_id;");

			/* Global Variables*/
	    	db.execSQL("create table global_variables_str ("
	    			+ "name text, "	    			
	    			+ "value text" + ");"	
	    		);

			db.execSQL("create table global_variables_num ("
							+ "name text, "
							+ "value integer" + ");"
			);
			db.execSQL("insert into global_variables_num values ('games_per_match',1);");
	    }

	    @Override
	    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			//here should be some upgrade text
			Log.d(LOG_TAG,"DB onUpgrade. Old version = "+oldVersion+". New version = "+newVersion);
	    }
	  }

	public void setGVValueNum(String name, int value) {
		Log.d(LOG_TAG,"DB. setGVValueNum. update global_variables_num set value = "+value+" where name = '"+name+"';");
		mDB.execSQL("update global_variables_num set value = "+value+" where name = '"+name+"';");
	}

	public void setGVValueStr(String name, String value) {
		Log.d(LOG_TAG,"DB. setGVValueStr. update global_variables_str set value = '"+value+"' where name = '"+name+"';");
		mDB.execSQL("update global_variables_str set value = '"+value+"' where name = '"+name+"';");
	}

	public int getGVValueNum(String name) {
		Log.d(LOG_TAG,"DB. getGVValueNum. select value from global_variables_num where name = '"+name+"';");
		int result = getIntValue("select value from global_variables_num where name = '"+name+"';","value");
		return result;
	}

	public String getGVValueStr(String name) {
		Log.d(LOG_TAG,"DB. getGVValueStr. select value from global_variables_str where name = '"+name+"';");
		String result = getStringValue("select value from global_variables_str where name = '"+name+"';","value");
		return result;
	}
	  
	// Используется только для гарантированно одной строки
		public int getIntValue(String sqlText, String colName) {			
			Cursor c = mDB.rawQuery(sqlText, null);
			int result=-1;
			if (c.moveToFirst()) {
				int nameColIndex = c.getColumnIndex(colName);
				do {
					if (!c.isNull(nameColIndex))
						result = c.getInt(nameColIndex);
				//result = c.getInt(nameColIndex);
				Log.d(LOG_TAG, "DB: result = "+result);
				}
				while (c.moveToNext());
			}
			else 
				Log.d(LOG_TAG, "0 rows");
			
			c.close();
			return result;
		}
		
		public String getStringValue(String sqlText, String colName) {			
			Cursor c = mDB.rawQuery(sqlText, null);
			String result="";
			if (c.moveToFirst()) {
				int nameColIndex = c.getColumnIndex(colName);
				do {
					if (!c.isNull(nameColIndex))
						result = c.getString(nameColIndex);
				//result = c.getInt(nameColIndex);
				Log.d(LOG_TAG, "DB: result = "+result);
				}
				while (c.moveToNext());
			}
			else 
				Log.d(LOG_TAG, "0 rows");
			
			c.close();
			return result;
		}
	}

