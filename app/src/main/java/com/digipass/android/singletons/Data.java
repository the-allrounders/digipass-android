package com.digipass.android.singletons;

import android.app.Activity;
import android.content.Context;

import com.digipass.android.objects.DefaultListItem;
import com.digipass.android.objects.StatusListItem;

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
            for (int i = 0; i < preferences.length(); i++) {
                JSONObject preference = (JSONObject) preferences.get(i);
                if (preference.getJSONArray("category").toString().contains("\"" + key + "\"")) {
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
            for (int n = 0; n < categories.length(); n++) {
                JSONObject category = (JSONObject) categories.get(n);
                JSONArray categoryParents = category.getJSONArray("parent");
                if (categoryParents.toString().contains("\"" + key + "\"") || (Objects.equals(key, "0") && categoryParents.length() == 0)) {
                    JSONArray values = new JSONArray();
                    for (int i = 0; i < preferences.length(); i++) {
                        JSONObject preference = (JSONObject) preferences.get(i);
                        if (preference.getJSONArray("category").toString().contains("\"" + category.getString("_id") + "\"")) {
                            JSONObject _v = new JSONObject();
                            _v.put("title", preference.getString("title"));
                            values.put(_v);
                        }
                    }
                    for (int a = 0; a < categories.length(); a++) {
                        JSONObject _category = (JSONObject) categories.get(a);
                        if (_category.has("parent") && _category.getJSONArray("parent").toString().contains("\"" + category.getString("_id") + "\"")) {
                            JSONObject _v = new JSONObject();
                            _v.put("title", _category.getString("title"));
                            values.put(_v);
                        }
                    }
                    preferences_list.add(new DefaultListItem(category.getString("_id"), category.getString("title"), "", values, "group", category.getString("icon")));
                }
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

    public Map<String, ArrayList<DefaultListItem>> GetRequestsList(String key) {
        String json = context.getSharedPreferences("requests_data", Context.MODE_PRIVATE).getString("requests_data", "[]");
        Map<String, ArrayList<DefaultListItem>> requests_list = new HashMap<>();
        ArrayList<DefaultListItem> preference_list = new ArrayList<>();
        ArrayList<DefaultListItem> group_list = new ArrayList<>();
        ArrayList<DefaultListItem> organisation_list = new ArrayList<>();
        JSONArray organisations;
        try {
            organisations = new JSONArray(json);
            for (int n = 0; n < organisations.length(); n++) {
                JSONObject request = (JSONObject) organisations.get(n);
                JSONArray permissions = request.getJSONArray("permissions");
                JSONArray values = new JSONArray();
                int total_preference_count = 0;
                int total_pending_count = 0;
                int total_approved_count = 0;
                int total_denied_count = 0;
                JSONArray organisation_children = new JSONArray();
                for (int i = 0; i < permissions.length(); i++) {
                    JSONObject permission = (JSONObject) permissions.get(i);

                    if (permission.has("preference")) {
                        JSONObject child = new JSONObject();
                        child.put("status", permission.getString("status"));
                        child.put("_id", permission.getString("_id"));
                        organisation_children.put(child);

                        total_preference_count++;
                        String child_status = permission.getString("status");
                        switch (child_status) {
                            case "approved":
                                total_approved_count++;
                                break;
                            case "denied":
                                total_denied_count++;
                                break;
                            case "pending":
                            default:
                                total_pending_count++;
                        }
                    }

                    if ((permission.has("parent") && permission.getJSONArray("parent").toString().contains("\""+ key +"\"")) || ((!permission.has("parent") || permission.getJSONArray("parent").length() == 0) && Objects.equals(request.getJSONObject("organisation").getString("_id"), key))) {
                        for (int p = 0; p < permissions.length(); p++) {
                            JSONObject _per = (JSONObject) permissions.get(p);
                            if (_per.has("parent") && _per.getJSONArray("parent").toString().contains("\""+ permission.getString("_id") +"\"")) {
                                JSONObject _v = new JSONObject();
                                _v.put("title", _per.has("preference") ? _per.getJSONObject("preference").getString("title"): _per.getString("title"));
                                values.put(_v);
                            }
                        }
                        if (permission.has("preference")) {
                            JSONObject pref = permission.getJSONObject("preference");
                            preference_list.add(new StatusListItem(permission.getString("_id"), pref.getString("title"), "", values, "preference", "", permission.getString("status"), new JSONArray()));
                        } else {
                            JSONArray children = permission.getJSONArray("children");
                            int cat_pending_count = 0;
                            int cat_approved_count = 0;
                            int cat_denied_count = 0;
                            for (int a = 0; a < children.length(); a++) {
                                String child_status = ((JSONObject)children.get(a)).getString("status");
                                switch (child_status) {
                                    case "approved":
                                        cat_approved_count++;
                                        break;
                                    case "denied":
                                        cat_denied_count++;
                                        break;
                                    case "pending":
                                    default:
                                        cat_pending_count++;
                                }
                            }
                            String cat_status;
                            if (cat_approved_count == children.length()) {
                                cat_status = "approved";
                            } else if (cat_denied_count == children.length()) {
                                cat_status = "denied";
                            } else if (cat_pending_count == children.length()) {
                                cat_status = "pending";
                            } else {
                                cat_status = "indeterminate";
                            }
                            group_list.add(new StatusListItem(permission.getString("_id"), permission.getString("title"), "", values, "group", permission.getString("icon"), cat_status, children));
                        }
                    } else if ((permission.has("parent") && permission.getJSONArray("parent").toString().contains("\""+ request.getJSONObject("organisation").getString("_id") +"\"")) || ((!permission.has("parent") || permission.getJSONArray("parent").length() == 0) && Objects.equals(key, "0"))) {
                        JSONObject _v = new JSONObject();
                        _v.put("title", permission.has("preference") ? permission.getJSONObject("preference").getString("title") : permission.getString("title"));
                        values.put(_v);
                    }
                }
                if (Objects.equals(key, "0")) {
                    String total_status;
                    if (total_approved_count == total_preference_count) {
                        total_status = "approved";
                    } else if (total_denied_count == total_preference_count) {
                        total_status = "denied";
                    } else if (total_pending_count == total_preference_count) {
                        total_status = "pending";
                    } else {
                        total_status = "indeterminate";
                    }
                    JSONObject org = request.getJSONObject("organisation");
                    organisation_list.add(new StatusListItem(org.getString("_id"), org.getString("title"), "", values, "organisation", org.getString("icon"), total_status, organisation_children));
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
        json = "[{\n" +
                "    \"_id\": \"7\",\n" +
                "    \"title\": \"Eetvoorkeur aangepast\",\n" +
                "    \"description\": \"Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.\",\n" +
                "    \"icon\": \"ic_help_feedback\",\n" +
                "    \"createdAt\": \"12:16\"\n" +
                "}, {\n" +
                "    \"_id\": \"8\",\n" +
                "    \"title\": \"Toegang geweigerd\",\n" +
                "    \"description\": \"Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.\",\n" +
                "    \"icon\": \"ic_hotel\",\n" +
                "    \"createdAt\": \"8:39\"\n" +
                "}]";
        ArrayList<DefaultListItem> activities_list = new ArrayList<>();
        JSONArray activities;
        try {
            activities = new JSONArray(json);
            for (int n = 0; n < activities.length(); n++) {
                JSONObject activity = (JSONObject) activities.get(n);
                activities_list.add(new DefaultListItem(activity.getString("_id"), activity.getString("title"), activity.getString("description"), new JSONArray(), "activity", activity.getString("icon"), activity.getString("createdAt")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return activities_list;
    }

    public void PrePermissionsPost(ArrayList<String> children, String status, Activity activity) {
        PrePermissionsPost(children, status, new Runnable() {
            @Override
            public void run() {

            }
        }, activity);
    }

    public void PrePermissionsPost(ArrayList<String> children, String status, Runnable runnable, Activity activity) {
        API.getInstance(context).PostPermissionsTask(children, status, runnable, activity);
    }
}
