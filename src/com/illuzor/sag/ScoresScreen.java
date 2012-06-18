package com.illuzor.sag;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.illuzor.sag.tools.DatabaseManager;

public class ScoresScreen extends Activity implements View.OnClickListener {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scores_screen);

        Button menu, again;
        TextView scores = (TextView) findViewById(R.id.sc_screen_tv_scores);

        menu = (Button) findViewById(R.id.sc_screen_menu);
        again = (Button) findViewById(R.id.sc_screen_again);
        menu.setOnClickListener(this);
        again.setOnClickListener(this);

        Bundle scoresBundle = getIntent().getExtras();
        int score = scoresBundle.getInt("sc");
        scores.setText("YOUR SCORE: " + String.valueOf(score));

        updateDatabase(String.valueOf(score));
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sc_screen_menu:
                Intent mainMenuIntent = new Intent(ScoresScreen.this, MainMenu.class);
                startActivity(mainMenuIntent);
                break;
            case R.id.sc_screen_again:
                Intent gameScreen = new Intent("com.illuzor.sag.GAME_SCREEN");
                startActivity(gameScreen);
                break;
        }
    }

    private void updateDatabase(String score) {
        try {
            DatabaseManager db = new DatabaseManager(ScoresScreen.this);
            db.open();
            db.createEntry(score);
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}