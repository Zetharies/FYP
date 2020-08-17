package com.aston.blindfitness.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.aston.blindfitness.Model.FontRowItem;
import com.aston.blindfitness.R;
import com.aston.blindfitness.View.PreferenceActivity;

import java.util.List;

public class FontListAdapter extends ArrayAdapter<FontRowItem> {

    Context context;

    public FontListAdapter(Context context, int resourceId, List<FontRowItem> items) {
        super(context, resourceId, items);
        this.context = context;
    }

    private class ViewHolder {
        TextView txtTitle;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        FontRowItem rowItem = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.font_item_list, null);
            holder = new ViewHolder();
            holder.txtTitle = (TextView) convertView.findViewById(R.id.font_title);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        holder.txtTitle.setText(rowItem.getTitle());

        if(rowItem.getTitle().equals(PreferenceActivity.selectedFont)){
            holder.txtTitle.setTextColor(Color.GREEN);
        } else {
            holder.txtTitle.setTextColor(Color.WHITE);
        }

        if(rowItem.getTitle().equals("Default")){
            holder.txtTitle.setTextSize(24f);
        }
        if(rowItem.getTitle().equals("Tiny")){
            holder.txtTitle.setTextSize(14f);
        }
        if(rowItem.getTitle().equals("Extra small")){
            holder.txtTitle.setTextSize(17f);
        }
        if(rowItem.getTitle().equals("Small")){
            holder.txtTitle.setTextSize(22f);
        }
        if(rowItem.getTitle().equals("Medium")){
            holder.txtTitle.setTextSize(30f);
        }
        if(rowItem.getTitle().equals("Large")){
            holder.txtTitle.setTextSize(36f);
        }
        if(rowItem.getTitle().equals("Extra Large")){
            holder.txtTitle.setTextSize(40f);
        }
        if(rowItem.getTitle().equals("Huge")){
            holder.txtTitle.setTextSize(46f);
        }

        return convertView;
    }

}
