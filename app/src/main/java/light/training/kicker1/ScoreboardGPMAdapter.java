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

public class ScoreboardGPMAdapter extends SimpleCursorAdapter {

    final static String LOG_TAG = "myLogs";
    Context ctx;
    LayoutInflater lInflater;
    Cursor c, cursor;
    private int layout;
    DB db;
    MainActivity activity;
    Scoreboard sc;
    public String[] arrPlayer1, arrPlayer2, arr1Score1, arr1Score2, arr2Score1, arr2Score2, arr3Score1, arr3Score2, arr4Score1, arr4Score2, arr5Score1, arr5Score2;
    int count = 0;
    int tournament_id, gpm;



    public ScoreboardGPMAdapter(Context _context, int _layout, Cursor _cursor, String[] _from, int[] _to, Scoreboard _sc) {
        super(_context, _layout, _cursor, _from, _to);
        ctx = _context;
        //layout = _layout;
        c = _cursor;
        sc = _sc;
        activity = (MainActivity) ctx;
        db = activity.db;
    }
/*
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
    }*/


    public View getView(int position, View convertView, ViewGroup parent) {
        // используем созaданные, но не используемые view
        final match _match;
        if (convertView == null) {
            _match = new match();
            LayoutInflater inflater = activity.getLayoutInflater();
            convertView = inflater.inflate(R.layout.draw_item, null);
            _match.tvPlayer1 = (TextView) convertView.findViewById(R.id.tvDrawPlayer1);
            _match.tvPlayer2 = (TextView) convertView.findViewById(R.id.tvDrawPlayer2);
           /* _match.etScore1 = (EditText) convertView.findViewById(R.id.etPlayer1Score);
            _match.etScore2 = (EditText) convertView.findViewById(R.id.etPlayer2Score);*/

            convertView.setTag(_match);
        } else {
            _match = (match) convertView.getTag();
        }

        _match.ref = position;

        _match.tvPlayer1.setText(arrPlayer1[position]);
        _match.tvPlayer2.setText(arrPlayer2[position]);
       /* _match.etScore1.setText(arrScore1[position]);
        _match.etScore2.setText(arrScore2[position]);*/

        return convertView;
    }



    public void printResults() {
        for(int i=0;i<count;i++) {
            //Log.d("results", "Res: "+arrPlayer1[i]+" "+arrScore1[i]+" : "+arrScore2[i]+" "+arrPlayer2[i]);
        }
    }


    public class match {
        String player1,player2;
        int ref;
        TextView tvPlayer1, tvPlayer2;
        EditText et1Score1, et1Score2, et2Score1, et2Score2, et3Score1, et3Score2, et4Score1, et4Score2, et5Score1, et5Score2;
    }

}