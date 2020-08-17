package com.aston.blindfitness.View;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.aston.blindfitness.Adapter.LanguageListAdapter;
import com.aston.blindfitness.Model.LanguageRowItem;
import com.aston.blindfitness.R;
import com.aston.blindfitness.SharedPrefs;

import java.util.ArrayList;
import java.util.List;

/**
 * LanguageActivity class where user will be able to select a language for them to use while traversing the app
 */
public class LanguageActivity extends Activity implements AdapterView.OnItemClickListener {

    SharedPrefs userPreferences;

    /**
     * Language array for top 10 most spoken languages in the world for user to choose from
     */
    public static final String[] languages = new String[]{
            //"English", "Arabic", "Chinese (China)", "Czech", "Danish", "Dutch", "French", "German", "Italian", "Japanese" <- OLD
            // Top 10 Most spoken languages in the world 2020
            "Arabic", "Bengali", "Chinese (Simplified)", "English", "French", "Hindi", "Indonesian", "Portuguese", "Russian", "Spanish"

    };

    /**
     * Language description for top 10 most spoken languages in the world
     */
    public static final String[] descriptions = new String[]{
            //"English", "العربية", "中文", "čeština", "dansk", "Nederlands" , "français", "Deutsch", "italiano", "日本語" <- OLD
            // Top 10 Most spoken languages in the world 2020
            "العربية", "বাংলা", "中文", "English (United Kingdom)", "français", "मानक हिन्दी", "bahasa Indonesia", "português", "русский", "español"
    };

    /**
     * Integer array for each flag of top 10 most spoken languages in the world
     */
    public static final Integer[] images = new Integer[]{
            R.drawable.flag_arabic, R.drawable.flag_bengali, R.drawable.flag_china,
            R.drawable.flag_united, R.drawable.flag_france, R.drawable.flag_hindi,
            R.drawable.flag_indonesia, R.drawable.flag_portugal, R.drawable.flag_russia,
            R.drawable.flag_spain
    };

    ListView listView;
    List<LanguageRowItem> rowItems;
    private Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        userPreferences = new SharedPrefs(LanguageActivity.this);

        if (userPreferences.loadDarkTheme() == true) {
            setTheme(R.style.DarkTheme);
        } else {
            setTheme(R.style.AppTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.language_list);

        rowItems = new ArrayList<LanguageRowItem>();
        for (int i = 0; i < languages.length; i++) {
            LanguageRowItem item = new LanguageRowItem(languages[i], descriptions[i], images[i]);
            rowItems.add(item);
        }

        listView = findViewById(R.id.list);
        LanguageListAdapter adapter = new LanguageListAdapter(this,
                R.layout.language_item_list, rowItems);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        toolbar = findViewById(R.id.home_toolbar);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.nav_language_list_close:
                        finish();
                        overridePendingTransition(R.anim.no_change_animation, R.anim.slide_down_animation);
                        return true;
                }
                return false;
            }
        });

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = getIntent();
        String language = rowItems.get(i).getTitle();

        Intent returnLanguageIntent = new Intent();
        returnLanguageIntent.putExtra("Returninglanguage", language);
        setResult(RESULT_OK, returnLanguageIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.no_change_animation, R.anim.slide_down_animation);
    }

}
