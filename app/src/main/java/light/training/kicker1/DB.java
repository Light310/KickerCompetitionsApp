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
	    			+ "start_date timestamp, "
	    			+ "end_date timestamp" + ");"	
	    		);
	    	db.execSQL("create table games ("
	    			+ "id integer primary key autoincrement, "
	    			+ "player1_id integer, "
	    			+ "player2_id integer, "
	    			/*+ "player1 text, "
	    			+ "player2 text, "*/
	    			+ "tournament_id integer, "
	    			+ "score1 integer, "
	    			+ "score2 integer" + ");"	
	    		);
	    	db.execSQL("create table player_x_tournament_link ("
	    			//+ "id integer primary key autoincrement, "
	    			+ "tournament_id integer, "	    			
	    			+ "player_id integer" + ");"	
	    		);
	    	db.execSQL("create table global_variables ("
	    			+ "name text, "	    			
	    			+ "value text" + ");"	
	    		);
	    }

	    @Override
	    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			//here should be some upgrade text
			Log.d(LOG_TAG,"DB onUpgrade. Old version = "+oldVersion+". New version = "+newVersion);
	    }
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

