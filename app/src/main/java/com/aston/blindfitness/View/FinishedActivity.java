package com.aston.blindfitness.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aston.blindfitness.R;
import com.aston.blindfitness.SharedPrefs;

public class FinishedActivity extends AppCompatActivity implements SensorEventListener {

    SharedPrefs userPreferences;
    private int heartRate;
    private TextView textView_heart_rate, textView_heart_rate_text;
    private ImageView imageView_heart;
    private Animation animation_InAndOut;

    private static final int STORAGE_PERMISSION_CODE = 23;
    private SensorManager mSensorManager;
    private Sensor heartSensor;

    MediaPlayer Finished_Exercise;
    MediaPlayer HeartRate_Added;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        userPreferences = new SharedPrefs(FinishedActivity.this);

        if (userPreferences.loadDarkTheme() == true) {
            setTheme(R.style.DarkTheme);
        } else {
            setTheme(R.style.AppTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finished);

        textView_heart_rate = findViewById(R.id.textView_heart_rate);
        textView_heart_rate_text = findViewById(R.id.textView_heart_rate_text);
        imageView_heart = findViewById(R.id.imageView_heart);
        animation_InAndOut = AnimationUtils.loadAnimation(this, R.anim.scale_up_down);
        Finished_Exercise = MediaPlayer.create(getApplicationContext(), R.raw.finished_exercise);
        HeartRate_Added = MediaPlayer.create(getApplicationContext(), R.raw.heartrate_added);

        Finished_Exercise.start();

        // Initialize temperature and step counter sensors
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        heartSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);

        if (heartSensor != null) {
            // Register heart rate sensor listener
            mSensorManager.registerListener(this, heartSensor, SensorManager.SENSOR_DELAY_NORMAL);
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.BODY_SENSORS}, STORAGE_PERMISSION_CODE);
        } else {
            textView_heart_rate_text.setText(R.string.step_sensor_is_not_available);
        }

        if (userPreferences.loadDarkTheme()) {
            getSupportActionBar().setTitle(Html.fromHtml("Finished Daily Workout"));
        } else {
            getSupportActionBar().setTitle(Html.fromHtml("Finished Daily Workout"));
        }
        getSupportActionBar().setDisplayUseLogoEnabled(true);
    }

    /**
     * Return to previous activity
     * @param view
     */
    public void returnActivity(View view) {
        getCurrentHeartRate();
        finish();
        Intent intent = new Intent(this, FitActivity.class);
        startActivity(intent);
    }

    /**
     * Get the users current heart rate using SQLite Database
     * @return users heart rate corresponding to the day recorded
     */
    public String getCurrentHeartRate() {
        int numRate = Integer.parseInt(textView_heart_rate.getText().toString()); // Parse the integer from the heart rate textview

        SQLiteDatabase sqLiteDatabase = openOrCreateDatabase("exercise", Context.MODE_PRIVATE, null);

        Cursor c = sqLiteDatabase.rawQuery("SELECT rate FROM Heart_Rate WHERE day = date('now', 'localtime')", null);
        if (c.getCount() < 1) {
            sqLiteDatabase.execSQL("INSERT INTO Heart_Rate (rate) VALUES ('" + numRate + "')");
            c = sqLiteDatabase.rawQuery("SELECT rate FROM Heart_Rate WHERE day = date('now', 'localtime')", null);
        }
        c.moveToFirst();

        return c.getString(c.getColumnIndex("rate"));
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_HEART_RATE) {
            Log.d("heartSensor", "onSensorChanged");
            int initialHeartRate = heartRate;
            heartRate = (int) sensorEvent.values[0];
            if (heartRate == 0) {
                return;
            } else if (initialHeartRate == 0) {
                textView_heart_rate_text.setText(getString(R.string.your_heart_rate_is));
                Log.d("heartSensor", "Your heart rate.");
                HeartRate_Added.start(); // Play the heart rate added media so user can hear confirmation of the heart rate recorded
            }
            String heartRateStr = "" + heartRate;
            textView_heart_rate.setText(heartRateStr);
            textView_heart_rate.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        if (sensor.getType() == Sensor.TYPE_HEART_RATE) {
            Log.d("heartSensor", "onAccuracyChanged");
            if (i == SensorManager.SENSOR_STATUS_NO_CONTACT) {
                textView_heart_rate_text.setText(R.string.place_your_finger_on_the_sensor);
                imageView_heart.clearAnimation();
                animation_InAndOut.cancel();
                animation_InAndOut.reset();
            } else if (i == SensorManager.SENSOR_STATUS_UNRELIABLE) {
                if (heartRate == 0) {
                    textView_heart_rate_text.setText(R.string.place_your_finger_properly);
                }
                imageView_heart.clearAnimation();
                animation_InAndOut.cancel();
                animation_InAndOut.reset();
            } else {
                imageView_heart.startAnimation(animation_InAndOut);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case STORAGE_PERMISSION_CODE:
                if (grantResults.length > 0) {
                    boolean sensorAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (sensorAccepted) {
                        String heartRateStr = "" + heartRate;
                        textView_heart_rate.setText(heartRateStr);
                        textView_heart_rate.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(this, "BlindFitness needs Sensor permission to perform this action", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, heartSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onBackPressed() {
        finish();
        Intent intent = new Intent(this, FitActivity.class);
        startActivity(intent);
    }
}
