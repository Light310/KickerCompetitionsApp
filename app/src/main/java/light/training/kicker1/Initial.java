package light.training.kicker1;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class Initial extends Fragment {
	
	TextView tv;
	DB db;
	public DialogFragment addPlayerDialog;
	public MainActivity activity;
	String[] spinner_data = {"1","2","3","4","5"};
	
	final String sqlText = "select p.* from players p inner join tmp_pl_x_trnm_lnk lnk on p.id = lnk.player_id";

	final String LOG_TAG = "myLogs";
	  
	@Override
	  public View onCreateView(LayoutInflater inflater, ViewGroup container,
	      Bundle savedInstanceState) {
		Log.d(LOG_TAG, "Ini OnCreate Initial");	
		
		
		View v = inflater.inflate(R.layout.initial, null);
		tv = (TextView) v.findViewById(R.id.tvPlayersList);
		activity = (MainActivity) getActivity();
		db = activity.db;
		onLoadUpdateList();

		//spinner
		// адаптер
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), R.layout.simple_spinner_item, spinner_data);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		Spinner spinner = (Spinner) v.findViewById(R.id.spnGamesPerMatch);
		spinner.setAdapter(adapter);
		// заголовок
		spinner.setPrompt("Games Per Match");
		// выделяем элемент
		int spinnerSelection = db.getGVValueNum("games_per_match");
		spinner.setSelection(spinnerSelection-1);
		// устанавливаем обработчик нажатия
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
									   int position, long id) {
				// показываем позиция нажатого элемента
				//Toast.makeText(activity.getBaseContext(), "Position = " + position, Toast.LENGTH_SHORT).show();
				db.setGVValueNum("games_per_match", position+1);
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		
		
		
		//addPlayerDialog = new AddPlayerDialog();
		addPlayerDialog = new AddPlayerDialogExtended();
		
		Button btnAddPlayer = (Button) v.findViewById(R.id.addPlayerButton);
		btnAddPlayer.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				addPlayerDialog.show(getFragmentManager(), "addPlayerDialog");			
			}
		});
		
		Button btnStartTournament = (Button) v.findViewById(R.id.startTournamentButton);
		btnStartTournament.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startTournament();
			}
		});
		
		Button btnContinueTournament = (Button) v.findViewById(R.id.continueTournamentButton);
		btnContinueTournament.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				continueTournament();
			}
		});
				
		
	    return v;
	  }	
	
	public void onLeftSwipe() {
		startTournament();
	}
	
	public void startTournament() {
		MainActivity activity = (MainActivity) getActivity();
		if (db.selectCount("tmp_pl_x_trnm_lnk")<2) {
	  		Toast toast = Toast.makeText(activity, "You need at least 2 players to start tournament!", Toast.LENGTH_SHORT);
			toast.show();
	  	} else {
	  		activity.onClkStartTournament("new", 0);
	  	}
	}
	
	public void continueTournament() {
		MainActivity activity = (MainActivity) getActivity();
		int tournament_id = db.getIntValue("select max(id) as id from tournaments", "id");
	    activity.onClkStartTournament("load", tournament_id);
	}
	
	public void onLoadUpdateList() {
		  	 
		  String text = "";		  
			      
	      //Cursor c = db.getTableData("players");
		  Cursor c = db.rawQuery(sqlText, null);
	  	
			if (c.moveToFirst()) {
				text = "List of players: ";
				int idColIndex = c.getColumnIndex("id");
				int nameColIndex = c.getColumnIndex("name");
				int activeColIndex = c.getColumnIndex("active_flg");
				do {
				Log.d(LOG_TAG,
					"Ini ID = " + c.getInt(idColIndex) +
					", active = " + c.getInt(activeColIndex) +
					", name = " + c.getString(nameColIndex));
				if (c.getInt(activeColIndex)==1) text+=c.getString(nameColIndex)+", ";
				}
				while (c.moveToNext());
				text = text.substring(0,text.length()-2)+".";
			}
			else text = "List of players: empty.";
			c.close();

		  tv.setText(text);
	  }

	 public void updateList() {
		  TextView tv = (TextView) getFragmentManager().findFragmentById(R.id.frInitial).getView().findViewById(R.id.tvPlayersList);	 
		  String text = "";
			      
		//Cursor c = db.getTableData("players");
		  Cursor c = db.rawQuery(sqlText, null);
			if (c.moveToFirst()) {
				text = "List of players: ";
				int idColIndex = c.getColumnIndex("id");
				int nameColIndex = c.getColumnIndex("name");
				int activeColIndex = c.getColumnIndex("active_flg");
				do {
				Log.d(LOG_TAG,
					"ID = " + c.getInt(idColIndex) +
					", active = " + c.getInt(activeColIndex) +
					", name = " + c.getString(nameColIndex));
				if (c.getInt(activeColIndex)==1) text+=c.getString(nameColIndex)+", ";
				}
				while (c.moveToNext());
				text = text.substring(0,text.length()-2)+".";
			}
			else text = "List of players: empty.";
			c.close();
		  tv.setText(text);
	  }
}
