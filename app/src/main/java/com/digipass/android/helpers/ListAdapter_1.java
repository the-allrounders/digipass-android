package com.digipass.android.helpers;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.digipass.android.R;
import com.digipass.android.objects.Preference;

import java.util.ArrayList;

public class ListAdapter_1 extends ArrayAdapter<Preference> {

    private Context context;
    private ArrayList<Preference> data;
    private int rowlayout;
    private DisplayMetrics metrics_;
    private LayoutInflater mInflater;

    public ListAdapter_1(Context context, int textViewResourceId, ArrayList<Preference> data, DisplayMetrics metrics) {
        super(context, textViewResourceId, data);
        this.context = context;
        this.mInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.data = data;
        this.metrics_ = metrics;
        this.rowlayout = textViewResourceId;
    }

    private class Holder {
        public ImageView thumb;
        public TextView title;
        public TextView description;
        public TextView subtitle;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Preference preference = this.data.get(position);
        final Holder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(rowlayout, null);
            holder = new Holder();
            holder.thumb = (ImageView) convertView.findViewById(R.id.row_1_thumb_icon);
            holder.title = (TextView) convertView.findViewById(R.id.row_1_title);
            holder.subtitle = (TextView) convertView.findViewById(R.id.row_1_subtitle);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        if (preference.has_icon()) {
            holder.thumb.setImageDrawable(preference.get_icon(context));
        } else {
            holder.thumb.setVisibility(View.GONE);
        }
        holder.title.setText(preference.get_name());
        holder.subtitle.setText(preference.get_values_as_string());

        Animation animation = AnimationUtils.loadAnimation(context, R.anim.fade_in);
        animation.setStartOffset(50 * position);

        convertView.startAnimation(animation);

        return convertView;
    }
}
