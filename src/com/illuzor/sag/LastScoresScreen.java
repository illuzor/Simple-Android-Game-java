package com.illuzor.sag;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import com.illuzor.sag.tools.DatabaseManager;

public class LastScoresScreen extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.last_scores_screen);

        TextView lastScores = (TextView) findViewById(R.id.lsc_screen_tv_lastscores);

        try {
            DatabaseManager db = new DatabaseManager(this);
            db.open();
            lastScores.setText(db.getData());
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}