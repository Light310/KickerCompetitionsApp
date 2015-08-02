package light.training.kicker1;

import java.util.Calendar;
import java.util.Date;

import android.util.Log;

public class Tournament {
	int id;
	int type;
	String name;
	long start_date;
	long end_date;
	DB db;

	final static String LOG_TAG = "myLogs";
	
	public Tournament(int _type, String _name, DB _db) {
		this.type = _type;
		this.name = _name;
		db = _db;
		start_date = System.currentTimeMillis();
		String sqlText = "insert into tournaments (type, name, start_date, end_date) values ("+type+", '"+name+"',"+start_date+",null);";
		db.execSQL(sqlText);
		Log.d(LOG_TAG,"Tournament. Creation text: "+sqlText);
		sqlText = "select id from tournaments where start_date = "+start_date+";";
		id = db.getIntValue(sqlText, "id");
		Log.d(LOG_TAG,"Tournament. Id = "+id+". Select text: "+sqlText);
	}
	
	public Tournament(int _id, DB _db) {
		db = _db;
		String sqlText = "select name from tournaments where id = "+_id+";";
		name = db.getStringValue(sqlText, "name");
		sqlText = "select type from tournaments where id = "+_id+";";
		type = db.getIntValue(sqlText, "type");
		sqlText = "select start_date from tournaments where id = "+_id+";";
		start_date = db.getIntValue(sqlText, "start_date");
		sqlText = "select end_date from tournaments where id = "+_id+";";
		end_date = db.getIntValue(sqlText, "end_date");
		id = _id;
		Log.d(LOG_TAG,"Tournament. Found tournament: id = "+id+", name = "+name+", type = "+type+", start_date = "+start_date+", end_date = "+end_date);		
	}
	
	public void closeTournament() {		
		end_date = System.currentTimeMillis();
		String sqlText = "update tournaments set end_date = "+end_date+" where id = "+id+";";
		db.execSQL(sqlText);
		Log.d(LOG_TAG,"Tournament. Closing text: "+sqlText);
	}
	
	public void deleteTournament(int _id, DB db) {		
		String sqlText = "delete from tournaments where id = "+_id+";";
		db.execSQL(sqlText);
		Log.d(LOG_TAG,"Tournament. Deletion text1: "+sqlText);
		sqlText = "delete from games where tournament_id = "+_id+";";
		Log.d(LOG_TAG,"Tournament. Deletion text2: "+sqlText);
	}
	public boolean isClosed() {
		if (end_date>0) {
			return true;
		} else return false;
	}
}
