package com.aston.blindfitness.View;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.aston.blindfitness.R;
import com.aston.blindfitness.SharedPrefs;

import java.util.Calendar;

/**
 * PlanActivity class is designed to create a fitness regime for the user
 * The user can select the number of days they want to train for as well as the level of training
 */
public class PlanActivity extends AppCompatActivity {

    SharedPrefs userPreferences;
    SharedPreferences sharedPreferences;
    RadioGroup modeGroup;
    RadioGroup dayGroup;
    static boolean passedToHomeScreen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        userPreferences = new SharedPrefs(PlanActivity.this);

        if (userPreferences.loadDarkTheme() == true) {
            setTheme(R.style.DarkTheme);
        } else {
            setTheme(R.style.AppTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan);

        modeGroup = findViewById(R.id.moderadiogroup);
        dayGroup = findViewById(R.id.dayradiogroup);

        sharedPreferences = getSharedPreferences("myFitness", MODE_PRIVATE);

        if (userPreferences.loadDarkTheme()) {
            getSupportActionBar().setTitle(Html.fromHtml("Update Plan"));
        } else {
            getSupportActionBar().setTitle(Html.fromHtml("Update Plan"));
        }
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        if (sharedPreferences.getBoolean("isBeginner", true)) {
            RadioButton beginner = (RadioButton) findViewById(R.id.beginner);
            beginner.setChecked(true);
        } else if (sharedPreferences.getBoolean("isIntermediate", true)) {
            RadioButton intermediate = (RadioButton) findViewById(R.id.intermediate);
            intermediate.setChecked(true);
        } else if (sharedPreferences.getBoolean("isAdvanced", true)){
            RadioButton advanced = (RadioButton) findViewById(R.id.advanced);
            advanced.setChecked(true);
        }

        if (sharedPreferences.getInt("days", 15) == 15) {
            RadioButton d = (RadioButton) findViewById(R.id.duration_thirty_days);
            d.setChecked(true);
        } else if (sharedPreferences.getInt("days", 30) == 30) {
            RadioButton e = (RadioButton) findViewById(R.id.duration_thirty_days);
            e.setChecked(true);
        } else if (sharedPreferences.getInt("days", 45) == 45) {
            RadioButton f = (RadioButton) findViewById(R.id.duration_fortyfive_days);
            f.setChecked(true);
        } else if (sharedPreferences.getInt("days", 60) == 60) {
            RadioButton g = (RadioButton) findViewById(R.id.duration_sixty_days);
            g.setChecked(true);
        } else if (sharedPreferences.getInt("days", 75) == 75) {
            RadioButton h = (RadioButton) findViewById(R.id.duration_seventyfive_days);
            h.setChecked(true);
        }
    }

    /**
     * Provides user with the ability to update their plan at anytime
     * @param view
     */
    public void UpdatePlan(View view) {
        //Editing mode plan
        /*
        if (modeGroup.getCheckedRadioButtonId() == R.id.beginner)
            sharedPreferences.edit().putBoolean("isBeginner", true).apply();
        else
            sharedPreferences.edit().putBoolean("isBeginner", false).apply();

         */

        if (modeGroup.getCheckedRadioButtonId() == R.id.beginner){
            sharedPreferences.edit().putBoolean("isBeginner", true).apply();
            sharedPreferences.edit().putBoolean("isIntermediate", false).apply();
            sharedPreferences.edit().putBoolean("isAdvanced", false).apply();
        } else if (modeGroup.getCheckedRadioButtonId() == R.id.intermediate){
            sharedPreferences.edit().putBoolean("isIntermediate", true).apply();
            sharedPreferences.edit().putBoolean("isBeginner", false).apply();
            sharedPreferences.edit().putBoolean("isAdvanced", false).apply();
        } else if (modeGroup.getCheckedRadioButtonId() == R.id.advanced) {
            sharedPreferences.edit().putBoolean("isAdvanced", true).apply();
            sharedPreferences.edit().putBoolean("isBeginner", false).apply();
            sharedPreferences.edit().putBoolean("isIntermediate", false).apply();
        }

        //Editing day plan
        if (dayGroup.getCheckedRadioButtonId() == R.id.duration_fifteen_days)
            sharedPreferences.edit().putInt("days", 15).apply();
        else if (dayGroup.getCheckedRadioButtonId() == R.id.duration_thirty_days)
            sharedPreferences.edit().putInt("days", 30).apply();
        else if (dayGroup.getCheckedRadioButtonId() == R.id.duration_fortyfive_days)
            sharedPreferences.edit().putInt("days", 45).apply();
        else if (dayGroup.getCheckedRadioButtonId() == R.id.duration_sixty_days)
            sharedPreferences.edit().putInt("days", 60).apply();
        else if (dayGroup.getCheckedRadioButtonId() == R.id.duration_seventyfive_days)
            sharedPreferences.edit().putInt("days", 75).apply();

        int days = sharedPreferences.getInt("days", 15);
        SQLiteDatabase sqLiteDatabase = this.openOrCreateDatabase("exercise", MODE_PRIVATE, null);
        sqLiteDatabase.execSQL("DELETE FROM exerciseTable");
        for (int i = 1; i <= days; i++) {
            sqLiteDatabase.execSQL("INSERT INTO exerciseTable(isFinished) VALUES ('Pending')");
        }

        passedToHomeScreen = !sharedPreferences.getBoolean("firstTime", false);

        sharedPreferences.edit().putBoolean("firstTime", false).apply();
        Calendar calendar = Calendar.getInstance();
        int previousDay = calendar.get(Calendar.DATE);
        int previousMonth = calendar.get(Calendar.MONTH);
        int previousYear = calendar.get(Calendar.YEAR);

        sharedPreferences.edit().putInt("Date", previousDay).apply();
        sharedPreferences.edit().putInt("Month", previousMonth).apply();
        sharedPreferences.edit().putInt("Year", previousYear).apply();

        sharedPreferences.edit().putInt("day_number", 1).apply();
        //Toast.makeText(this, "Fitness regime changed", Toast.LENGTH_SHORT).show();
        finish();
        Intent intent = new Intent(this, FitActivity.class);
        startActivity(intent);

    }
}
