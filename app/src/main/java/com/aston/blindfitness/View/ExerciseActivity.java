package com.aston.blindfitness.View;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Html;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.aston.blindfitness.R;
import com.aston.blindfitness.SharedPrefs;

public class ExerciseActivity extends AppCompatActivity {

    SharedPrefs userPreferences;
    TextView exercise_name, numMinutes, timer_text, exerciseDetail;
    ImageView exercise_photo;
    Button beginSession;
    MediaPlayer mediaPlayer;
    long duration = 60000;

    /**
     * Countdown timer for our exercises.
     * Sets up music and starts countdown for each exercise
     *
     * @param duration
     */
    public void beginTimer(long duration) {
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.music2); // Calm music to play while performing exercises
        CountDownTimer timer = new CountDownTimer(duration, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                String text_of_timer = "";
                int minutes = (int) millisUntilFinished / 60000;
                int seconds = (int) (millisUntilFinished % 60000) / 1000;

                AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

                mediaPlayer.start();
                String secondString = "";

                if (seconds < 10) {
                    secondString = "0" + seconds;
                } else
                    secondString = Integer.toString(seconds);

                text_of_timer = minutes + ":" + secondString;

                timer_text.setText(text_of_timer);
            }

            @Override
            public void onFinish() {
                timer_text.setText("0:00");
                DailyExerciseActivity.exercise_finished++;
                finish();
                Intent intent;
                mediaPlayer.pause();
                if (DailyExerciseActivity.exercise_finished == 5) {
                    SQLiteDatabase sqLiteDatabase = openOrCreateDatabase("exercise", MODE_PRIVATE, null);
                    SharedPreferences sp = getSharedPreferences("myFitness", MODE_PRIVATE);
                    int day_number = sp.getInt("day_number", 1);

                    sqLiteDatabase.execSQL("UPDATE exerciseTable set isFinished = 'Yes' WHERE day_id = " + day_number + "");

                    intent = new Intent(ExerciseActivity.this, FinishedActivity.class);
                } else {
                    intent = new Intent(ExerciseActivity.this, RestActivity.class);
                }
                startActivity(intent);
            }
        }.start();
    }

    /**
     * Method to call our beginTimer method
     *
     * @param view
     */
    public void startTimer(View view) {
        Button go_button = (Button) view;
        go_button.setEnabled(false);
        beginTimer(duration);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        userPreferences = new SharedPrefs(ExerciseActivity.this);

        if (userPreferences.loadDarkTheme() == true) {
            setTheme(R.style.DarkTheme);
        } else {
            setTheme(R.style.AppTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);

        exercise_name = findViewById(R.id.exercise_name);
        numMinutes = findViewById(R.id.numMinutes);
        exerciseDetail = findViewById(R.id.exerciseDetail);
        timer_text = findViewById(R.id.timer_text);
        exercise_photo = findViewById(R.id.exercise_photo);
        beginSession = findViewById(R.id.beginSession);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        exercise_name.setText(DailyExerciseActivity.exerciseList.get(DailyExerciseActivity.exercise_finished));

        numMinutes.setText(DailyExerciseActivity.durationList.get(DailyExerciseActivity.exercise_finished));

        timer_text.setText(DailyExerciseActivity.durationList.get(DailyExerciseActivity.exercise_finished));

        switch (DailyExerciseActivity.durationList.get(DailyExerciseActivity.exercise_finished)) {
            case "1:00 Minute":
                timer_text.setText("1:00"); // Default is 1 minute, but for the sake of a Demo, we will leave it at 1000 which is 1 second
                duration = 1000; // 60000
                break;
            case "2:00 Minutes":
                timer_text.setText("2:00"); // Default is 2 minutes, but for the sake of a Demo, we will leave it at 2000 which is 2 seconds
                duration = 2000; // 120000
                break;
            case "3:00 Minutes":
                timer_text.setText("3:00"); // Default is 3 minutes, but for the sake of a Demo, we will leave it at 3000 which is 3 seconds
                duration = 3000; // 180000
                break;
            case "4:00 Minutes":
                timer_text.setText("4:00"); // Default is 3 minutes, but for the sake of a Demo, we will leave it at 3000 which is 3 seconds
                duration = 4000; // 240000
                break;
            case "5:00 Minutes":
                timer_text.setText("5:00"); // Default is 3 minutes, but for the sake of a Demo, we will leave it at 3000 which is 3 seconds
                duration = 5000; // 300000
                break;
        }

        if (DailyExerciseActivity.exercise_finished >= 1) {
            beginSession.setVisibility(View.INVISIBLE);
            beginTimer(duration);
        }

        String exerciseName = DailyExerciseActivity.exerciseList.get(DailyExerciseActivity.exercise_finished);

        switch (exerciseName) {
            case "Push Ups":
                exerciseDetail.setText(getString(R.string.push_ups));
                exercise_photo.setImageResource(R.drawable.pushups);
                break;
            case "Pull Ups":
                exerciseDetail.setText(getString(R.string.pull_ups));
                exercise_photo.setImageResource(R.drawable.pullups);
                break;
            case "Cobra Stretch":
                exerciseDetail.setText(getString(R.string.cobra_stretch));
                exercise_photo.setImageResource(R.drawable.cobra);
                break;
            case "Crunches":
                exerciseDetail.setText(getString(R.string.crunches));
                exercise_photo.setImageResource(R.drawable.crunches);
                break;
            case "Plank":
                exerciseDetail.setText(getString(R.string.plank));
                exercise_photo.setImageResource(R.drawable.plank);
                break;
            case "Lunges":
                exerciseDetail.setText(getString(R.string.lunges));
                exercise_photo.setImageResource(R.drawable.lunges);
                break;
            case "Side Plank":
                exerciseDetail.setText(getString(R.string.side_pank));
                exercise_photo.setImageResource(R.drawable.sideplank);
                break;
            case "Squats":
                exerciseDetail.setText(getString(R.string.squats));
                exercise_photo.setImageResource(R.drawable.squats);
                break;
            case "Skipping":
                exerciseDetail.setText(getString(R.string.skipping));
                exercise_photo.setImageResource(R.drawable.skipping);
                break;
        }

        String fitnessPlan = null; // A string to contain which fitness level the user selected

        /*
        SharedPreferences sharedPreferences = getSharedPreferences("myFitness", Context.MODE_PRIVATE);
        if (sharedPreferences.getBoolean("isBeginner", true)) {
            fitnessPlan = "Beginner";
        } else {
            fitnessPlan = "Advanced";
        }

         */

        SharedPreferences sharedPreferences = getSharedPreferences("myFitness", Context.MODE_PRIVATE);
        if (sharedPreferences.getBoolean("isBeginner", true)) {
            fitnessPlan = "Beginner";
        } else if (sharedPreferences.getBoolean("isIntermediate", true)) {
            fitnessPlan = "Intermediate";
        } else if (sharedPreferences.getBoolean("isAdvanced", true)){
            fitnessPlan = "Advanced";
        }
        if (userPreferences.loadDarkTheme()) {
            getSupportActionBar().setTitle(Html.fromHtml(fitnessPlan + " Workout | " + exerciseName)); // Set the action bar name to be "Workout | " + the current exercise name
        } else {
            getSupportActionBar().setTitle(Html.fromHtml(fitnessPlan + " Workout | " + exerciseName));
        }
        getSupportActionBar().setDisplayUseLogoEnabled(true);
    }

    // We won't allow users to go back to the previous screen.
    @Override
    public void onBackPressed() {

    }

}
