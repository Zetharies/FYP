package com.aston.blindfitness.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;

import com.aston.blindfitness.R;
import com.aston.blindfitness.SharedPrefs;

import java.util.ArrayList;
import java.util.Calendar;

public class FitActivity extends AppCompatActivity {

    SharedPrefs userPreferences;
    static Fragment fragment;
    static Fragment fragment2;
    static Button button;
    static int day_number = 1;
    static Button button3;

    ArrayList no_of_days;

    /*

    public void changeWeightHeight(View view)
    {
        finish();
        Intent intent = new Intent(this,BodyActivity.class);
        startActivity(intent);
    }

    public void bmiHistory(View view)
    {
        Intent intent = new Intent(this,BmiHistory.class);
        startActivity(intent);
    }

     */

    /**
     * Send user to activity where they can change their fitness plan
     * @param view
     */
    public void changePlan(View view) {
        finish();
        Intent intent = new Intent(this, PlanActivity.class);
        startActivity(intent);
    }

    /**
     * Send user to activity where they can begin their daily workout
     * @param view
     */
    public void startWorkout(View view) {
        finish();
        Intent intent = new Intent(this, DailyExerciseActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        userPreferences = new SharedPrefs(FitActivity.this);

        if (userPreferences.loadDarkTheme() == true) {
            setTheme(R.style.DarkTheme);
        } else {
            setTheme(R.style.AppTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fit);

        //androidx.appcompat.widget.Toolbar myToolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(myToolbar);

        /*

        if (PlanActivity.savedToMainExerciseActivity) {
            fragment = new MeFragment();
            PlanActivity.savedToMainExerciseActivity = false;
        }
        else if (MyFitness.savedToMainExerciseActivity) {
            fragment = new ReportFragment();
            MyFitness.savedToMainExerciseActivity = false;
        }
        else

         */

        button = findViewById(R.id.button);
        button3 = findViewById(R.id.button3);

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FitActivity.this, ProgressActivity.class);
                startActivity(intent);
            }
        });


        SharedPreferences sp = getSharedPreferences("myFitness", Context.MODE_PRIVATE);

        if (sp.getBoolean("doneToday", false)) {
            button.setEnabled(false);
        }

        ArrayList<String> list = new ArrayList<String>();

        int no_of_days = sp.getInt("days", 15);

        list.clear();

        for (int i = 1; i <= no_of_days; i++) {
            list.add("Day " + i);
        }

        day_number = sp.getInt("day_number", 1);

        SQLiteDatabase sqLiteDatabase = openOrCreateDatabase("exercise", Context.MODE_PRIVATE, null);

        Calendar calendar = Calendar.getInstance();

        Calendar previousOpenCalendar = Calendar.getInstance();

        int date = sp.getInt("Date", 0);
        int month = sp.getInt("Month", 0);
        int year = sp.getInt("Year", 0);

        previousOpenCalendar.set(Calendar.DATE, date);
        previousOpenCalendar.set(Calendar.MONTH, month);
        previousOpenCalendar.set(Calendar.YEAR, year);

        ArrayList<String> doneList = new ArrayList<>();

        doneList.clear();

        Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM exerciseTable", null);

        int doneIndex = c.getColumnIndex("isFinished");

        c.moveToFirst();

        while (!c.isAfterLast()) {
            doneList.add(c.getString(doneIndex));
            c.moveToNext();
        }

        while (calendar.after(previousOpenCalendar)) {
            previousOpenCalendar.add(Calendar.DATE, 1);
            if (doneList.get(day_number - 1).contentEquals("Pending"))
                sqLiteDatabase.execSQL("UPDATE exerciseTable set isFinished = 'No' WHERE day_id = " + day_number + "");
            day_number++;
        }

        if (doneList.get(day_number - 1).contentEquals("Yes")) {
            button.setEnabled(false);
            button.setText("FINISHED TODAY");
            button.setBackgroundColor(Color.parseColor("#8C3b5063"));
        }

        sp.edit().putInt("Date", calendar.get(Calendar.DATE)).apply();
        sp.edit().putInt("Month", calendar.get(Calendar.MONTH)).apply();
        sp.edit().putInt("Year", calendar.get(Calendar.YEAR)).apply();

        sp.edit().putInt("day_number", day_number).apply();

        doneList.clear();

        if (MyFitness.savedToMainExerciseActivity) {
            fragment = new ReportFragment();
            MyFitness.savedToMainExerciseActivity = false;
        } else
            fragment2 = new HomeFragment();
            fragment = new MeFragment();

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container2, fragment2).commit();

        if (userPreferences.loadDarkTheme()) {
            getSupportActionBar().setTitle(Html.fromHtml("My Fitness"));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } else {
            getSupportActionBar().setTitle(Html.fromHtml("My Fitness"));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        getSupportActionBar().setDisplayUseLogoEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
