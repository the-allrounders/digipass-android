package com.digipass.android.singletons;

import android.content.Context;
import android.content.SharedPreferences;

import com.digipass.android.objects.Preference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Data {

    private static Data _data;
    private Context context;

    private Data(Context c) {
        context = c;
    }

    public Map<String, ArrayList<Preference>> GetPreferences(String key) {
        Map<String, ArrayList<Preference>> preferences_list = new HashMap<>();
        SharedPreferences preference_data = context.getSharedPreferences("preferences_data", Context.MODE_PRIVATE);
        SharedPreferences pref_taxonomy_data = context.getSharedPreferences("preference_category_data", Context.MODE_PRIVATE);
        String pref_string = preference_data.getString("preferences_data", "[]");
        String tax_string = pref_taxonomy_data.getString("preference_category_data", "[]");
        try {
            JSONArray taxonomy_tree = new JSONArray(tax_string);
            preferences_list.put("preferences", getPreferencesList(key, taxonomy_tree, pref_string));
            preferences_list.put("categories", getCategoriesList(key, taxonomy_tree, pref_string));
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return preferences_list;
    }

    private ArrayList<Preference> getPreferencesList(String key, JSONArray categories, String json) {
        ArrayList<Preference> preferences_list = new ArrayList<>();
        JSONArray preferences;
        try {
            preferences = new JSONArray(json);
            for(int i = 0; i < preferences.length(); i++)
            {
                JSONObject preference = (JSONObject)preferences.get(i);
                JSONArray __v = preference.getJSONArray("values");
                JSONArray _v = new JSONArray();
                for(int n = 0; n < __v.length(); n++) {
                    JSONObject ___v = new JSONObject();
                    ___v.put("title", __v.get(n));
                    ___v.put("value", "true");
                    _v.put(___v);
                }
                preference.put("values", _v);
//                if (preference.getJSONArray("category").toString().contains("\""+ key +"\"")) {
                    preferences_list.add(new Preference(preference.getString("_id"), preference.getString("title"), preference.getString("description"), preference.getJSONArray("values"), "preference", ""));
//                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return preferences_list;
    }

    private ArrayList<Preference> getCategoriesList(String key, JSONArray categories, String json) {
        ArrayList<Preference> preferences_list = new ArrayList<>();
        JSONArray preferences;
        try {
            preferences = new JSONArray(json);
            for(int n = 0; n < categories.length(); n++) {
                JSONObject category = (JSONObject) categories.get(n);
                JSONArray categoryParents = category.getJSONArray("parent");
                if (categoryParents.toString().contains("\""+ key +"\"") || (Objects.equals(key, "0") && categoryParents.length() == 0)) {
                    JSONArray values = new JSONArray();
                    for(int i = 0; i < preferences.length(); i++)
                    {
                        JSONObject preference = (JSONObject)preferences.get(i);
                        if (preference.getJSONArray("category").toString().contains("\""+ category.getString("_id") +"\"")) {
                            JSONObject _v = new JSONObject();
                            _v.put("title", preference.getString("title"));
                            values.put(_v);
                        }
                    }
                    preferences_list.add(new Preference(category.getString("_id"), category.getString("title"), "", values, "group", category.getString("icon")));
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
