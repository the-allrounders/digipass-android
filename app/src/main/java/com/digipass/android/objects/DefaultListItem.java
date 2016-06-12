package com.digipass.android.objects;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class DefaultListItem implements Parcelable {


    protected String _key;
    protected String _name;
    protected String _description;
    protected String _values;
    protected String _row_type;
    protected String _icon_name;
    protected int _status;
    protected String _timestamp;

    public DefaultListItem(String k, String name, String description, JSONArray values, String row_type, String icon_name, String timestamp) {
        this(k, name, description, values, row_type, icon_name);
        _timestamp = timestamp;
    }

    public DefaultListItem(String k, String name, String description, JSONArray values, String row_type, String icon_name, int status) {
        this(k, name, description, values, row_type, icon_name);
        _status = status;
    }

    public DefaultListItem(String k, String name, String description, JSONArray values, String row_type, String icon_name)
    {
        _key = k;
        _name = name;
        _description = description;
        _values = values.toString();
        _row_type = row_type;
        _icon_name = icon_name;
    }

    protected DefaultListItem(Parcel in) {
        _key = in.readString();
        _name = in.readString();
        _description = in.readString();
        _values = in.readString();
        _row_type = in.readString();
        _icon_name = in.readString();
        _status = in.readInt();
        _timestamp = in.readString();
    }

    public static final Creator<DefaultListItem> CREATOR = new Creator<DefaultListItem>() {
        @Override
        public DefaultListItem createFromParcel(Parcel in) {
            return new DefaultListItem(in);
        }

        @Override
        public DefaultListItem[] newArray(int size) {
            return new DefaultListItem[size];
        }
    };

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

    public Boolean has_icon() {
        return _icon_name != null && !Objects.equals(_icon_name, "");
    }

    public Drawable get_icon(Context c) {
        int imageResource = c.getResources().getIdentifier("drawable/" + this._icon_name, null, c.getPackageName());
        return c.getResources().getDrawable(imageResource);
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
                } catch (Exception ignored) {

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return values_string;
    }

//    public Bitmap get_status_icon() {
//        return ;
//    }

    public int get_status() {
        return _status;
    }

    public String get_timestamp() {
        return _timestamp;
    }

    public String get_timestamp_formatted() {
        return _timestamp;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(_key);
        dest.writeString(_name);
        dest.writeString(_description);
        dest.writeString(_values);
        dest.writeString(_row_type);
        dest.writeString(_icon_name);
        dest.writeInt(_status);
        dest.writeString(_timestamp);
    }
}
