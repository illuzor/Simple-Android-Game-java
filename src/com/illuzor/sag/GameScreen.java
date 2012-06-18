package com.illuzor.sag;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.*;

import java.util.List;

public class GameScreen extends Activity implements View.OnTouchListener, SensorEventListener {

    boolean sensorActivated = false;
    GameView gameView;
    SensorManager sensorManager;
    Sensor accelerometer;
    Display display;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gameView = new GameView(this, this);
        setContentView(gameView);
        initControlType();
    }

    @Override
    protected void onPause() {
        gameView.pause();
        if (sensorActivated) sensorManager.unregisterListener(this);
        super.onPause();
    }

    @Override
    protected void onResume() {
        gameView.resume();
        if (sensorActivated) sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        super.onResume();
    }

    @Override
    public boolean onTouch(View view, MotionEvent e) {
        gameView.movePlatformTo((int) e.getX());
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    private void initControlType() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        int controlType = Integer.parseInt(prefs.getString("control", "0"));

        switch (controlType) {
            case 0:
                gameView.setOnTouchListener(this);
                break;
            case 1:
                initAccelerometer();
                break;
        }
    }

    private void initAccelerometer() {
        WindowManager mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        display = mWindowManager.getDefaultDisplay();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);

        if (sensors.size() > 0) {
            for (Sensor sensor : sensors) {
                if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                    accelerometer = sensor;
                    sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
                    sensorActivated = true;
                    break;
                }
            }
        } else {
            gameView.setOnTouchListener(this);
        }
    }

    public void showScores(int numScores) {
        Intent scoreScreenIntent = new Intent();
        scoreScreenIntent.setClass(this, ScoresScreen.class);
        scoreScreenIntent.putExtra("sc", numScores);
        startActivity(scoreScreenIntent);
    }

    @Override
    public void onSensorChanged(SensorEvent e) {
        if (e.sensor.getType() != Sensor.TYPE_ACCELEROMETER)
            return;

        if (display.getRotation() == Surface.ROTATION_0) {
            double aspect = e.values[0] / 10 + 0.5;
            if (e.values[0] < -5) aspect = 0;
            if (e.values[0] > 5) aspect = 1;
            gameView.movePlatformTo(1 - aspect);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        // функция не нужна, но она её существование необходимо при имплементации  SensorEventListener
    }
}
