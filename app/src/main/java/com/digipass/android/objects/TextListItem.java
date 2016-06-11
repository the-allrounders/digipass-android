package com.digipass.android.objects;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;

public class TextListItem extends DefaultListItem implements Parcelable {

    public TextListItem(String name, String description) {
        super("", name, description, new JSONArray(), "text", "");
    }

    public TextListItem(String name) {
        super("", name, "", new JSONArray(), "text", "");
    }

    protected TextListItem(Parcel in) {
        super(in);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TextListItem> CREATOR = new Creator<TextListItem>() {
        @Override
        public TextListItem createFromParcel(Parcel in) {
            return new TextListItem(in);
        }

        @Override
        public TextListItem[] newArray(int size) {
            return new TextListItem[size];
        }
    };
}
