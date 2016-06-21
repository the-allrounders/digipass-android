package com.digipass.android.objects;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

import com.digipass.android.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class StatusListItem extends DefaultListItem implements Parcelable {

    private String _status;
    private ArrayList<String> _children;

    public StatusListItem(String k, String name, String description, JSONArray values, String row_type, String icon_name, String status, JSONArray children) {
        super(k, name, description, values, row_type, icon_name);
        this._status = status;
        _children = new ArrayList<>();
        for (int i=0;i<children.length();i++){
            try {
                _children.add(children.get(i).toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    protected StatusListItem(Parcel in) {
        super(in);
        _status = in.readString();
        _children = in.createStringArrayList();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(_status);
        dest.writeStringList(_children);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<StatusListItem> CREATOR = new Creator<StatusListItem>() {
        @Override
        public StatusListItem createFromParcel(Parcel in) {
            return new StatusListItem(in);
        }

        @Override
        public StatusListItem[] newArray(int size) {
            return new StatusListItem[size];
        }
    };

    public ArrayList<String> get_children() {
        return _children;
    }

    public String get_status() {
        return _status;
    }

    public Drawable get_status_icon(Context c) {
        String icon_string;
        int icon_color;
        switch (_status) {
            case "approved":
                icon_string = "ic_approve";
                break;
            case "denied":
                icon_string = "ic_deny";
                break;
            case "indeterminate":
                icon_string = "ic_indeterminate";
            case "pending":
            default:
                icon_string = "ic_pending";
                icon_color = R.color.pendingColor;
        }
        int imageResource = c.getResources().getIdentifier("drawable/" + icon_string, null, c.getPackageName());
        return c.getResources().getDrawable(imageResource);
    }
}
