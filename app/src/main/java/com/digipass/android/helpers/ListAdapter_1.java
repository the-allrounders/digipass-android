package com.digipass.android.helpers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.digipass.android.R;
import com.digipass.android.objects.Preference;

import java.util.ArrayList;

public class ListAdapter_1 extends ArrayAdapter<Preference> {

    private Context context;
    private ArrayList<Preference> pref;
    private int rowlayout;

    public ListAdapter_1(Context context, int textViewResourceId, ArrayList<Preference> data) {
        super(context, textViewResourceId, data);
        this.context = context;
        pref = data;
        rowlayout = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(rowlayout, parent, false);
        TextView name = (TextView) rowView.findViewById(R.id.name);
        TextView description = (TextView) rowView.findViewById(R.id.description);
        TextView values = (TextView) rowView.findViewById(R.id.values);
        name.setText(pref.get(position).get_name());
        description.setText(String.valueOf(pref.get(position).get_description()));
        values.setText(String.valueOf(pref.get(position).get_values_as_string()));
        return rowView;
    }
}
