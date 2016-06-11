package com.digipass.android.objects;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;

public class OrganisationDefaultListItem extends DefaultListItem implements Parcelable {


    public OrganisationDefaultListItem(String k, String name, String description, JSONArray values, String row_type, String icon_name, int status) {
        super(k, name, description, values, row_type, icon_name, status);
    }

    protected OrganisationDefaultListItem(Parcel in) {
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

    public static final Creator<OrganisationDefaultListItem> CREATOR = new Creator<OrganisationDefaultListItem>() {
        @Override
        public OrganisationDefaultListItem createFromParcel(Parcel in) {
            return new OrganisationDefaultListItem(in);
        }

        @Override
        public OrganisationDefaultListItem[] newArray(int size) {
            return new OrganisationDefaultListItem[size];
        }
    };

    public Drawable get_icon(Context c) {
        int imageResource = c.getResources().getIdentifier("drawable/" + this._icon_name, null, c.getPackageName());
        return c.getResources().getDrawable(imageResource);
    }
}
