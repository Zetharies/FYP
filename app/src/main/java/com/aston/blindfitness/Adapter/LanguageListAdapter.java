package com.aston.blindfitness.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.aston.blindfitness.Model.LanguageRowItem;
import com.aston.blindfitness.R;
import com.aston.blindfitness.View.PreferenceActivity;

import java.util.List;

public class LanguageListAdapter extends ArrayAdapter<LanguageRowItem> {

    Context context;

    /**
     * Adapter for our LanguageList
     * @param context
     * @param resourceId
     * @param items
     */
    public LanguageListAdapter(Context context, int resourceId, List<LanguageRowItem> items) {
        super(context, resourceId, items);
        this.context = context;
    }

    private class ViewHolder {
        TextView txtTitle;
        TextView txtDesc;
        ImageView image;
    }

    /**
     * Return our view for language list items
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        LanguageRowItem rowItem = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.language_item_list, null);
            holder = new ViewHolder();
            holder.txtDesc = (TextView) convertView.findViewById(R.id.desc);
            holder.txtTitle = (TextView) convertView.findViewById(R.id.language_title);
            holder.image = (ImageView) convertView.findViewById(R.id.language_image);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        holder.txtDesc.setText(rowItem.getDesc());
        holder.txtTitle.setText(rowItem.getTitle());
        holder.image.setImageResource(rowItem.getImage());

        if (rowItem.getTitle().equals(PreferenceActivity.selectedLanguage)) {
            holder.txtTitle.setTextColor(Color.GREEN);
            holder.txtDesc.setTextColor(Color.GREEN);
        } else {
            holder.txtTitle.setTextColor(Color.WHITE);
            holder.txtDesc.setTextColor(Color.WHITE);
        }

        return convertView;
    }

}
