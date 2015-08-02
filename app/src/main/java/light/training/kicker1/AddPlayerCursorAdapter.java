package light.training.kicker1;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
 
public class AddPlayerCursorAdapter extends SimpleCursorAdapter {
	
 final static String LOG_TAG = "myLogs";
 Context ctx;
 LayoutInflater lInflater;
 Cursor c, cursor;
 private int layout;
 DB db;
 MainActivity activity;
 AddPlayerDialogExtended apde;
 public int[] arrId;
 public String[] arrName;
 public int[] arrChecked; /* 1|0 */
 
 int count = 0;
 
 
 public AddPlayerCursorAdapter(Context _context, int _layout, Cursor _cursor, String[] _from, int[] _to, AddPlayerDialogExtended _apde) {
	 super(_context, _layout, _cursor, _from, _to);
	 ctx = _context;	 
	 cursor = _cursor;
	 apde = _apde;
     activity = (MainActivity) ctx;
     
     Log.d(LOG_TAG,"APCA AddPlayerCursorAdapter constructor started");
     Log.d(LOG_TAG,"APCA AddPlayerCursorAdapter got Cursor: "+cursor);
	 arrId = new int[count];
	 arrName = new String[count];
	 arrChecked = new int[count];
     Log.d(LOG_TAG,"APCA AddPlayerCursorAdapter end of consturctor");
 }
 
 public void updateCursor() {
	 cursor = getCursor();
	 clearArrays();
	 
	 if (cursor==null) {
		 Log.d(LOG_TAG,"APCA updateCursor constructor, cursor is null");
	 } else {
     if (cursor.moveToFirst()) {			
			int idColIndex = cursor.getColumnIndex("id");
			int nameColIndex = cursor.getColumnIndex("name");
			int isCheckedColIndex = cursor.getColumnIndex("checked");
			do {
				arrId[cursor.getPosition()] = cursor.getInt(idColIndex);
				arrName[cursor.getPosition()] = cursor.getString(nameColIndex);
				arrChecked[cursor.getPosition()] = cursor.getInt(isCheckedColIndex);
				Log.d(LOG_TAG,"APCA updateCursor. Position = "+cursor.getPosition()+". arrName[position] = "+arrName[cursor.getPosition()]+
						". Value = "+cursor.getString(nameColIndex)+
						". Checked = "+cursor.getInt(isCheckedColIndex));
			}
			while (cursor.moveToNext());			
		}
		else Log.d(LOG_TAG,"APCA updateCursor No rows in players?!");
        /*Log.d(LOG_TAG,"APCA This count: "+this.getCount()+". Cursor count: "+cursor.getCount());
	 	cursor.close();*/
	 }
	 Log.d(LOG_TAG,"APCA This count: "+this.getCount());
	 this.notifyDataSetChanged();
 }
 
 public void clearArrays() {
	 cursor = getCursor();
	 count=cursor.getCount();
	 Log.d(LOG_TAG,"APCA Clearing arrays. New count = "+count);
	 arrId = new int[count];
	 arrName = new String[count];
	 arrChecked = new int[count];
 }
 
 
 public View getView(final int position, View convertView, ViewGroup parent) {
	    // используем созданные, но не используемые view	   
	 	final player _player;
	 	if (convertView == null) {
	 		//Log.d(LOG_TAG,"APCA getView No Tag!");
	 		_player = new player();
	 		LayoutInflater inflater = activity.getLayoutInflater();
	 		convertView = inflater.inflate(R.layout.add_player_extended_item, null);
	 		_player.btnDelPlayer = (Button) convertView.findViewById(R.id.btnDelPlayer);
	 		_player.tvPlayerName = (TextView) convertView.findViewById(R.id.tvPlayerName);
	 		_player.cbChoosePlayer = (CheckBox) convertView.findViewById(R.id.cbChoosePlayer); 	
	 		
	 		convertView.setTag(_player);
	 	} else {
	 		_player = (player) convertView.getTag();
	 		//Log.d(LOG_TAG,"APCA getView Got Tag!");
	 	}
	 	
	 	_player.ref = position;	 	
	 	
	 	_player.tvPlayerName.setText(arrName[position]);
	 	Log.d(LOG_TAG,"APCA getView position = "+position+", arrName[position] = "+arrName[position]+", arrChecked[position] = " + arrChecked[position]);
	 	
	 	_player.btnDelPlayer.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.d(LOG_TAG,"APCA OnClick delete player id = "+arrId[position]);
				apde.delPlayer(arrId[position]);
			}
		});
	 	
	 	_player.cbChoosePlayer.setOnCheckedChangeListener(null);
	 	
	 	if (arrChecked[position] == 1) _player.cbChoosePlayer.setChecked(true);
	 	else _player.cbChoosePlayer.setChecked(false);
	 	_player.cbChoosePlayer.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				String mode;
				Log.d(LOG_TAG,"APCA onCheckedChanged. Position: "+position+". Value: "+isChecked);
				if (isChecked) {
					arrChecked[position]=1;
					mode = "select";
				} else {
					arrChecked[position]=0;
					mode = "deselect";
				}
				apde.selectPlayer(arrId[position], mode);
				
			}
		});
	 	
	 	return convertView;
	  }
 
 
 
 public void printResults() {
	 Log.d(LOG_TAG,"APCA Results:");
	 for(int i=0;i<count;i++) {
		 Log.d(LOG_TAG,"APCA Res: "+arrId[i]+" "+arrName[i]+" : "+arrChecked[i]);
	 }
 }

 @Override
 public void notifyDataSetChanged() {
	 Log.d(LOG_TAG,"APCA NotifyDataSetChanged called");
	 printResults();
	 super.notifyDataSetChanged();
 }
 
 private class player {
	 String name;
	 int ref, id, checked;
	 Button btnDelPlayer;
	 TextView tvPlayerName;
	 CheckBox cbChoosePlayer;
 }

}