package com.aston.blindfitness.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.aston.blindfitness.R;
import com.aston.blindfitness.SharedPrefs;

import java.util.ArrayList;
import java.util.Arrays;

public class HelpActivity extends AppCompatActivity {

    SharedPrefs userPreferences;
    String[] items;
    ArrayList<String> listItems;
    ArrayAdapter<String> adapter;
    ListView listView;
    EditText editText;
    boolean isOpened;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        userPreferences = new SharedPrefs(HelpActivity.this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        listView = findViewById(R.id.helplistview);
        editText = findViewById(R.id.txtSearch);
        isOpened = false;

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0:
                        showDialog("How to view navigation route", "In the main application, visit Map and accept the location services, then click or search for a destination and select the start navigation process.");
                    case 1:
                        showDialog("How to perform exercise", "In the main application, visit My Fitness to start a daily exercise regime to help keep you in shape");
                    case 2:
                        showDialog("How to visit nearby support groups", "In the main application, visit Map and accept the location services to view nearby support groups for visually impaired");
                    case 3:
                        showDialog("How to view latest health news", "In the main application, visit news and events to keep up to date with the latest articles regarding health and fitness");
                    case 4:
                        showDialog("How do I record my progress", "In the My Fitness function, after you have finished a daily exercise, record your heart rate by placing a finger on the sensor to keep track of your heart rate over time");
                    case 5:
                        showDialog("How can I change the language", "In the main application, go to settings and tap on language to change the language feature");
                    case 6:
                        showDialog("How can I change the font", "In the main application, go to settings and tap on font size to change the size of fonts");
                    case 7:
                        showDialog("How can I interact with the application", "In the main application, tap on the microphone icon in the top right hand corner to perform various speech activities. These include: \nOpen followed by the feature you want to use, as well as Time and date commands");
                    case 8:
                        showDialog("How do I use object recognition", "In the main application, visit the OCR functionality where you will be provided object recognition services such as text recognition and object recognition");
                    default:
                        isOpened = false;
                }
            }
        });
        initList();
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.toString().equals("")){
                    // Reset ListView
                    initList();
                } else {
                    // Perform Search
                    searchItem(charSequence.toString());
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        if (userPreferences.loadDarkTheme()) {
            getSupportActionBar().setTitle(Html.fromHtml("Help"));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } else {
            getSupportActionBar().setTitle(Html.fromHtml("Help"));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        getSupportActionBar().setDisplayUseLogoEnabled(true);
    }

    /**
     * Allows the user to search for a question regarding the application quicker
     * @param searchedText
     */
    private void searchItem(String searchedText) {
        for(String item: items){
            if(!item.contains(searchedText)){
                listItems.remove(item);
            }
        }
        adapter.notifyDataSetChanged();
    }

    /**
     * Initialise the questions that the user can select from
     */
    public void initList(){
        items = new String[] {"How to view navigation route", "How to perform exercise", "How to visit nearby support groups", "How to view latest health news", "How do I record my progress",
        "How can I change the language", "How can I change the font", "How can I interact with the application", "How do I use object recognition"};
        listItems = new ArrayList<>(Arrays.asList(items));
        adapter = new ArrayAdapter<String>(this, R.layout.help_list_items, R.id.txtItem, listItems);
        listView.setAdapter(adapter);
    }

    /**
     * Dialog to display to the user when they have a question
     * @param title
     * @param information
     */
    public void showDialog(String title, String information){
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.help_alert_dialog, null);

        Button close = view.findViewById(R.id.closeHelp);
        TextView alertTvtitle = view.findViewById(R.id.help_ad_tv_title);
        TextView alertTvdesc = view.findViewById(R.id.help_ad_tv_desc);

        alertTvtitle.setText(title);
        alertTvdesc.setText(information);

        AlertDialog alertDialog = new AlertDialog.Builder(this).setView(view).create();

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                isOpened = false;
            }
        });

        if (!isOpened) {
            alertDialog.show();
            isOpened = true;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
