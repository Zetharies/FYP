package com.aston.blindfitness.View;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.aston.blindfitness.R;
import com.aston.blindfitness.SharedPrefs;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * ProgressActivity class to show graph representation of users recorded heart rate history
 */
public class ProgressActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    SharedPrefs userPreferences;

    int yGraphMax;
    int xGraphSize;
    SQLiteDatabase sqLiteDatabase;
    GraphView graphView;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    TextView maxHeartRate, minHeartRate, avgHeartRate, minTargetTV, maxTargetTv, avgTargetTV, currentWeight, currentHeight, bmiTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        userPreferences = new SharedPrefs(ProgressActivity.this);

        if (userPreferences.loadDarkTheme() == true) {
            setTheme(R.style.DarkTheme);
        } else {
            setTheme(R.style.AppTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);
        graphView = findViewById(R.id.graph);
        sqLiteDatabase = this.openOrCreateDatabase("exercise", MODE_PRIVATE, null);

        // Initialise views
        minHeartRate = findViewById(R.id.minHeartRate);
        maxHeartRate = findViewById(R.id.maxHeartRate);
        avgHeartRate = findViewById(R.id.avgHeartRate);

        minTargetTV = findViewById(R.id.mintargetweight);
        maxTargetTv = findViewById(R.id.maxtargetweight);
        avgTargetTV = findViewById(R.id.averagetargetweight);

        currentHeight = findViewById(R.id.currentheight);
        currentWeight = findViewById(R.id.currentweight);
        bmiTv = findViewById(R.id.bmitv);

        // Show values for users weight and height and bmi:
        SharedPreferences sp = getSharedPreferences("myFitness", Context.MODE_PRIVATE);
        float current_Height = sp.getFloat("height",0);
        float current_Weight = sp.getFloat("weight",0);
        float bmi = current_Weight /((current_Height / 100.0F) * (current_Height / 100.0F));

        currentHeight.setText(getString(R.string.currentHeight) + " " + current_Height + " cm");
        currentWeight.setText(getString(R.string.currentWeight) + " " + current_Weight + " kg");

        String bmiText = getString(R.string.bmi) + " " + bmi;
        if (bmi < 18.5F)
            bmiText+=" (Underweight)";
        else if (bmi > 30.0F)
            bmiText+=" (Obese)";
        else if (bmi > 24F )
            bmiText+=" (Overweight)";
        else
            bmiText+= " (Healthy Weight)";
        bmiTv.setText(bmiText);

        // Default values for comparing heart rate with 20 year olds
        int minTarget = 100;
        int maxTarget = 170;
        int average = (minTarget + maxTarget) / 2;

        minTargetTV.setText(getString(R.string.minTargetRate) + " " + minTarget);
        maxTargetTv.setText(getString(R.string.maxTargetRate) + " " + maxTarget);
        avgTargetTV.setText(getString(R.string.avgTargetRate) + " " + average);

        ArrayList<ArrayList> rateHistory = getRateHistory();

        yGraphMax = 120; // Default num for y axis;
        xGraphSize = 0; // Default num for x axis;
        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(getDataPoint(rateHistory));
        series.setDrawBackground(true);
        //series.setColor(Color.rgb(59,80,99));
        //series.setBackgroundColor(Color.argb(100, 99,78,59));
        series.setBackgroundColor(Color.argb(100, 59, 80, 99));
        series.setDrawDataPoints(true);
        series.setThickness(10);
        graphView.addSeries(series);
        graphView.getGridLabelRenderer().setNumHorizontalLabels(xGraphSize);
        graphView.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.VERTICAL);
        graphView.getViewport().setMaxY(yGraphMax + 10);
        graphView.getViewport().setYAxisBoundsManual(true);




        //graphView.getGridLabelRenderer().setPadding(32);

        //Toast.makeText(ProgressActivity.this, "size:  " + xGraphSize, Toast.LENGTH_SHORT).show();

        /*
        GridLabelRenderer renderer = graphView.getGridLabelRenderer();
        renderer.setHorizontalLabelsAngle(90);
         */
        graphView.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {

                    String s = "" + (int) value;
                    String s1 = s.substring(0, 4);
                    String s2 = s.substring(4, 6);
                    String s3 = s.substring(6, 8);

                    String dashedString = s1 + "-" + s2 + "-" + s3;

                    //Toast.makeText(ProgressActivity.this, "lol: "+dashedString, Toast.LENGTH_SHORT).show();
                    Date date = null;

                    String dtStart = dashedString;
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    try {
                        date = format.parse(dtStart);
                        System.out.println(date);

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    String x = sdf.format(new Date((long) value));
                    //Toast.makeText(ProgressActivity.this, ""+x, Toast.LENGTH_SHORT).show();
                    return sdf.format(new Date(String.valueOf(date)));
                }
                return super.formatLabel(value, isValueX);
            }
        });

        GridLabelRenderer gridLabel = graphView.getGridLabelRenderer();
        gridLabel.setHorizontalAxisTitle("Date recorded");
        gridLabel.setVerticalAxisTitle("Heart Rate");
    }

    /**
     * @param history
     * @return array of datapoints to plot to the graph
     */
    private DataPoint[] getDataPoint(ArrayList<ArrayList> history) {

        ArrayList<String> vals = new ArrayList<>();
        ArrayList<String> xVals = new ArrayList<>();


        DataPoint[] dp = new DataPoint[history.get(0).size()];
        for (int i = 0; i < history.get(0).size(); i++) {
            vals.add(((String) history.get(0).get(i)));
            xVals.add((String) history.get(1).get(i));

            int x = Integer.parseInt((String) history.get(0).get(i));

            String lel = (String) ((String) history.get(1).get(i)).replace("-", "");

            Double y = Double.parseDouble(lel);
            int v = Integer.parseInt(lel);

            xGraphSize++;

            dp[i] = new DataPoint(v, x);
        }

        List<Integer> newList = new ArrayList<Integer>(vals.size());
        for (String myInt : vals) {
            newList.add(Integer.valueOf(myInt));
        }
        yGraphMax = Collections.max(newList); // Make the Y axis only show the top label as the highest heart rate recorded
        int min = Collections.min(newList); // Make the Y axis only show the top label as the highest heart rate recorded
        int avg = (yGraphMax + min) / 2;
        maxHeartRate.setText(getString(R.string.maxHeartRate) + " " + yGraphMax);
        minHeartRate.setText(getString(R.string.minHeartRate) + " " + min);
        avgHeartRate.setText(getString(R.string.avgHeartRate) + " " + avg);

        //Toast.makeText(this, "Max num is: " + yGraphMax, Toast.LENGTH_SHORT).show();
        //Toast.makeText(this, "" + vals.get(0), Toast.LENGTH_SHORT).show(); // works, but vals.get0 doesn't, fix
        //Toast.makeText(this, "" + xVals.get(0), Toast.LENGTH_SHORT).show(); // works, but vals.get0 doesn't, fix
        return dp;
    }

    /**
     * @return arraylist of rates and the day they were recorded
     */
    private ArrayList<ArrayList> getRateHistory() {
        ArrayList<ArrayList> rateArrayList = new ArrayList<>();

        rateArrayList.add(new ArrayList<String>());
        rateArrayList.add(new ArrayList<String>());

        try {
            Cursor c = sqLiteDatabase.rawQuery("SELECT day, rate FROM Heart_Rate ORDER BY day DESC LIMIT 365", null);

            c.moveToFirst();
            do {
                rateArrayList.get(0).add(c.getString(c.getColumnIndex("rate")));
                rateArrayList.get(1).add(c.getString(c.getColumnIndex("day")));

            } while (c.moveToNext());
            c.close();
        } catch (Exception e) {
            Log.e("ProgressActivity", "Error on gathering rate history\nStack Trace:\n" + Log.getStackTraceString(e));
        }

        Collections.reverse(rateArrayList.get(0));
        Collections.reverse(rateArrayList.get(1));

        return rateArrayList;
    }

    public void showComparisonPopUp(View v){
        PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.compare_heartrate_menu);
        popupMenu.show();
    }

    public void updateHeightAndWeight(View view)
    {
        finish();
        Intent intent = new Intent(this, MyFitness.class);
        startActivity(intent);
    }

    public void initDayToHeart_Rate() {
        String query = "INSERT INTO Heart_Rate (rate) VALUES (?)";
        SQLiteStatement statement = sqLiteDatabase.compileStatement(query);
        statement.bindString(1, "0");
        statement.execute();
    }

    public String getCurrentHeartRate() {
        Cursor c = sqLiteDatabase.rawQuery("SELECT rate FROM Heart_Rate WHERE day = date('now', 'localtime')", null);
        if (c.getCount() < 1) {
            sqLiteDatabase.execSQL("INSERT INTO Heart_Rate (rate) VALUES (0)");
            c = sqLiteDatabase.rawQuery("SELECT rate FROM Heart_Rate WHERE day = date('now', 'localtime')", null);
        }
        c.moveToFirst();

        return c.getString(c.getColumnIndex("rate"));
    }

    public String getTodaysHeartRate() {
        Cursor c = sqLiteDatabase.rawQuery("SELECT rate FROM Heart_Rate WHERE day = date('now', 'localtime')", null);
        c.moveToFirst();
        String rate = c.getString(c.getColumnIndex("rate"));
        c.close();

        return rate;
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        int minTarget;
        int average;
        int maxTarget;
        switch (menuItem.getItemId()){
            case R.id.age20:
                minTarget = 100;
                maxTarget = 170;
                average = (minTarget + maxTarget) / 2;
                minTargetTV.setText(getString(R.string.minTargetRate) + " " + minTarget);
                maxTargetTv.setText(getString(R.string.maxTargetRate) + " " + maxTarget);
                avgTargetTV.setText(getString(R.string.avgTargetRate) + " " + average);
                return true;
            case R.id.age30:
                minTarget = 95;
                maxTarget = 162;
                average = (minTarget + maxTarget) / 2;
                minTargetTV.setText(getString(R.string.minTargetRate) + " " + minTarget);
                maxTargetTv.setText(getString(R.string.maxTargetRate) + " " + maxTarget);
                avgTargetTV.setText(getString(R.string.avgTargetRate) + " " + average);
                return true;
            case R.id.age35:
                minTarget = 93;
                maxTarget = 157;
                average = (minTarget + maxTarget) / 2;
                minTargetTV.setText(getString(R.string.minTargetRate) + " " + minTarget);
                maxTargetTv.setText(getString(R.string.maxTargetRate) + " " + maxTarget);
                avgTargetTV.setText(getString(R.string.avgTargetRate) + " " + average);
                return true;
            case R.id.age40:
                minTarget = 90;
                maxTarget = 153;
                average = (minTarget + maxTarget) / 2;
                minTargetTV.setText(getString(R.string.minTargetRate) + " " + minTarget);
                maxTargetTv.setText(getString(R.string.maxTargetRate) + " " + maxTarget);
                avgTargetTV.setText(getString(R.string.avgTargetRate) + " " + average);
                return true;
            case R.id.age45:
                minTarget = 88;
                maxTarget = 149;
                average = (minTarget + maxTarget) / 2;
                minTargetTV.setText(getString(R.string.minTargetRate) + " " + minTarget);
                maxTargetTv.setText(getString(R.string.maxTargetRate) + " " + maxTarget);
                avgTargetTV.setText(getString(R.string.avgTargetRate) + " " + average);
                return true;
            case R.id.age50:
                minTarget = 85;
                maxTarget = 145;
                average = (minTarget + maxTarget) / 2;
                minTargetTV.setText(getString(R.string.minTargetRate) + " " + minTarget);
                maxTargetTv.setText(getString(R.string.maxTargetRate) + " " + maxTarget);
                avgTargetTV.setText(getString(R.string.avgTargetRate) + " " + average);
                return true;
            case R.id.age55:
                minTarget = 83;
                maxTarget = 140;
                average = (minTarget + maxTarget) / 2;
                minTargetTV.setText(getString(R.string.minTargetRate) + " " + minTarget);
                maxTargetTv.setText(getString(R.string.maxTargetRate) + " " + maxTarget);
                avgTargetTV.setText(getString(R.string.avgTargetRate) + " " + average);
                return true;
            case R.id.age60:
                minTarget = 80;
                maxTarget = 136;
                average = (minTarget + maxTarget) / 2;
                minTargetTV.setText(getString(R.string.minTargetRate) + " " + minTarget);
                maxTargetTv.setText(getString(R.string.maxTargetRate) + " " + maxTarget);
                avgTargetTV.setText(getString(R.string.avgTargetRate) + " " + average);
                return true;
            case R.id.age65:
                minTarget = 78;
                maxTarget = 132;
                average = (minTarget + maxTarget) / 2;
                minTargetTV.setText(getString(R.string.minTargetRate) + " " + minTarget);
                maxTargetTv.setText(getString(R.string.maxTargetRate) + " " + maxTarget);
                avgTargetTV.setText(getString(R.string.avgTargetRate) + " " + average);
                return true;
            case R.id.age70:
                minTarget = 75;
                maxTarget = 128;
                average = (minTarget + maxTarget) / 2;
                minTargetTV.setText(getString(R.string.minTargetRate) + " " + minTarget);
                maxTargetTv.setText(getString(R.string.maxTargetRate) + " " + maxTarget);
                avgTargetTV.setText(getString(R.string.avgTargetRate) + " " + average);
                return true;
            default:
                return false;
        }
    }
}
