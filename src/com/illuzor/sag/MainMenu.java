package com.illuzor.sag;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainMenu extends Activity implements View.OnClickListener {

    Button playButton, settingsButton, scoresButton, exitButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        initButtons();
    }

    private void initButtons() {
        playButton = (Button) findViewById(R.id.play_btn);
        playButton.setOnClickListener(this);

        settingsButton = (Button) findViewById(R.id.settings_btn);
        settingsButton.setOnClickListener(this);

        scoresButton = (Button) findViewById(R.id.scores_btn);
        scoresButton.setOnClickListener(this);

        exitButton = (Button) findViewById(R.id.exit_btn);
        exitButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.play_btn:
                Intent gameScreen = new Intent("com.illuzor.sag.GAME_SCREEN");
                startActivity(gameScreen);
                break;
            case R.id.settings_btn:
                Intent scoresScreen = new Intent("com.illuzor.sag.SETTINGS_SCREEN");
                startActivity(scoresScreen);
                break;
            case R.id.scores_btn:
                Intent lastScoresScreen = new Intent("com.illuzor.sag.LAST_SCORES_SCREEN");
                startActivity(lastScoresScreen);
                break;
            case R.id.exit_btn:
                android.os.Process.killProcess(android.os.Process.myPid());
                break;
        }
    }
}