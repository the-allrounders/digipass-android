package com.digipass.android.objects;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.digipass.android.GenericListActivity;
import com.digipass.android.R;
import com.digipass.android.singletons.Data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Preference implements Parcelable {


    private int _key;
    private String _name;
    private String _description;
    private String _values;
    private String _row_type;

    public Preference(int k, String name, String description, JSONArray values, String row_type)
    {
        _key = k;
        _name = name;
        _description = description;
        _values = values.toString();
        _row_type = row_type;
    }

    protected Preference(Parcel in) {
        _key = in.readInt();
        _name = in.readString();
        _description = in.readString();
        _values = in.readString();
        _row_type = in.readString();
    }

    public static final Creator<Preference> CREATOR = new Creator<Preference>() {
        @Override
        public Preference createFromParcel(Parcel in) {
            return new Preference(in);
        }

        @Override
        public Preference[] newArray(int size) {
            return new Preference[size];
        }
    };

    public static void ShowPreferenceList(Context c) {
        Intent i = new Intent(c, GenericListActivity.class);
        ArrayList<Preference> data = Data.GetInstance(c).GetPreferences("0");
        i.putExtra("data", data);
        i.putExtra("list_type", "preferences");
        i.putExtra("row_type", R.layout.list_row_1);
        i.putExtra("group_title", "");
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        c.startActivity(i);
    }

    public String get_key() {
        return _key + "";
    }

    public String get_description()
    {
        return _description;
    }

    public String get_name()
    {
        return _name;
    }

    public String get_row_type()
    {
        return _row_type;
    }

    public JSONArray get_values() {
        JSONArray v = new JSONArray();
        try {
            v = new JSONArray(_values);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return v;
    }

    public String get_values_as_string() {
        String values_string = "";

        try {
            JSONArray v = new JSONArray(_values);
            for (int i = 0; i < v.length(); i++) {
                try {
                    values_string += ((JSONObject) v.get(i)).getString("title");
                    if (i < v.length() - 1) {
                        values_string += ", ";
                    }
                } catch (Exception e) {

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return values_string;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(_key);
        dest.writeString(_name);
        dest.writeString(_description);
        dest.writeString(_values);
        dest.writeString(_row_type);
    }
}
