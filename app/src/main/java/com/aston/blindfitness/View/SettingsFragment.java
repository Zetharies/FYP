package com.aston.blindfitness.View;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.aston.blindfitness.R;
import com.aston.blindfitness.SharedPrefs;

import java.util.Locale;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

/**
 * SettingsFragment will use preferences for settings user will be able to use
 */
public class SettingsFragment extends PreferenceFragmentCompat {

    SharedPrefs userPreferences;
    private Preference language, font;
    private SwitchPreferenceCompat darkTheme;
    private String full_Language, default_font;


    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        userPreferences = new SharedPrefs(getContext());
        if (userPreferences.loadDarkTheme() == true) {
            getActivity().setTheme(R.style.DarkTheme);
        } else {
            getActivity().setTheme(R.style.AppTheme);
        }
        setPreferencesFromResource(R.xml.preference_main, rootKey);
        full_Language = "English"; // Set default langage to be English first
        default_font = "Default"; // Set default font to be Default first
        loadLocale();

        // Initialise views
        darkTheme = (SwitchPreferenceCompat) findPreference("darkThemeSwitch");

        if (userPreferences.loadDarkTheme() == true) {
            darkTheme.setSummary("On");
            darkTheme.setChecked(true);
        }

        if (darkTheme.isChecked()) {
            darkTheme.setSummary("On");
        } else {
            darkTheme.setSummary("Use a darker theme to keep your eyes comfortable");
        }
        darkTheme.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (darkTheme.isChecked()) {
                    userPreferences.setDarkTheme(false);
                    darkTheme.setChecked(false);
                } else {
                    userPreferences.setDarkTheme(true);
                    darkTheme.setChecked(true);
                }
                restartApp();
                return false;
            }
        });

        language = findPreference("language");
        switch (full_Language) {
            case "English":
                language.setIcon(R.drawable.flag_united24);
                break;
            case "Arabic":
                language.setIcon(R.drawable.flag_arabic24);
                break;
            case "Bengali":
                language.setIcon(R.drawable.flag_bengali24);
                break;
            case "Chinese (Simplified)":
                language.setIcon(R.drawable.flag_china24);
                break;
            case "French":
                language.setIcon(R.drawable.flag_france24);
                break;
            case "Hindi":
                language.setIcon(R.drawable.flag_hindi24);
                break;
            case "Indonesian":
                language.setIcon(R.drawable.flag_indonesia24);
                break;
            case "Portuguese":
                language.setIcon(R.drawable.flag_portugal24);
                break;
            case "Russian":
                language.setIcon(R.drawable.flag_russia24);
                break;
            case "Spanish":
                language.setIcon(R.drawable.flag_spain24);
                break;
            default:
                language.setIcon(R.drawable.flag_united24);
                break;
        }
        language.setSummary(full_Language);
        language.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(getContext(), LanguageActivity.class);
                startActivityForResult(intent, 1111);
                ((Activity) getContext()).overridePendingTransition(R.anim.slide_up_animation, R.anim.no_change_animation);
                return true;
            }
        });

        font = findPreference("font");
        switch (default_font) {
            case "Default":
                default_font = "Default";
                break;
            case "Tiny":
                default_font = "Tiny";
                break;
            case "Extra small":
                default_font = "Extra small";
                break;
            case "Small":
                default_font = "Small";
                break;
            case "Medium":
                default_font = "Medium";
                break;
            case "Large":
                default_font = "Large";
                break;
            case "Extra Large":
                default_font = "Extra Large";
                break;
            case "Huge":
                default_font = "Huge";
                break;
        }
        font.setSummary(default_font);
        font.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(getContext(), FontActivity.class);
                startActivityForResult(intent, 2222);
                ((Activity) getContext()).overridePendingTransition(R.anim.slide_up_animation, R.anim.no_change_animation);
                return true;
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1111) {
            if (resultCode == RESULT_OK) {
                String result = data.getStringExtra("Returninglanguage");
                String language = result;
                if (language.equals("English")) {
                    setLocale("en");
                    recreateApp();
                } else if (language.equals("Arabic")) {
                    setLocale("ar");
                    recreateApp();
                } else if (language.equals("Bengali")) {
                    setLocale("bn");
                    recreateApp();
                } else if (language.equals("Chinese (Simplified)")) {
                    setLocale("zh");
                    recreateApp();
                } else if (language.equals("French")) {
                    setLocale("fr");
                    recreateApp();
                } else if (language.equals("Hindi")) {
                    setLocale("hi");
                    recreateApp();
                } else if (language.equals("Indonesian")) {
                    setLocale("in");
                    recreateApp();
                } else if (language.equals("Portuguese")) {
                    setLocale("pt");
                    recreateApp();
                } else if (language.equals("Russian")) {
                    setLocale("ru");
                    recreateApp();
                } else if (language.equals("Spanish")) {
                    setLocale("es");
                    recreateApp();
                }
            }
        }
        if (requestCode == 2222) {
            if (resultCode == RESULT_OK) {
                String result = data.getStringExtra("Returningfont");
                String font = result;
                if (font.equals("Default")) {
                    recreateApp();
                } else if (font.equals("Tiny")) {
                    recreateApp();
                } else if (font.equals("Extra small")) {
                    recreateApp();
                } else if (font.equals("Small")) {
                    recreateApp();
                } else if (font.equals("Medium")) {
                    recreateApp();
                } else if (font.equals("Large")) {
                    recreateApp();
                } else if (font.equals("Extra Large")) {
                    recreateApp();
                } else if (font.equals("Huge")) {
                    recreateApp();
                }
            }
        }
    }

    public void recreateApp() {
        Intent intent = new Intent(getContext(), SettingsActivity.class);
        startActivity(intent);
        getActivity().finishAffinity();
    }

    /**
     * Reload the layout
     */
    public void restartApp() {
        Intent intent = new Intent(getContext(), SettingsActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    private void setLocale(String language) {
        Locale locale = new Locale(language);
        locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getActivity().getResources().updateConfiguration(config, getActivity().getResources().getDisplayMetrics());

        // Save user data to shared preferences
        SharedPreferences.Editor editor = getActivity().getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putString("User_Language", language);
        editor.apply();
    }

    private void setFont(String font) {
        Locale locale = new Locale(font);
        locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getActivity().getResources().updateConfiguration(config, getActivity().getResources().getDisplayMetrics());

        // Save user data to shared preferences
        SharedPreferences.Editor editor = getActivity().getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putString("User_Font", font);
        editor.apply();
    }

    public void loadLocale() {
        SharedPreferences prefs = getActivity().getSharedPreferences("Settings", MODE_PRIVATE);
        String language = prefs.getString("User_Language", "");
        String font = prefs.getString("User_Font", "");
        setLocale(language);
        setFont(font);
        if (language.equals("en")) {
            full_Language = "English";
        } else if (language.equals("ar")) {
            full_Language = "Arabic";
        } else if (language.equals("bn")) {
            full_Language = "Bengali";
        } else if (language.equals("zh")) {
            full_Language = "Chinese (Simplified)";
        } else if (language.equals("fr")) {
            full_Language = "French";
        } else if (language.equals("hi")) {
            full_Language = "Hindi";
        } else if (language.equals("in")) {
            full_Language = "Indonesian";
        } else if (language.equals("pt")) {
            full_Language = "Portuguese";
        } else if (language.equals("ru")) {
            full_Language = "Russian";
        } else if (language.equals("es")) {
            full_Language = "Spanish";
        }

        if(font.equals("Default")){
            default_font = "Default";
        } else if (font.equals("Tiny")){
            default_font = "Tiny";
        } else if (font.equals("Extra small")){
            default_font = "Extra small";
        } else if (font.equals("Small")){
            default_font = "Small";
        } else if (font.equals("Medium")){
            default_font = "Medium";
        } else if (font.equals("Large")){
            default_font = "Large";
        } else if (font.equals("Extra Large")){
            default_font = "Extra Large";
        } else if (font.equals("Huge")){
            default_font = "Huge";
        }
    }



}
