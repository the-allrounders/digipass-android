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
import com.digipass.android.objects.StatusListItem;

import java.util.ArrayList;
import java.util.Objects;

public class StatusListAdapter extends ArrayAdapter<DefaultListItem> {

    private Context context;
    private ArrayList<DefaultListItem> data;
    private int rowlayout;
    private LayoutInflater mInflater;
    private int delay;

    public StatusListAdapter(Context context, int textViewResourceId, ArrayList<DefaultListItem> data, int delay) {
        super(context, textViewResourceId, data);
        this.context = context;
        this.mInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.data = data;
        this.rowlayout = textViewResourceId;
        this.delay = delay;
    }

    private class Holder {
        public TextView title;
        public TextView subtitle;
        public ImageView status;
        public ImageView thumb;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final StatusListItem listItem = (StatusListItem)this.data.get(position);
        final Holder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(rowlayout, null);
            holder = new Holder();
            holder.title = (TextView) convertView.findViewById(R.id.row_1_title);
            holder.subtitle = (TextView) convertView.findViewById(R.id.row_1_subtitle);
            holder.status = (ImageView) convertView.findViewById(R.id.row_1_status_icon);
            holder.thumb = (ImageView) convertView.findViewById(R.id.row_1_thumb_icon);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        if (listItem.has_icon()) {
            holder.thumb.setImageDrawable(listItem.get_icon(context));
        } else {
            holder.thumb.setVisibility(View.GONE);
        }

        holder.title.setText(listItem.get_name());

        if (Objects.equals(listItem.get_values_as_string(), "")) {
            holder.subtitle.setVisibility(View.GONE);
        } else {
            holder.subtitle.setText(listItem.get_values_as_string());
        }

        holder.status.setImageDrawable(listItem.get_status_icon(context));

        Animation animation = AnimationUtils.loadAnimation(context, R.anim.fade_in);
        int d = 50;
        animation.setStartOffset(d * (position + 1) + (d * delay));

        convertView.startAnimation(animation);

        return convertView;
    }
}
