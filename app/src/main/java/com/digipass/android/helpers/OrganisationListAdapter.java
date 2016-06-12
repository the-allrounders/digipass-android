package com.digipass.android.helpers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.digipass.android.R;
import com.digipass.android.objects.DefaultListItem;
import com.digipass.android.objects.OrganisationListItem;

import java.util.ArrayList;

public class OrganisationListAdapter extends ArrayAdapter<DefaultListItem> {

    private Context context;
    private ArrayList<DefaultListItem> data;
    private int rowlayout;
    private LayoutInflater mInflater;
    private int delay;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public OrganisationListAdapter(Context context, int textViewResourceId, ArrayList<DefaultListItem> data, int delay) {
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
        public NetworkImageView thumb;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final OrganisationListItem organisation = (OrganisationListItem)this.data.get(position);
        final Holder holder;

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();

        if (convertView == null) {
            convertView = mInflater.inflate(rowlayout, null);
            holder = new Holder();
            holder.title = (TextView) convertView.findViewById(R.id.row_1_title);
            holder.subtitle = (TextView) convertView.findViewById(R.id.row_1_subtitle);
            holder.thumb = (NetworkImageView) convertView.findViewById(R.id.row_1_thumb_icon);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        holder.subtitle.setText(organisation.get_values_as_string());
        holder.title.setText(organisation.get_name());
        holder.thumb.setImageUrl(organisation.get_icon_url(), imageLoader);

        Animation animation = AnimationUtils.loadAnimation(context, R.anim.fade_in);
        int d = 50;
        animation.setStartOffset(d * (position + 1) + (d * delay));

        convertView.startAnimation(animation);

        return convertView;
    }
}
