package com.digipass.android.singletons;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.digipass.android.MainActivity;
import com.digipass.android.objects.Preference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

public class Data {

    private static Data _data;
    private Context context;

    private Data(Context c) {
        context = c;
    }

    public ArrayList<Preference> GetPreferences(String key) {
        ArrayList<Preference> preferences_list = new ArrayList<>();
        SharedPreferences data = context.getSharedPreferences("json_data", Context.MODE_PRIVATE);
        String jsonString = data.getString("json_data", "[]");
        Log.d("abc", key);
        JSONArray preferences = null;
        try {
            preferences = new JSONArray(jsonString);
            for(int i = 0; i < preferences.length(); i++)
            {
                JSONObject preference = (JSONObject)preferences.get(i);
                preferences_list.add(new Preference(preference.getInt("id"), preference.getString("title"), preference.getString("description"), preference.getJSONArray("field_values")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return preferences_list;
    }

    public static Data GetInstance(Context c) {
        if (_data == null) _data = new Data(c);
        return _data;
    }
}
