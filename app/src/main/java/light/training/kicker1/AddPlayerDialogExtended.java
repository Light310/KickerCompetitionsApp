package light.training.kicker1;

import light.training.kicker1.Scoreboard.MyCursorLoader;
import android.app.DialogFragment;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class AddPlayerDialogExtended extends DialogFragment implements OnClickListener,  LoaderCallbacks<Cursor> {

  final static String LOG_TAG = "myLogs";
  public EditText etDlgPlayerName;
  MainActivity activity;
  DB db;
  
  //final static String sqlQuery = "select rowid _id, name from players";
  //final static String sqlQuery = "select rowid _id, id, name, 0 as checked from players";
  /*final static String sqlQuery = "select rowid _id, id, name, 0 as checked from active_players";*/
  final static String sqlQuery = "select a.rowid _id, id, name, case when lnk.rowid is not null then 1 else 0 end as checked from active_players a " +
  		"left join tmp_pl_x_trnm_lnk lnk " +
  		"on a.id = lnk.player_id;";
  
  //SimpleCursorAdapter scAdapterPlayers;
  AddPlayerCursorAdapter scAdapterPlayers;

  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
	Log.d(LOG_TAG, "APDE Inside onCreateView AddPlayerDialogExtended");
	
	activity = (MainActivity) getActivity();
	db = activity.db;
	
	
    //reloadPlayers();
	
    getDialog().setTitle("Choose players.");
    View v = inflater.inflate(R.layout.addplayerextendeddialog, null);
    
    String[] from = new String[] { "id", "name", "checked" };
    int[] to = new int[] { R.id.btnDelPlayer, R.id.tvPlayerName, R.id.cbChoosePlayer };    
    
    Log.d(LOG_TAG,"APDE Before scAdapterPlayers creation. (Context)activity = "+(Context)activity+". R.layout.add_player_extended_item = "+R.layout.add_player_extended_item +
    		". from = "+from+". to = "+to);
    //scAdapterPlayers = new SimpleCursorAdapter((Context)activity, R.layout.add_player_extended_item, null, from, to, 0);
    scAdapterPlayers = new AddPlayerCursorAdapter((Context)activity, R.layout.add_player_extended_item, null, from, to, this);
    ListView lvPlayersList = (ListView) v.findViewById(R.id.lvPlayersList);
    
    lvPlayersList.setAdapter(scAdapterPlayers);
    
    activity.getSupportLoaderManager().initLoader(2, null, this); 
    //reloadPlayers();
    
    etDlgPlayerName = (EditText) v.findViewById(R.id.etNewPlayerName); 
    Button btnAdd = (Button) v.findViewById(R.id.btnAddNewPlayer);
    btnAdd.setOnClickListener(this);
    
    Button btnSelectPlayers = (Button) v.findViewById(R.id.btnSelectAllPlayers);
    btnSelectPlayers.setOnClickListener(new OnClickListener(){
        public void onClick(View v) {
        	db.execSQL("delete from tmp_pl_x_trnm_lnk;");
        	db.execSQL("insert into tmp_pl_x_trnm_lnk (player_id) select id from active_players;");
        	activity.frInitial.updateList();        	
        	reloadPlayers();
        	//select all players
        }

    });
    
    Button btnDeselectPlayers = (Button) v.findViewById(R.id.btnDeselectAllPlayers);
    btnDeselectPlayers.setOnClickListener(new OnClickListener(){
        public void onClick(View v) {
        	//db.clearTable("players");
        	//db.execSQL("update players set active_flg = 0;");
        	db.execSQL("delete from tmp_pl_x_trnm_lnk;");
        	activity.frInitial.updateList();        	
        	reloadPlayers();
        }

    });
    
    Button btnDone = (Button) v.findViewById(R.id.btnAddingDone);
    btnDone.setOnClickListener(new OnClickListener(){
        public void onClick(View v) {
        	dismiss();
        }

    });
    
    return v;
  }  

public void reloadPlayers() {
	scAdapterPlayers.clearArrays();
	activity.getSupportLoaderManager().getLoader(2).forceLoad();
	scAdapterPlayers.notifyDataSetChanged();
}
@Override
public void onClick(View v) {
	// TODO Auto-generated method stub
	Log.d(LOG_TAG, "APDE Inside OnClick Dialog "+etDlgPlayerName.getText().toString());
	MainActivity activity = (MainActivity) getActivity();
	if (addPlayer(etDlgPlayerName.getText().toString())) {    
    activity.frInitial.updateList();    
    reloadPlayers();
    etDlgPlayerName.setText("");
	}
}
	public void delPlayer(int id) {
		db.execSQL("update players set active_flg = 0 where id = "+id+";");
		db.execSQL("delete from tmp_pl_x_trnm_lnk where player_id = "+id+";");
		activity.frInitial.updateList();    
		//activity.frScoreboard.requeryAllLoaders();
	    reloadPlayers();
	}
	
	public void selectPlayer(int id, String mode) {
		Log.d(LOG_TAG,"APDE "+mode+" player id = "+id);
		String sqlText;
		if (mode.equals("select")) {
			//select
			sqlText = "insert into tmp_pl_x_trnm_lnk (player_id) values ("+id+");";
		} else {
			//deselect
			sqlText = "delete from tmp_pl_x_trnm_lnk where player_id = "+id+";";
		}
		db.execSQL(sqlText);
		activity.printLink();
		activity.frInitial.updateList();
		activity.frScoreboard.requeryAllLoaders();
		reloadPlayers();
	}

	public boolean addPlayer(String name) {	  
		Log.d(LOG_TAG, "APDE Inside addPlayer");
		MainActivity activity = (MainActivity) getActivity();
		
		String sqlQuery = "select * from active_players where name = '" + name + "';";
		Cursor c = activity.db.rawQuery(sqlQuery, null);	
		int cnt = c.getCount();
	    c.close();
		Log.d(LOG_TAG, "APDE Count = "+cnt);
		if (cnt>0) {
			Log.d(LOG_TAG, "APDE This player already exists");
			Toast toast = Toast.makeText(activity, "Player "+name+" already added!", Toast.LENGTH_SHORT);
			toast.show();
			return false;
		} else if (name.replace(" ", "").length()==0) {
			Log.d(LOG_TAG, "APDE Empty player name not available");
			Toast toast = Toast.makeText(activity, "Empty player name not available", Toast.LENGTH_SHORT);
			toast.show();
			return false;
		} else {
			Log.d(LOG_TAG, "APDE insert into players (name) values ('" + name + "');");
			activity.db.execSQL("insert into players (name) values ('" + name + "');");
			int id = db.getIntValue("select id from players where name = '"+name+"';", "id");
			activity.db.execSQL("insert into tmp_pl_x_trnm_lnk (player_id) values ("+id+");");
			activity.frScoreboard.requeryAllLoaders();
			return true;
		}
	  }

	 @Override
	  public Loader<Cursor> onCreateLoader(int id, Bundle bndl) {
		 Log.d(LOG_TAG,"APDE onCreateLoader for scAdapterPlayers");
	    return new PlayersCursorLoader(activity, db);
	  }

	  @Override
	  public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		  Log.d(LOG_TAG,"APDE onLoadFinished for scAdapterPlayers. id: "+loader.getId());		 
		  scAdapterPlayers.swapCursor(cursor);
		  scAdapterPlayers.updateCursor();
		  //scAdapterPlayers.notifyDataSetChanged();
	  }

	  @Override
	  public void onLoaderReset(Loader<Cursor> loader) {
	  }
	  
	  static class PlayersCursorLoader extends CursorLoader {

	    DB db;
	    
	    public PlayersCursorLoader(Context _context, DB _db) {
	      super(_context);
	      this.db = _db;
	      Log.d(LOG_TAG,"APDE PlayersCursorLoader");
	    }
	    
	    @Override
	    public Cursor loadInBackground() {
	      
	      Cursor cursor;
	      Log.d(LOG_TAG,"APDE Load in background: "+sqlQuery);
	      cursor = db.rawQuery(sqlQuery, null);	    
	      /*Cursor c = db.rawQuery(sqlQuery, null);
	      Log.d(LOG_TAG, "APDE query: "+sqlQuery);
	      if (c.moveToFirst()) {
				int idColIndex = c.getColumnIndex("id");
				int nameColIndex = c.getColumnIndex("name");
				int checkedColIndex = c.getColumnIndex("checked");
				do {
				Log.d(LOG_TAG,
					"APDE. id = " + c.getInt(idColIndex) +
					", name = " + c.getString(nameColIndex) +
					", checked = " + c.getInt(checkedColIndex));
				}
				while (c.moveToNext());
			}
			else Log.d(LOG_TAG, "SC 0 rows");
			c.close();		*/
	      
	      return cursor;
	    }
	    
	    
	    
	  }
}