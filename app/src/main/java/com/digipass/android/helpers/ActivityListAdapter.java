package com.digipass.android.helpers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.digipass.android.R;
import com.digipass.android.objects.DefaultListItem;

import java.util.ArrayList;


public class ActivityListAdapter extends ArrayAdapter<DefaultListItem> {

    private Context context;
    private ArrayList<DefaultListItem> data;
    private int rowlayout;
    private LayoutInflater mInflater;
    private int delay;


    public ActivityListAdapter(Context context, int textViewResourceId, ArrayList<DefaultListItem> data, int delay) {
        super(context, textViewResourceId, data);
        this.context = context;
        this.mInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.data = data;
        this.rowlayout = textViewResourceId;
        this.delay = delay;
    }


    private class Holder {
        public ImageView thumb;
        public TextView title;
        public TextView description;
        public TextView subtitle;
        public TextView dateAdded;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final DefaultListItem defaultListItem = this.data.get(position);
        final Holder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(rowlayout, null);
            holder = new Holder();
            holder.thumb = (ImageView) convertView.findViewById(R.id.row_1_thumb_icon);
            holder.title = (TextView) convertView.findViewById(R.id.row_1_title);
            holder.subtitle = (TextView) convertView.findViewById(R.id.row_1_subtitle);
            holder.dateAdded = (TextView) convertView.findViewById(R.id.date_added);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        if (defaultListItem.has_icon()) {
            holder.thumb.setImageDrawable(defaultListItem.get_icon(context));
        } else {
            holder.thumb.setVisibility(View.GONE);
        }
        holder.title.setText(defaultListItem.get_name());
        holder.subtitle.setText(defaultListItem.get_description());

        holder.dateAdded.setText(defaultListItem.get_timestamp());

        Animation animation = AnimationUtils.loadAnimation(context, R.anim.fade_in);
        int d = 50;
        animation.setStartOffset(d * (position + 1) + (d * delay));

        convertView.startAnimation(animation);

        return convertView;
    }
}
