package com.aston.blindfitness.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.aston.blindfitness.R;
import com.aston.blindfitness.SharedPrefs;

/**
 * ThemeActivity class is desined to give user the option to select the theme when using the application
 * Users can select between: Default; White-0n-Black; Black-On-White; Black-On-Yellow
 */
public class ThemeActivity extends AppCompatActivity {

    ActionBar actionBar;

    Button btnStart;
    ImageButton btnDefault, btnLight, btnDark, btnYellow;
    TextView selectedInfoTv, selectedTv;
    View views;
    LinearLayout themeSelectLL;

    SharedPrefs userPreferences;

    RadioButton rd;
    RadioButton rl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        userPreferences = new SharedPrefs(ThemeActivity.this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference);
        if (restorePrefData()) {
            Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(mainActivity);
            finish();
        }

        // Initialise views
        btnDefault = findViewById(R.id.defaultBtn);
        btnLight = findViewById(R.id.lightBtn);
        btnDark = findViewById(R.id.darkBtn);
        btnYellow = findViewById(R.id.yellowBtn);
        themeSelectLL = findViewById(R.id.themeSelectLL);
        btnStart = findViewById(R.id.start);
        selectedInfoTv = findViewById(R.id.selected_info_tv);
        selectedTv = findViewById(R.id.selected_theme_tv);
        views = findViewById(R.id.background);

        btnDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnDefault.setImageResource(R.drawable.defaultthemeselected2);
                btnLight.setImageResource(R.drawable.lighttheme);
                btnDark.setImageResource(R.drawable.darktheme);
                btnYellow.setImageResource(R.drawable.yellowtheme);

                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3b5063")));
                actionBar.setTitle(Html.fromHtml("<font color='#FFFFFF'>BlindFitness</font>"));
                updateStatusBarColor("#3b5063");
                views.setBackgroundResource(R.drawable.bgdefault);
                selectedTv.setBackgroundColor(Color.parseColor("#3b5063"));
                btnStart.setBackgroundColor(Color.parseColor("#3b5063"));
                themeSelectLL.setBackgroundColor(Color.parseColor("#1F3949"));

                userPreferences.setDefaultTheme(true);
                userPreferences.setDarkTheme(false);
                userPreferences.setLightTheme(false);
                userPreferences.setYellowTheme(false);
                selectedInfoTv.setTextColor(Color.WHITE);
                selectedTv.setText("Default Theme");
                selectedTv.setTextColor(Color.WHITE);
                btnStart.setTextColor(Color.WHITE);
            }
        });

        btnLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnDefault.setImageResource(R.drawable.defaulttheme);
                btnLight.setImageResource(R.drawable.lightthemeselected);
                btnDark.setImageResource(R.drawable.darktheme);
                btnYellow.setImageResource(R.drawable.yellowtheme);

                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#dadce0")));
                actionBar.setTitle(Html.fromHtml("<font color='#000000'>BlindFitness</font>"));
                updateStatusBarColor("#dadce0");
                views.setBackgroundResource(R.drawable.bglight);
                selectedTv.setBackgroundColor(Color.parseColor("#dadce0"));
                btnStart.setBackgroundColor(Color.parseColor("#dadce0"));
                themeSelectLL.setBackgroundColor(Color.parseColor("#e6e6e6"));

                userPreferences.setDefaultTheme(false);
                userPreferences.setDarkTheme(false);
                userPreferences.setLightTheme(true);
                userPreferences.setYellowTheme(false);
                selectedInfoTv.setTextColor(Color.BLACK);
                selectedTv.setText("Light Theme");
                selectedTv.setTextColor(Color.BLACK);
                btnStart.setTextColor(Color.BLACK);

            }
        });

        btnDark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnDefault.setImageResource(R.drawable.defaulttheme);
                btnLight.setImageResource(R.drawable.lighttheme);
                btnDark.setImageResource(R.drawable.darkthemeselected);
                btnYellow.setImageResource(R.drawable.yellowtheme);

                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1f1f1f")));
                actionBar.setTitle(Html.fromHtml("<font color='#FFFFFF'>BlindFitness</font>"));
                updateStatusBarColor("#1f1f1f");
                views.setBackgroundResource(R.drawable.bgdark);
                selectedTv.setBackgroundColor(Color.parseColor("#35363a"));
                btnStart.setBackgroundColor(Color.parseColor("#1f1f1f"));
                themeSelectLL.setBackgroundColor(Color.parseColor("#1f1f1f"));

                userPreferences.setDefaultTheme(false);
                userPreferences.setDarkTheme(true);
                userPreferences.setLightTheme(false);
                userPreferences.setYellowTheme(false);
                selectedInfoTv.setTextColor(Color.WHITE);
                selectedTv.setText("Dark Theme");
                selectedTv.setTextColor(Color.WHITE);
                btnStart.setTextColor(Color.WHITE);
            }
        });

        btnYellow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnDefault.setImageResource(R.drawable.defaulttheme);
                btnLight.setImageResource(R.drawable.lighttheme);
                btnDark.setImageResource(R.drawable.darktheme);
                btnYellow.setImageResource(R.drawable.yellowthemeselected);

                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ede1cb")));
                actionBar.setTitle(Html.fromHtml("<font color='#000000'>BlindFitness</font>"));
                updateStatusBarColor("#ede1cb");
                views.setBackgroundResource(R.drawable.bgyellow);
                selectedTv.setBackgroundColor(Color.parseColor("#e4dac8"));
                btnStart.setBackgroundColor(Color.parseColor("#dacfbc"));
                themeSelectLL.setBackgroundColor(Color.parseColor("#dacfbc"));
                //getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

                userPreferences.setDefaultTheme(false);
                userPreferences.setDarkTheme(false);
                userPreferences.setLightTheme(false);
                userPreferences.setYellowTheme(true);
                selectedInfoTv.setTextColor(Color.BLACK);
                selectedTv.setText("Yellow Theme");
                selectedTv.setTextColor(Color.BLACK);
                btnStart.setTextColor(Color.BLACK);
            }
        });


        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(mainActivity);
                finish();
                savePrefsData();
            }
        });

        actionBar = getSupportActionBar();
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
    }

    /*
    public void checkButton(View v){
        int radioId = radioGroup.getCheckedRadioButtonId();
        radioButton = (RadioButton) findViewById(radioId);

        switch(radioId){
            case R.id.darkRadio:
                rl.setButtonDrawable(R.drawable.lighttheme);
                radioButton.setButtonDrawable(R.drawable.darkselected);
                views.setBackgroundResource(R.drawable.bgdark);
                selectedInfoTv.setText("What theme would you like to use for BlindFitness?\n You can always change this later in settings.");
                selectedInfoTv.setTextColor(Color.WHITE);
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1f1f1f")));
                actionBar.setTitle(" " + Html.fromHtml("<font color='#fff'>BlindFitness</font>"));
                updateStatusBarColor("#000000");
                userPreferences.setDarkTheme(true);
                userPreferences.setLightTheme(false);
                userPreferences.setYellowTheme(false);
                break;
            case R.id.lightRadio:
                rd.setButtonDrawable(R.drawable.dark);
                radioButton.setButtonDrawable(R.drawable.lightthemeselected);
                views.setBackgroundResource(R.drawable.bglight);
                selectedInfoTv.setText("What theme would you like to use for BlindFitness?\n You can always change this later in settings.");
                selectedInfoTv.setTextColor(Color.BLACK);
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#33b2ff")));
                actionBar.setTitle(" " + Html.fromHtml("<font color='#000'>BlindFitness</font>"));
                updateStatusBarColor("#f2f2f2");
                userPreferences.setDarkTheme(false);
                userPreferences.setLightTheme(true);
                userPreferences.setYellowTheme(false);
                break;
            case R.id.yellowRadio:
                rd.setButtonDrawable(R.drawable.dark);
                rl.setButtonDrawable(R.drawable.lighttheme);
                radioButton.setButtonDrawable(R.drawable.yellowselected);
                views.setBackgroundResource(R.drawable.bgyellow);
                selectedInfoTv.setText("What theme would you like to use for BlindFitness?\n You can always change this later in settings.");
                selectedInfoTv.setTextColor(Color.BLACK);
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ede1cb")));
                Spannable text = new SpannableString(actionBar.getTitle());
                text.setSpan(new ForegroundColorSpan(Color.BLACK), 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                actionBar.setTitle(text);
                updateStatusBarColor("#ede1cb");
                userPreferences.setDarkTheme(false);
                userPreferences.setLightTheme(false);
                userPreferences.setYellowTheme(true);
                break;
        }
    }
     */

    /**
     * @return if intro has been opened
     */
    private boolean restorePrefData() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPreferences", MODE_PRIVATE);
        Boolean checkIntroActivityOpenedBefore = pref.getBoolean("checkIntroOpen", false);
        return checkIntroActivityOpenedBefore;
    }

    /**
     * Save users data once they have clicked continue
     */
    private void savePrefsData() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("checkIntroOpen", true);
        editor.commit();
    }

    public void updateStatusBarColor(String color) {// Color must be in hexadecimal format
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(color));
        }
    }
}
