package com.aston.blindfitness.View;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.aston.blindfitness.View.PreferenceActivity;

/**
 * SplashScreen class to show an introduction to the app
 */
public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, PreferenceActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION); // To to remove screen flicker
        startActivity(intent);
        finish();
        overridePendingTransition(0, 0); // To to remove screen flicker
    }
}
