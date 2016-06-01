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
        SharedPreferences preference_data = context.getSharedPreferences("preferences_data", Context.MODE_PRIVATE);
        SharedPreferences pref_taxonomy_data = context.getSharedPreferences("preference_taxonomy_data", Context.MODE_PRIVATE);
        String pref_string = preference_data.getString("preferences_data", "[]");
        String tax_string = pref_taxonomy_data.getString("preference_taxonomy_data", "[]");
        Log.d("key", key);
        try {
            JSONArray taxonomy_tree = new JSONArray(tax_string);
            preferences_list = getPreferencesList(key, taxonomy_tree, pref_string);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return preferences_list;
    }

    private ArrayList<Preference> getPreferencesList (String key, JSONArray tree, String json) {
        ArrayList<Preference> preferences_list = new ArrayList<>();
        JSONArray preferences = null;
        Log.d("tree", tree.toString());
        try {
            preferences = new JSONArray(json);
            for(int n = 0; n < tree.length(); n++) {
                JSONObject term = (JSONObject) tree.get(n);
                Log.d("term", term.toString());
                if (Objects.equals(term.getString("parent"), key)) {
                    preferences_list.add(new Preference(Integer.valueOf(term.getString("tid")), term.getString("name"), "", new JSONArray(), "group"));
                }
            }
            for(int i = 0; i < preferences.length(); i++)
            {
                JSONObject preference = (JSONObject)preferences.get(i);
                if (preference.getJSONArray("field_category").toString().contains("\""+ key +"\"")) {
                    preferences_list.add(new Preference(preference.getInt("id"), preference.getString("title"), preference.getString("description"), preference.getJSONArray("field_values"), "preference"));
                }
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
