package com.digipass.android.singletons;

import android.content.Context;

import com.digipass.android.objects.DefaultListItem;
import com.digipass.android.objects.OrganisationListItem;

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
        home_map.put("requests", GetRequestsList("0").get("organisations"));
        home_map.put("activities", GetActivitiesList());

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

    public  Map<String, ArrayList<DefaultListItem>> GetRequestsList(String key) {
        String json = context.getSharedPreferences("requests_data", Context.MODE_PRIVATE).getString("requests", "[]");
        json = "[{\"title\": \"CitizenM\", \"_id\": \"1\", \"icon\": \"http://project.cmi.hro.nl/2015_2016/emedia_mt2b_t4/digipass/images/organisations/citizenm.jpg\", \"status\":0, \"permissions\": [{\"parent\":\"1\", \"_id\": \"0\", \"title\": \"Bed size\", \"icon\": \"ic_food\", \"status\": 2, \"values\": [{\"title\": \"King size\", \"value\": \"true\"}]}]},{\"title\": \"HRO\", \"_id\": \"2\", \"icon\": \"http://project.cmi.hro.nl/2015_2016/emedia_mt2b_t4/digipass/images/organisations/hro.jpg\", \"status\":0, \"permissions\": [{\"parent\":\"2\", \"_id\": \"3\", \"title\": \"Eetvoorkeuren\", \"icon\": \"ic_food\", \"status\": 0, \"values\": []}, {\"parent\":\"3\", \"_id\": \"4\", \"title\": \"Allergieen\", \"icon\": \"\", \"status\": 0, \"values\": [{\"title\": \"Aardbei\", \"value\": \"true\"}]}]}]";
        Map<String, ArrayList<DefaultListItem>> requests_list = new HashMap<>();
        ArrayList<DefaultListItem> preference_list = new ArrayList<>();
        ArrayList<DefaultListItem> group_list = new ArrayList<>();
        ArrayList<DefaultListItem> organisation_list = new ArrayList<>();
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
                        if (permission.getJSONArray("values").length() == 0) {
                            group_list.add(new DefaultListItem(permission.getString("_id"), permission.getString("title"), "", values, "group", permission.getString("icon"), permission.getInt("status")));
                        } else {
                            preference_list.add(new DefaultListItem(permission.getString("_id"), permission.getString("title"), "", values, "preference", "", permission.getInt("status")));
                        }
                    } else if (Objects.equals(organisation.getString("_id"), permission.getString("parent"))) {
                        JSONObject _v = new JSONObject();
                        _v.put("title", permission.getString("title"));
                        values.put(_v);
                    }
                }
                if (Objects.equals(key, "0")) {
                    organisation_list.add(new OrganisationListItem(organisation.getString("_id"), organisation.getString("title"), "", values, "organisation", organisation.getString("icon"), organisation.getInt("status")));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        requests_list.put("preferences", preference_list);
        requests_list.put("categories", group_list);
        requests_list.put("organisations", organisation_list);
        return requests_list;
    }

    public ArrayList<DefaultListItem> GetActivitiesList() {
        String json = context.getSharedPreferences("preference_category_data", Context.MODE_PRIVATE).getString("preference_category_data", "[]");
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
