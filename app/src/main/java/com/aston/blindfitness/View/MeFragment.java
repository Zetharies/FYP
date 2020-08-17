package com.aston.blindfitness.View;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.aston.blindfitness.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class MeFragment extends Fragment {

    class MyProfileAdapter extends ArrayAdapter {
        ArrayList title = new ArrayList<String>();
        ArrayList description = new ArrayList<String>();

        public MyProfileAdapter(Context context, ArrayList<String> titles, ArrayList<String> description) {
            super(context, R.layout.profile_listview, titles);
            title = titles;
            this.description = description;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            View rowView = getLayoutInflater().inflate(R.layout.profile_listview, null, true);

            TextView titleText = rowView.findViewById(R.id.title);
            TextView descriptionText = rowView.findViewById(R.id.description);

            titleText.setText(title.get(position).toString());
            descriptionText.setText(description.get(position).toString());

            return rowView;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_me, container, false);

        setHasOptionsMenu(true);

        ListView profileListView = view.findViewById(R.id.profileListView);

        TextView plan_Text = view.findViewById(R.id.plan_text);

        ArrayList<String> user_information = new ArrayList<String>();
        user_information.add("Plan ");
        user_information.add("Plan days ");
        user_information.add("My Height ");
        user_information.add("My Weight ");
        ArrayList<String> plan_description = new ArrayList<String>();

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("myFitness", Context.MODE_PRIVATE);

        boolean isBeginner = sharedPreferences.getBoolean("isBeginner", true);
        boolean isIntermediate = sharedPreferences.getBoolean("isIntermediate", true);
        boolean isAdvanced = sharedPreferences.getBoolean("isAdvanced", true);

        /*
        if (isBeginner) {
            plan_Text.setText("Beginner Plan");
            plan_description.add("Beginner");
        } else {
            plan_Text.setText("Advanced Plan");
            plan_description.add("Advanced");
        }

         */

        if (isBeginner) {
            plan_Text.setText("Beginner Plan");
            plan_description.add("Beginner");
        } else if (isIntermediate) {
            plan_Text.setText("Intermediate Plan");
            plan_description.add("Intermediate");
        } else if (isAdvanced){
            plan_Text.setText("Advanced Plan");
            plan_description.add("Advanced");
        }
        int days = sharedPreferences.getInt("days", 15);
        switch (days) {
            case 15:
                plan_description.add("15 days");
                break;
            case 30:
                plan_description.add("30 days");
                break;
            case 45:
                plan_description.add("45 days");
                break;
            case 60:
                plan_description.add("60 days");
                break;
            case 75:
                plan_description.add("75 days");
                break;
        }

        plan_description.add(Float.toString(sharedPreferences.getFloat("height", 0)) + " cm");
        plan_description.add(Float.toString(sharedPreferences.getFloat("weight", 0)) + " kg");
        MyProfileAdapter myProfileAdapter = new MyProfileAdapter(getActivity(), user_information, plan_description);

        profileListView.setAdapter(myProfileAdapter);

        return view;
    }
}
