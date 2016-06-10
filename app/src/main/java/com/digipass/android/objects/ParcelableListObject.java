package com.digipass.android.objects;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class ParcelableListObject implements Parcelable {

    public String list_type;
    public ArrayList data_list;
    public int row_type;
    private String type;

    public ParcelableListObject(String l, ArrayList d, int r) {
        list_type = l;
        data_list = d;
        row_type = r;
    }

    protected ParcelableListObject(Parcel in) {
        list_type = in.readString();
        row_type = in.readInt();
        Class t;
        switch(list_type) {
            case "preferences":
                t = ListItem.class;
                break;
            default:
                t = null;
        }
        if (t != null) {
            data_list = in.readArrayList(t.getClass().getClassLoader());
        }
    }

    private void func () {}

    public static final Creator<ParcelableListObject> CREATOR = new Creator<ParcelableListObject>() {
        @Override
        public ParcelableListObject createFromParcel(Parcel in) {
            return new ParcelableListObject(in);
        }

        @Override
        public ParcelableListObject[] newArray(int size) {
            return new ParcelableListObject[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(list_type);
        dest.writeInt(row_type);
        dest.writeList(data_list);
    }
}
