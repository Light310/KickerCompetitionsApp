package light.training.kicker1;

import light.training.kicker1.Scoreboard.MyCursorLoader;
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
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class TournamentMainFragment extends Fragment implements LoaderCallbacks<Cursor>  {

	final static String LOG_TAG = "myLogs";
	MainActivity activity;
	
	ListView lvActiveTournaments;
	//SimpleCursorAdapter trnmAdapterActive;
	TournamentListAdapter trnmAdapterActive;
	
	final static String sqlQuery = "select rowid _id, id, name from tournaments where end_date is null;"; 
	
	DB db;
	
	View v;
	  
	@Override
	  public View onCreateView(LayoutInflater inflater, ViewGroup container,
	      Bundle savedInstanceState) {
		Log.d(LOG_TAG, "OnCreate Tournament Main");	
		
		activity = (MainActivity) getActivity();
		db = activity.db;
		
		v = inflater.inflate(R.layout.tournament_main, null);
		Button btnNewTournament = (Button) v.findViewById(R.id.btnNewTournament);
		btnNewTournament.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {				
				activity.onClickNewTournament();
			}
		});
		
		Button btnOldTournaments = (Button) v.findViewById(R.id.btnOldTournaments);
		btnOldTournaments.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {				
				activity.onClkOldTournaments();
			}
		});		
		
		showActiveTournaments();		
		
	    return v;
	  }
	
	@Override
	public void onResume() {
		super.onResume();
		//activity.getSupportLoaderManager().getLoader(3).forceLoad();
		//trnmAdapterActive.notifyDataSetChanged();
	}
	
	public void showActiveTournaments() {
		String[] from = new String[] { "id" };
	    int[] to = new int[] { R.id.barActiveTournament };
	    
	    Cursor c = db.rawQuery(sqlQuery, null);
	    
	    //trnmAdapterActive = new SimpleCursorAdapter((Context)activity, R.layout.active_tournament_item, null, from, to, 0);
	    trnmAdapterActive = new TournamentListAdapter((Context)activity, R.layout.active_tournament_item, c, from, to);
	    lvActiveTournaments = (ListView) v.findViewById(R.id.lvActiveTournaments);
	    
	    lvActiveTournaments.setAdapter(trnmAdapterActive);
	    
	    //activity.getSupportLoaderManager().initLoader(3, null, this);
	}
	
	public void onLeftSwipe() {
		activity.onClickNewTournament();
	}
	
	
	
	@Override
	  public Loader<Cursor> onCreateLoader(int id, Bundle bndl) {
		 Log.d(LOG_TAG,"onCreateLoader");
	    return new MyCursorLoader(activity, db);
	  }

	  @Override
	  public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		  Log.d(LOG_TAG,"onLoadFinished. id: "+loader.getId());
		  trnmAdapterActive.swapCursor(cursor);
		  Log.d(LOG_TAG,"On Load Finished cursor count: "+cursor.getCount());	
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
	     
    	  Log.d(LOG_TAG,"Load in background: "+sqlQuery);
    	  cursor = db.rawQuery(sqlQuery, null);	    	 
	      
	      return cursor;
	    }   	    
	  }	 
	
}
