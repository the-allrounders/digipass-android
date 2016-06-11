package com.digipass.android.singletons;

import android.content.Context;

import com.digipass.android.objects.DefaultListItem;
import com.digipass.android.objects.OrganisationDefaultListItem;

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

    public static Data GetInstance(Context c) {
        if (_data == null) _data = new Data(c);
        return _data;
    }

    public Map<String, ArrayList<DefaultListItem>> GetPreferences(String key) {
        Map<String, ArrayList<DefaultListItem>> preferences_list = new HashMap<>();
        String pref_string = context.getSharedPreferences("preferences_data", Context.MODE_PRIVATE).getString("preferences_data", "[]");
        String tax_string = context.getSharedPreferences("preference_category_data", Context.MODE_PRIVATE).getString("preference_category_data", "[]");
        try {
            JSONArray taxonomy_tree = new JSONArray(tax_string);
            preferences_list.put("preferences", getPreferencesList(key, taxonomy_tree, pref_string));
            preferences_list.put("categories", getCategoriesList(key, taxonomy_tree, pref_string));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return preferences_list;
    }

    private ArrayList<DefaultListItem> getPreferencesList(String key, JSONArray categories, String json) {
        ArrayList<DefaultListItem> preferences_list = new ArrayList<>();
        JSONArray preferences;
        try {
            preferences = new JSONArray(json);
            for(int i = 0; i < preferences.length(); i++)
            {
                JSONObject preference = (JSONObject)preferences.get(i);
                if (preference.getJSONArray("category").toString().contains("\""+ key +"\"")) {
                    preferences_list.add(new DefaultListItem(preference.getString("_id"), preference.getString("title"), preference.getString("description"), preference.getJSONArray("values"), "preference", ""));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return preferences_list;
    }

    private ArrayList<DefaultListItem> getCategoriesList(String key, JSONArray categories, String json) {
        ArrayList<DefaultListItem> preferences_list = new ArrayList<>();
        JSONArray preferences;
        try {
            preferences = new JSONArray(json);
            for(int n = 0; n < categories.length(); n++) {
                JSONObject category = (JSONObject) categories.get(n);
                JSONArray categoryParents = category.getJSONArray("parent");
//                if (categoryParents.toString().contains("\""+ key +"\"") || (Objects.equals(key, "0") && categoryParents.length() == 0)) {
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
                    preferences_list.add(new DefaultListItem(category.getString("_id"), category.getString("title"), "", values, "group", category.getString("icon")));
//                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return preferences_list;
    }

    public Map<String, ArrayList<DefaultListItem>> GetHomeLists() {
        return GetHomeLists(3, 3);
    }

    public Map<String, ArrayList<DefaultListItem>> GetHomeLists(int max_req, int max_ac_log) {
        Map<String, ArrayList<DefaultListItem>> home_map = new HashMap<>();
        String req_string = context.getSharedPreferences("requests_data", Context.MODE_PRIVATE).getString("requests", "[]");
        String ac_log_string = context.getSharedPreferences("preference_category_data", Context.MODE_PRIVATE).getString("preference_category_data", "[]");
        home_map.put("requests", getRequestsList("0", req_string));
        home_map.put("activities", getActivitiesList(ac_log_string));

        int req_size = home_map.get("requests").size();
        if (req_size > max_req && max_req > 0) {
            home_map.get("requests").subList(max_req, req_size).clear();
        }
        int ac_log_size = home_map.get("activities").size();
        if (ac_log_size > max_ac_log && max_ac_log > 0) {
            home_map.get("activities").subList(max_ac_log, ac_log_size).clear();
        }

        return home_map;
    }

    private ArrayList<DefaultListItem> getRequestsList(String key, String json) {
        ArrayList<DefaultListItem> requests_list = new ArrayList<>();
        JSONArray organisations;
        try {
            organisations = new JSONArray(json);
            for(int n = 0; n < organisations.length(); n++) {
                JSONObject organisation = (JSONObject)organisations.get(n);
                JSONArray permissions = organisation.getJSONArray("permissions");
                JSONArray values = new JSONArray();
                for(int i = 0; i < permissions.length(); i++)
                {
                    JSONObject permission = (JSONObject)permissions.get(i);

                    if (Objects.equals(permission.getString("parent"), key)) {
                        for(int p = 0; p < permissions.length(); p++) {
                            JSONObject _per = (JSONObject)permissions.get(p);
                            if (Objects.equals(_per.getString("parent"), permission.getString("_id"))) {
                                JSONObject _v = new JSONObject();
                                _v.put("title", _per.getString("title"));
                                values.put(_v);
                            }
                        }
                        requests_list.add(new OrganisationDefaultListItem(permission.getString("_id"), permission.getString("title"), "", values, "permission", permission.getString("icon"), permission.getInt("status")));
                    } else if (Objects.equals(key, "0")) {
                        JSONObject _v = new JSONObject();
                        _v.put("title", permission.getString("title"));
                        values.put(_v);
                    }
                }
                if (Objects.equals(key, "0")) {
                    requests_list.add(new OrganisationDefaultListItem(organisation.getString("_id"), organisation.getString("title"), "", values, "organisation", organisation.getString("icon"), organisation.getInt("status")));
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return requests_list;
    }

    private ArrayList<DefaultListItem> getActivitiesList(String json) {
        ArrayList<DefaultListItem> activities_list = new ArrayList<>();
        JSONArray activities;
        try {
            activities = new JSONArray(json);
            for(int n = 0; n < activities.length(); n++) {
                JSONObject activity = (JSONObject)activities.get(n);
//                activities_list.add(new DefaultListItem(activity.getString("_id"), activity.getString("title"), activity.getString("description"), new JSONArray(), "activity", activity.getString("icon"), activity.getString("createdAt")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return activities_list;
    }
}
