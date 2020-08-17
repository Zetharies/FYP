package com.aston.blindfitness.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.aston.blindfitness.R;

import java.util.Locale;

public class PreferenceActivity extends AppCompatActivity {

    private Button language_btn, font_btn, continue_btn;
    public static final int REQUEST_CODE_GET_LANGUAGE = 1337;
    private static final String TAG = "PreferencesActivity";
    private static final int AUTHUI_REQUEST_CODE = 10000;
    public static String selectedLanguage, selectedFont;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_preference);
        loadLocale();

        if (restorePrefData()) {
            Intent mainActivity = new Intent(getApplicationContext(), ThemeActivity.class);
            startActivity(mainActivity);
            finish();
        }

        final String text = language_btn.getText().toString();


        language_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PreferenceActivity.this, LanguageActivity.class);
                //String language = "English";
                //intent.putExtra("English", language);
                startActivityForResult(intent, 1337);
                overridePendingTransition(R.anim.slide_up_animation, R.anim.no_change_animation);
            }
        });

        font_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PreferenceActivity.this, FontActivity.class);
                //String language = "English";
                //intent.putExtra("English", language);
                startActivityForResult(intent, 1338);
                overridePendingTransition(R.anim.slide_up_animation, R.anim.no_change_animation);
            }
        });

        continue_btn = findViewById(R.id.preference_continue);
        continue_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //handleLoginRequest(view);
                Intent intent = new Intent(PreferenceActivity.this, ThemeActivity.class);
                //intent.putExtra("GetCallingActivityLogin", 1992);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.slide_up_animation, R.anim.no_change_animation);
                savePrefsData();
            }
        });
    }

    /**
     * Method to restart the class
     */
    public void recreateApp() {
        Intent intent = new Intent(getApplicationContext(), PreferenceActivity.class);
        startActivity(intent);
        finishAffinity();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1337) {
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
        if (requestCode == 1338) {
            if (resultCode == RESULT_OK) {
                String resultFont = data.getStringExtra("Returningfont");
                String font = resultFont;

                if (font.equals("Default")) {
                    setFont("de");
                    recreateApp();
                } else if (font.equals("Tiny")) {
                    setFont("ti");
                    recreateApp();
                } else if (font.equals("Extra small")) {
                    setFont("es");
                    recreateApp();
                } else if (font.equals("Small")) {
                    setFont("sm");
                    recreateApp();
                } else if (font.equals("Medium")) {
                    setFont("me");
                    recreateApp();
                } else if (font.equals("Large")) {
                    setFont("la");
                    recreateApp();
                } else if (font.equals("Extra Large")) {
                    setFont("el");
                    recreateApp();
                } else if (font.equals("Huge")) {
                    setFont("hu");
                    recreateApp();
                }
            }
        }
    }

    /**
     * Set locale for users chosen language
     *
     * @param language
     */
    private void setLocale(String language) {
        Locale locale = new Locale(language);
        locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

        // Save user data to shared preferences
        SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putString("User_Language", language);
        editor.apply();
    }

    /**
     * Set font for users chosen font size
     *
     * @param font
     */
    private void setFont(String font) {
        Locale locale = new Locale(font);
        locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

        // Save user data to shared preferences
        SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putString("User_Font", font);
        editor.apply();
    }

    /**
     * Load users selected language and font size
     */
    public void loadLocale() {
        SharedPreferences prefs = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String language = prefs.getString("User_Language", "");
        String font = prefs.getString("User_Font", "");
        setLocale(language);
        setFont(font);
        String full_Language = "English";
        String font_size = "Default";

        selectedLanguage = full_Language;
        selectedFont = font_size;

        language_btn = findViewById(R.id.language);
        font_btn = findViewById(R.id.font);

        if (language.equals("en")) {
            full_Language = "English";
            selectedLanguage = "English";
            language_btn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.flag_united24, 0, R.drawable.ic_arrow_drop_down_black_24dp, 0);
        } else if (language.equals("ar")) {
            full_Language = "Arabic";
            selectedLanguage = "Arabic";
            language_btn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.flag_arabic24, 0, R.drawable.ic_arrow_drop_down_black_24dp, 0);
        } else if (language.equals("bn")) {
            full_Language = "Bengali";
            selectedLanguage = "Bengali";
            language_btn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.flag_bengali24, 0, R.drawable.ic_arrow_drop_down_black_24dp, 0);
        } else if (language.equals("zh")) {
            full_Language = "Chinese (Simplified)";
            selectedLanguage = "Chinese (Simplified)";
            language_btn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.flag_china24, 0, R.drawable.ic_arrow_drop_down_black_24dp, 0);
        } else if (language.equals("fr")) {
            full_Language = "French";
            selectedLanguage = "French";
            language_btn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.flag_france24, 0, R.drawable.ic_arrow_drop_down_black_24dp, 0);
        } else if (language.equals("hi")) {
            full_Language = "Hindi";
            selectedLanguage = "Hindi";
            language_btn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.flag_hindi24, 0, R.drawable.ic_arrow_drop_down_black_24dp, 0);
        } else if (language.equals("in")) {
            full_Language = "Indonesian";
            selectedLanguage = "Indonesian";
            language_btn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.flag_indonesia24, 0, R.drawable.ic_arrow_drop_down_black_24dp, 0);
        } else if (language.equals("pt")) {
            full_Language = "Portuguese";
            selectedLanguage = "Portuguese";
            language_btn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.flag_portugal24, 0, R.drawable.ic_arrow_drop_down_black_24dp, 0);
        } else if (language.equals("ru")) {
            full_Language = "Russian";
            selectedLanguage = "Russian";
            language_btn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.flag_russia24, 0, R.drawable.ic_arrow_drop_down_black_24dp, 0);
        } else if (language.equals("es")) {
            full_Language = "Spanish";
            selectedLanguage = "Spanish";
            language_btn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.flag_spain24, 0, R.drawable.ic_arrow_drop_down_black_24dp, 0);
        }
        language_btn.setText(full_Language);

        if (font.equals("de")) {
            font_size = "Default";
            selectedFont = "Default";
            font_btn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.font_increase24, 0, R.drawable.ic_arrow_drop_down_black_24dp, 0);
        } else if (font.equals("ti")) {
            font_size = "Tiny";
            selectedFont = "Tiny";
            font_btn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.font_increase24, 0, R.drawable.ic_arrow_drop_down_black_24dp, 0);
        } else if (font.equals("es")) {
            font_size = "Extra small";
            selectedFont = "Extra small";
            font_btn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.font_increase24, 0, R.drawable.ic_arrow_drop_down_black_24dp, 0);
        } else if (font.equals("sm")) {
            font_size = "Small";
            selectedFont = "Small";
            font_btn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.font_increase24, 0, R.drawable.ic_arrow_drop_down_black_24dp, 0);
        } else if (font.equals("me")) {
            font_size = "Medium";
            selectedFont = "Medium";
            font_btn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.font_increase24, 0, R.drawable.ic_arrow_drop_down_black_24dp, 0);
        } else if (font.equals("la")) {
            font_size = "Large";
            selectedFont = "Large";
            font_btn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.font_increase24, 0, R.drawable.ic_arrow_drop_down_black_24dp, 0);
        } else if (font.equals("el")) {
            font_size = "Extra Large";
            selectedFont = "Extra Large";
            font_btn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.font_increase24, 0, R.drawable.ic_arrow_drop_down_black_24dp, 0);
        } else if (font.equals("hu")) {
            font_size = "Huge";
            selectedFont = "Huge";
            font_btn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.font_increase24, 0, R.drawable.ic_arrow_drop_down_black_24dp, 0);
        }

        font_btn.setText(font_size);

    }


    /**
     * @return if intro has been opened
     */
    private boolean restorePrefData() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPreferences", MODE_PRIVATE);
        Boolean checkIntroActivityOpenedBefore = pref.getBoolean("checkLanguageOpen", false);
        return checkIntroActivityOpenedBefore;
    }

    /**
     * Save users data once they have clicked continue
     */
    private void savePrefsData() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("checkLanguageOpen", true);
        editor.commit();
    }

    /**
     * @return users language in a string format
     */
    public String getLanguage() {
        return language_btn.getText().toString();
    }
}
