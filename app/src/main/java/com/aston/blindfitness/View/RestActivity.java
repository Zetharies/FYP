package com.aston.blindfitness.View;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Html;
import android.view.WindowManager;
import android.widget.TextView;

import com.aston.blindfitness.R;
import com.aston.blindfitness.SharedPrefs;

/**
 * RestActivity provides the user the ability to rest after each exercise
 */
public class RestActivity extends AppCompatActivity {

    SharedPrefs userPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        userPreferences = new SharedPrefs(RestActivity.this);

        if (userPreferences.loadDarkTheme() == true) {
            setTheme(R.style.DarkTheme);
        } else {
            setTheme(R.style.AppTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rest);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        final TextView break_timer_text = findViewById(R.id.break_timer);

        // Originally we would allow the user to rest for a certain duration, but due to a demo, the timer will be very short
        CountDownTimer timer = new CountDownTimer(1000, 1000) { // 40000
            @Override
            public void onTick(long millisUntilFinished) {
                String timer_text = "";
                int seconds = (int) millisUntilFinished / 1000;

                if (seconds < 10)
                    timer_text = "0:0" + seconds;
                else
                    timer_text = "0:" + seconds;

                break_timer_text.setText(timer_text);
            }

            @Override
            public void onFinish() {
                break_timer_text.setText("0:00");
                finish();
                Intent intent = new Intent(RestActivity.this, ExerciseActivity.class);
                startActivity(intent);
            }
        }.start();

        if (userPreferences.loadDarkTheme()) {
            getSupportActionBar().setTitle(Html.fromHtml("Break Time!"));
        } else {
            getSupportActionBar().setTitle(Html.fromHtml("Break Time!"));
        }
        getSupportActionBar().setDisplayUseLogoEnabled(true);
    }

    @Override
    public void onBackPressed() {
        // We won't allow the user to go back to a previous layout
    }
}
