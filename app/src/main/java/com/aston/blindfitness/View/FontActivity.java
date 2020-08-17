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

import com.aston.blindfitness.Adapter.FontListAdapter;
import com.aston.blindfitness.Model.FontRowItem;
import com.aston.blindfitness.R;
import com.aston.blindfitness.SharedPrefs;

import java.util.ArrayList;
import java.util.List;

public class FontActivity extends Activity implements AdapterView.OnItemClickListener {

    SharedPrefs userPreferences;

    public static final String[] fonts = new String[]{
            "Default", "Tiny", "Extra small", "Small", "Medium", "Large", "Extra Large", "Huge"
    };

    ListView listView;
    List<FontRowItem> rowItems;
    private Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        userPreferences = new SharedPrefs(FontActivity.this);

        if (userPreferences.loadDarkTheme() == true) {
            setTheme(R.style.DarkTheme);
        } else {
            setTheme(R.style.AppTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.font_list);

        rowItems = new ArrayList<FontRowItem>();
        for (int i = 0; i < fonts.length; i++) {
            FontRowItem item = new FontRowItem(fonts[i]);
            rowItems.add(item);
        }

        listView = findViewById(R.id.list);
        FontListAdapter adapter = new FontListAdapter(this,
                R.layout.font_item_list, rowItems);
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
        String font = rowItems.get(i).getTitle();

        // chosenLanguage = language;
        Intent returnFontIntent = new Intent();
        returnFontIntent.putExtra("Returningfont", font);
        setResult(RESULT_OK, returnFontIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.no_change_animation, R.anim.slide_down_animation);
    }

}
