package com.aston.blindfitness.View;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.aston.blindfitness.R;

import java.util.Calendar;

/**
 * MyFitness class to create a fitness regime for the user
 */
public class MyFitness extends AppCompatActivity {

    EditText heightText, weightText;
    static boolean savedToMainExerciseActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_fitness);
        weightText = findViewById(R.id.editText2);
        heightText = findViewById(R.id.editText3);

        SQLiteDatabase sqLiteDatabase = this.openOrCreateDatabase("exercise", MODE_PRIVATE, null); // Create an SQLDatabase called "exercise" to store user data

        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS exerciseTable(day_id INTEGER PRIMARY KEY, isFinished VARCHAR)");

        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS bmi(month VARCHAR, year INTEGER, bmi VARCHAR)");

        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS Heart_Rate" + "(day DATETIME DEFAULT (date('now', 'localtime')) PRIMARY KEY," + "rate INTEGER DEFAULT 0)");
    }

    /**
     * Save users body information
     *
     * @param view
     */
    public void SaveBodyDim(View view) {

        SharedPreferences sp = getSharedPreferences("myFitness", MODE_PRIVATE);
        boolean firstTime = sp.getBoolean("firstTime", true);

        if (weightText.getText().toString().length() > 0 && heightText.getText().toString().length() > 0 && heightText.getText().toString().charAt(heightText.getText().toString().length() - 1) != '.' && weightText.getText().toString().charAt(weightText.getText().toString().length() - 1) != '.') {
            if (firstTime) {

                SharedPreferences sharedPreferences = MyFitness.this.getSharedPreferences("myFitness", MODE_PRIVATE);

                Calendar calendar = Calendar.getInstance();
                int previousDay = calendar.get(Calendar.DATE);
                int previousMonth = calendar.get(Calendar.MONTH);
                int previousYear = calendar.get(Calendar.YEAR);

                sharedPreferences.edit().putInt("Date", previousDay).apply();
                sharedPreferences.edit().putInt("Month", previousMonth).apply();
                sharedPreferences.edit().putInt("Year", previousYear).apply();

                float weight = Float.parseFloat(weightText.getText().toString());
                float height = Float.parseFloat(heightText.getText().toString());
                sp.edit().putFloat("height", height).apply();
                sp.edit().putFloat("weight", weight).apply();
                sp.edit().putFloat("minweight", weight).apply();
                sp.edit().putFloat("maxweight", weight).apply();
                finish();
                //Toast.makeText(this, "Added", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, PlanActivity.class);
                startActivity(intent);
            } else {
                float weight = Float.parseFloat(weightText.getText().toString());
                float height = Float.parseFloat(heightText.getText().toString());
                sp.edit().putFloat("height", height).apply();
                sp.edit().putFloat("weight", weight).apply();
                float maxWeight = sp.getFloat("maxweight", 0);
                float minWeight = sp.getFloat("minweight", 0);

                SQLiteDatabase sqLiteDatabase = openOrCreateDatabase("exercise", MODE_PRIVATE, null);

                Calendar calendar = Calendar.getInstance();

                int month = calendar.get(Calendar.MONTH) + 1;
                int year = calendar.get(Calendar.YEAR);
                float bmi = weight / ((height / 100F) * (height / 100F));
                String bmiString = Float.toString(bmi);
                String stringMonth = "";
                switch (month) {
                    case 1:
                        stringMonth = "January";
                        break;
                    case 2:
                        stringMonth = "February";
                        break;
                    case 3:
                        stringMonth = "March";
                        break;
                    case 4:
                        stringMonth = "April";
                        break;
                    case 5:
                        stringMonth = "May";
                        break;
                    case 6:
                        stringMonth = "June";
                        break;
                    case 7:
                        stringMonth = "July";
                        break;
                    case 8:
                        stringMonth = "August";
                        break;
                    case 9:
                        stringMonth = "September";
                        break;
                    case 10:
                        stringMonth = "October";
                        break;
                    case 11:
                        stringMonth = "November";
                        break;
                    case 12:
                        stringMonth = "December";
                        break;
                }

                // Will be used to show a history of the users bmi
                sqLiteDatabase.execSQL("INSERT INTO bmi (month, year, bmi) VALUES ('" + stringMonth + "'," + year + ",'" + bmiString + "')");

                //Updating max weight and  min weight values
                if (minWeight > weight)
                    sp.edit().putFloat("minweight", weight).apply();
                else if (maxWeight < weight)
                    sp.edit().putFloat("maxweight", weight).apply();
                finish();
                savedToMainExerciseActivity = true;
                Toast.makeText(this, "Height and weight updated", Toast.LENGTH_SHORT).show();
            }
        } else
            Toast.makeText(this, "Please enter your details", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        savedToMainExerciseActivity = true;
    }
}
