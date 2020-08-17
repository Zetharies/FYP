package com.aston.blindfitness.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.Html;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.aston.blindfitness.R;
import com.aston.blindfitness.SharedPrefs;

import java.util.Locale;

/**
 * TTSActivity will provide text-to-specch options for the user to perform
 */
public class TTSActivity extends AppCompatActivity {

    SharedPrefs userPreferences;
    ActionBar actionBar;

    int image;
    String description;

    private Toolbar playtoolbar, fonttoolbar;
    ImageView image_preview;
    TextView tv;

    TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        userPreferences = new SharedPrefs(TTSActivity.this);
        if (userPreferences.loadDarkTheme() == true) {
            setTheme(R.style.DarkTheme);
        } else {
            setTheme(R.style.AppTheme);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tts);

        // Initialise Views
        //image_preview = findViewById(R.id.imageIv);
        playtoolbar = findViewById(R.id.tts_play_toolbar);
        fonttoolbar = findViewById(R.id.tts_font_toolbar);

        tv = findViewById(R.id.resultTv);
        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i != TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.UK);
                }
            }
        });

        if (getIntent() != null) {
            //image = getIntent().getIntExtra("image", -1);
            description = getIntent().getStringExtra("description");

            //image_preview.setImageResource(image);
            tv.setText(description);

        }

        playtoolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_play:
                        tts.speak(tv.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
                        return true;
                    case R.id.nav_stop:
                        tts.stop();
                        return true;
                }
                return false;
            }
        });

        fonttoolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_increase:
                        tv.setTextSize(0, tv.getTextSize() + 2.0f);
                        return true;
                    case R.id.nav_decrease:
                        tv.setTextSize(0, tv.getTextSize() - 2.0f);
                        return true;
                    case R.id.nav_share:
                        shareText(tv.getText().toString());
                        return true;
                }
                return false;
            }
        });

        if (userPreferences.loadDarkTheme()) {
            getSupportActionBar().setTitle(Html.fromHtml("Scan Result"));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } else {
            getSupportActionBar().setTitle(Html.fromHtml("Scan Result"));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        getSupportActionBar().setDisplayUseLogoEnabled(true);

    }

    private void shareText(String text) {
        // Have to concatenate tripName and tripDescription to share
        // Have to create a new intent to start share
        // Serving a Content URI to Another App
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "BlindFitness text document");
        intent.putExtra(Intent.EXTRA_TEXT, text); // Information to share
        startActivity(Intent.createChooser(intent, "Share via")); // Title to show in the dialog
    }

    @Override
    protected void onPause() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onPause();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
