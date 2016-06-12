package com.digipass.android.objects;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;

public class OrganisationListItem extends DefaultListItem implements Parcelable {

    public OrganisationListItem(String k, String name, String description, JSONArray values, String row_type, String icon_name, int status) {
        super(k, name, description, values, row_type, icon_name, status);
    }

    protected OrganisationListItem(Parcel in) {
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

    public static final Creator<OrganisationListItem> CREATOR = new Creator<OrganisationListItem>() {
        @Override
        public OrganisationListItem createFromParcel(Parcel in) {
            return new OrganisationListItem(in);
        }

        @Override
        public OrganisationListItem[] newArray(int size) {
            return new OrganisationListItem[size];
        }
    };

    public String get_icon_url() {
        return _icon_name;
    }
}
