package com.aston.blindfitness;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SharedPrefs class to save users data
 */
public class SharedPrefs {

    SharedPreferences userPreferences;

    public SharedPrefs(Context context) {
        userPreferences = context.getSharedPreferences("filename", Context.MODE_PRIVATE);
    }

    /**
     * Sets the light color scheme for the application
     * @param state
     */
    public void setLightTheme(Boolean state) {
        SharedPreferences.Editor editor = userPreferences.edit();
        editor.putBoolean("LightTheme", state);
        editor.apply();
    }

    /**
     * @return state of the light theme
     */
    public boolean loadLightTheme() {
        Boolean state = userPreferences.getBoolean("LightTheme", false);
        return state;
    }

    /**
     * Sets the default color scheme for the application
     * @param state
     */
    public void setDefaultTheme(Boolean state) {
        SharedPreferences.Editor editor = userPreferences.edit();
        editor.putBoolean("DefaultTheme", state);
        editor.apply();
    }

    /**
     * @return state of the default theme
     */
    public boolean loadDefaultTheme() {
        Boolean state = userPreferences.getBoolean("DefaultTheme", false);
        return state;
    }

    /**
     * Sets the dark color scheme for the application
     * @param state
     */

    public void setDarkTheme(Boolean state) {
        SharedPreferences.Editor editor = userPreferences.edit();
        editor.putBoolean("DarkTheme", state);
        editor.apply();
    }

    /**
     * @return state of the dark theme
     */

    public boolean loadDarkTheme() {
        Boolean state = userPreferences.getBoolean("DarkTheme", false);
        return state;
    }

    /**
     * Sets the yellow color scheme for the application
     * @param state
     */
    public void setYellowTheme(Boolean state) {
        SharedPreferences.Editor editor = userPreferences.edit();
        editor.putBoolean("YellowTheme", state);
        editor.apply();
    }

    /**
     * @return state of the yellow theme
     */
    public boolean loadYellowTheme() {
        Boolean state = userPreferences.getBoolean("YellowTheme", false);
        return state;
    }
}
