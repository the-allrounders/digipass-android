package com.digipass.android.singletons;

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

    public  Map<String, ArrayList<DefaultListItem>> GetRequestsList(String key) {
        String json = context.getSharedPreferences("requests_data", Context.MODE_PRIVATE).getString("requests_data", "[]");
        json = "[\n" +

                "  {\n" +
                "    \"id\": \"5766fb5ef8a4037b446f9d4e\",\n" +
                "    \"status\": \"pending\",\n" +
                "    \"permissions\": [\n" +
                "      {\n" +
                "        \"_id\": \"57670157f8a4037b446f9d55\",\n" +
                "        \"preference\": {\n" +
                "          \"_id\": \"57669e2ed0bf105540f743d0\",\n" +
                "          \"title\": \"Allergieen\",\n" +
                "          \"description\": \"\",\n" +
                "          \"category\": [\n" +
                "            \"57669bd68fe61d3c40d2ae71\"\n" +
                "          ],\n" +
                "          \"type\": \"checkbox\",\n" +
                "          \"values\": [\n" +
                "            {\n" +
                "              \"title\": \"Noten\",\n" +
                "              \"value\": \"false\",\n" +
                "              \"_id\": \"57669e2ed0bf105540f743d2\"\n" +
                "            },\n" +
                "            {\n" +
                "              \"title\": \"Gluten\",\n" +
                "              \"value\": \"false\",\n" +
                "              \"_id\": \"57669e2ed0bf105540f743d1\"\n" +
                "            }\n" +
                "          ],\n" +
                "          \"icon\": \"\"\n" +
                "        },\n" +
                "        \"status\": \"pending\",\n" +
                "        \"parent\": \"5767006bf8a4037b446f9d53\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"_id\": \"57670180f8a4037b446f9d56\",\n" +
                "        \"preference\": {\n" +
                "          \"_id\": \"57669e6ed0bf105540f743d3\",\n" +
                "          \"title\": \"Vegetarisch\",\n" +
                "          \"description\": \"\",\n" +
                "          \"category\": [\n" +
                "            \"57669bd68fe61d3c40d2ae71\"\n" +
                "          ],\n" +
                "          \"type\": \"switch\",\n" +
                "          \"values\": [\n" +
                "            {\n" +
                "              \"title\": \"Vegetarisch\",\n" +
                "              \"value\": \"false\",\n" +
                "              \"_id\": \"57669e6ed0bf105540f743d4\"\n" +
                "            }\n" +
                "          ],\n" +
                "          \"icon\": \"\"\n" +
                "        },\n" +
                "        \"status\": \"pending\",\n" +
                "        \"parent\": \"5767006bf8a4037b446f9d53\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"_id\": \"576701dff8a4037b446f9d57\",\n" +
                "        \"preference\": {\n" +
                "          \"_id\": \"57669f1ad0bf105540f743d8\",\n" +
                "          \"title\": \"Kinderstoel\",\n" +
                "          \"description\": \"\",\n" +
                "          \"category\": [\n" +
                "            \"57669bd68fe61d3c40d2ae71\"\n" +
                "          ],\n" +
                "          \"type\": \"switch\",\n" +
                "          \"values\": [\n" +
                "            {\n" +
                "              \"title\": \"Kinderstoel\",\n" +
                "              \"value\": \"false\",\n" +
                "              \"_id\": \"57669f1ad0bf105540f743d9\"\n" +
                "            }\n" +
                "          ],\n" +
                "          \"icon\": \"\"\n" +
                "        },\n" +
                "        \"status\": \"pending\",\n" +
                "        \"parent\": \"57670081f8a4037b446f9d54\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"_id\": \"576701dff8a4037b446f9d57\",\n" +
                "        \"preference\": {\n" +
                "          \"_id\": \"57669f1ad0bf105540f743d8\",\n" +
                "          \"title\": \"Kinderstoel\",\n" +
                "          \"description\": \"\",\n" +
                "          \"category\": [\n" +
                "            \"57669bd68fe61d3c40d2ae71\"\n" +
                "          ],\n" +
                "          \"type\": \"switch\",\n" +
                "          \"values\": [\n" +
                "            {\n" +
                "              \"title\": \"Kinderstoel\",\n" +
                "              \"value\": \"false\",\n" +
                "              \"_id\": \"57669f1ad0bf105540f743d9\"\n" +
                "            }\n" +
                "          ],\n" +
                "          \"icon\": \"\"\n" +
                "        },\n" +
                "        \"status\": \"pending\",\n" +
                "        \"parent\": \"57670081f8a4037b446f9d54z\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"_id\": \"576701dff8a4037b446f9d57\",\n" +
                "        \"preference\": {\n" +
                "          \"_id\": \"57669f1ad0bf105540f743d8\",\n" +
                "          \"title\": \"Kinderstoel\",\n" +
                "          \"description\": \"\",\n" +
                "          \"category\": [\n" +
                "            \"57669bd68fe61d3c40d2ae71\"\n" +
                "          ],\n" +
                "          \"type\": \"switch\",\n" +
                "          \"values\": [\n" +
                "            {\n" +
                "              \"title\": \"Kinderstoel\",\n" +
                "              \"value\": \"false\",\n" +
                "              \"_id\": \"57669f1ad0bf105540f743d9\"\n" +
                "            }\n" +
                "          ],\n" +
                "          \"icon\": \"\"\n" +
                "        },\n" +
                "        \"status\": \"pending\",\n" +
                "        \"parent\": \"57670081f8a4037b446f9d54z\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"_id\": \"57670201f8a4037b446f9d58\",\n" +
                "        \"preference\": {\n" +
                "          \"_id\": \"5766f68ef8a4037b446f9d39\",\n" +
                "          \"title\": \"Bedformaat\",\n" +
                "          \"description\": \"\",\n" +
                "          \"category\": [\n" +
                "            \"5766f5d0f8a4037b446f9d38\"\n" +
                "          ],\n" +
                "          \"type\": \"checkbox\",\n" +
                "          \"values\": [\n" +
                "            {\n" +
                "              \"title\": \"King size\",\n" +
                "              \"value\": \"true\",\n" +
                "              \"_id\": \"5766f68ef8a4037b446f9d3c\"\n" +
                "            },\n" +
                "            {\n" +
                "              \"title\": \"Tweepersoons\",\n" +
                "              \"value\": \"true\",\n" +
                "              \"_id\": \"5766f68ef8a4037b446f9d3b\"\n" +
                "            },\n" +
                "            {\n" +
                "              \"title\": \"Eenpersoons\",\n" +
                "              \"value\": \"true\",\n" +
                "              \"_id\": \"5766f68ef8a4037b446f9d3a\"\n" +
                "            }\n" +
                "          ],\n" +
                "          \"icon\": \"\"\n" +
                "        },\n" +
                "        \"status\": \"pending\",\n" +
                "        \"parent\": \"57670028f8a4037b446f9d52\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"_id\": \"5767025af8a4037b446f9d59\",\n" +
                "        \"preference\": {\n" +
                "          \"_id\": \"5766f6d3f8a4037b446f9d3d\",\n" +
                "          \"title\": \"Uren per nacht\",\n" +
                "          \"description\": \"\",\n" +
                "          \"category\": [\n" +
                "            \"5766f5a0f8a4037b446f9d37\"\n" +
                "          ],\n" +
                "          \"type\": \"slider\",\n" +
                "          \"values\": [\n" +
                "            {\n" +
                "              \"title\": \"Uren per nacht\",\n" +
                "              \"value\": \"8\",\n" +
                "              \"_id\": \"5766f6d3f8a4037b446f9d3e\"\n" +
                "            }\n" +
                "          ],\n" +
                "          \"icon\": \"\"\n" +
                "        },\n" +
                "        \"status\": \"pending\",\n" +
                "        \"parent\": \"5766ffd7f8a4037b446f9d50\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"_id\": \"57670271f8a4037b446f9d5a\",\n" +
                "        \"preference\": {\n" +
                "          \"_id\": \"5766f723f8a4037b446f9d3f\",\n" +
                "          \"title\": \"Bedstand\",\n" +
                "          \"description\": \"\",\n" +
                "          \"category\": [\n" +
                "            \"5766f5d0f8a4037b446f9d38\"\n" +
                "          ],\n" +
                "          \"type\": \"slider\",\n" +
                "          \"values\": [\n" +
                "            {\n" +
                "              \"title\": \"Plat\",\n" +
                "              \"value\": \"0\",\n" +
                "              \"_id\": \"5766f723f8a4037b446f9d42\"\n" +
                "            },\n" +
                "            {\n" +
                "              \"title\": \"Licht Verhoogd\",\n" +
                "              \"value\": \"1\",\n" +
                "              \"_id\": \"5766f723f8a4037b446f9d41\"\n" +
                "            },\n" +
                "            {\n" +
                "              \"title\": \"Verhoogd\",\n" +
                "              \"value\": \"2\",\n" +
                "              \"_id\": \"5766f723f8a4037b446f9d40\"\n" +
                "            }\n" +
                "          ],\n" +
                "          \"icon\": \"\"\n" +
                "        },\n" +
                "        \"status\": \"pending\",\n" +
                "        \"parent\": \"57670028f8a4037b446f9d52\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"_id\": \"57670290f8a4037b446f9d5b\",\n" +
                "        \"preference\": {\n" +
                "          \"_id\": \"5766f7a7f8a4037b446f9d44\",\n" +
                "          \"title\": \"Kleur licht\",\n" +
                "          \"description\": \"\",\n" +
                "          \"category\": [\n" +
                "            \"5766f75ff8a4037b446f9d43\"\n" +
                "          ],\n" +
                "          \"type\": \"checkbox\",\n" +
                "          \"values\": [\n" +
                "            {\n" +
                "              \"title\": \"Rood\",\n" +
                "              \"value\": \"true\",\n" +
                "              \"_id\": \"5766f7a7f8a4037b446f9d48\"\n" +
                "            },\n" +
                "            {\n" +
                "              \"title\": \"Groen\",\n" +
                "              \"value\": \"true\",\n" +
                "              \"_id\": \"5766f7a7f8a4037b446f9d47\"\n" +
                "            },\n" +
                "            {\n" +
                "              \"title\": \"Blauw\",\n" +
                "              \"value\": \"true\",\n" +
                "              \"_id\": \"5766f7a7f8a4037b446f9d46\"\n" +
                "            },\n" +
                "            {\n" +
                "              \"title\": \"Geel\",\n" +
                "              \"value\": \"true\",\n" +
                "              \"_id\": \"5766f7a7f8a4037b446f9d45\"\n" +
                "            }\n" +
                "          ],\n" +
                "          \"icon\": \"\"\n" +
                "        },\n" +
                "        \"status\": \"pending\",\n" +
                "        \"parent\": \"5766ffeaf8a4037b446f9d51\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"_id\": \"576702a4f8a4037b446f9d5c\",\n" +
                "        \"preference\": {\n" +
                "          \"_id\": \"5766f7d9f8a4037b446f9d49\",\n" +
                "          \"title\": \"Licht felheid\",\n" +
                "          \"description\": \"\",\n" +
                "          \"category\": [\n" +
                "            \"5766f75ff8a4037b446f9d43\"\n" +
                "          ],\n" +
                "          \"type\": \"slider\",\n" +
                "          \"values\": [\n" +
                "            {\n" +
                "              \"title\": \"Uit\",\n" +
                "              \"value\": \"0\",\n" +
                "              \"_id\": \"5766f7d9f8a4037b446f9d4d\"\n" +
                "            },\n" +
                "            {\n" +
                "              \"title\": \"Gedimd\",\n" +
                "              \"value\": \"1\",\n" +
                "              \"_id\": \"5766f7d9f8a4037b446f9d4c\"\n" +
                "            },\n" +
                "            {\n" +
                "              \"title\": \"Normaal\",\n" +
                "              \"value\": \"2\",\n" +
                "              \"_id\": \"5766f7d9f8a4037b446f9d4b\"\n" +
                "            },\n" +
                "            {\n" +
                "              \"title\": \"Fel\",\n" +
                "              \"value\": \"3\",\n" +
                "              \"_id\": \"5766f7d9f8a4037b446f9d4a\"\n" +
                "            }\n" +
                "          ],\n" +
                "          \"icon\": \"\"\n" +
                "        },\n" +
                "        \"status\": \"pending\",\n" +
                "        \"parent\": \"5766ffeaf8a4037b446f9d51\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"_id\": \"5766ffa8f8a4037b446f9d4f\",\n" +
                "        \"category\": {\n" +
                "          \"title\": \"Hotel\",\n" +
                "          \"id\": \"5766f553f8a4037b446f9d36\",\n" +
                "          \"icon\": \"\"\n" +
                "        },\n" +
                "        \"request\": \"5766fb5ef8a4037b446f9d4e\",\n" +
                "        \"children\": [\n" +
                "          \"5766ffd7f8a4037b446f9d50\",\n" +
                "          \"5766ffeaf8a4037b446f9d51\",\n" +
                "          \"5767025af8a4037b446f9d59\",\n" +
                "          \"57670290f8a4037b446f9d5b\",\n" +
                "          \"576702a4f8a4037b446f9d5c\",\n" +
                "          \"57670028f8a4037b446f9d52\",\n" +
                "          \"57670201f8a4037b446f9d58\",\n" +
                "          \"57670271f8a4037b446f9d5a\"\n" +
                "        ]\n" +
                "      },\n" +
                "      {\n" +
                "        \"_id\": \"5766ffd7f8a4037b446f9d50\",\n" +
                "        \"category\": {\n" +
                "          \"title\": \"Slaapvoorkeuren\",\n" +
                "          \"id\": \"5766f5a0f8a4037b446f9d37\",\n" +
                "          \"icon\": \"\"\n" +
                "        },\n" +
                "        \"parent\": \"5766ffa8f8a4037b446f9d4f\",\n" +
                "        \"request\": \"5766fb5ef8a4037b446f9d4e\",\n" +
                "        \"children\": [\n" +
                "          \"5767025af8a4037b446f9d59\",\n" +
                "          \"57670028f8a4037b446f9d52\",\n" +
                "          \"57670201f8a4037b446f9d58\",\n" +
                "          \"57670271f8a4037b446f9d5a\"\n" +
                "        ]\n" +
                "      },\n" +
                "      {\n" +
                "        \"_id\": \"5766ffeaf8a4037b446f9d51\",\n" +
                "        \"category\": {\n" +
                "          \"title\": \"Verlichting\",\n" +
                "          \"id\": \"5766f75ff8a4037b446f9d43\",\n" +
                "          \"icon\": \"\"\n" +
                "        },\n" +
                "        \"parent\": \"5766ffa8f8a4037b446f9d4f\",\n" +
                "        \"request\": \"5766fb5ef8a4037b446f9d4e\",\n" +
                "        \"children\": [\n" +
                "          \"57670290f8a4037b446f9d5b\",\n" +
                "          \"576702a4f8a4037b446f9d5c\"\n" +
                "        ]\n" +
                "      },\n" +
                "      {\n" +
                "        \"_id\": \"57670028f8a4037b446f9d52\",\n" +
                "        \"category\": {\n" +
                "          \"title\": \"Bedvoorkeuren\",\n" +
                "          \"id\": \"5766f5d0f8a4037b446f9d38\",\n" +
                "          \"icon\": \"\"\n" +
                "        },\n" +
                "        \"parent\": \"5766ffd7f8a4037b446f9d50\",\n" +
                "        \"request\": \"5766fb5ef8a4037b446f9d4e\",\n" +
                "        \"children\": [\n" +
                "          \"57670201f8a4037b446f9d58\",\n" +
                "          \"57670271f8a4037b446f9d5a\"\n" +
                "        ]\n" +
                "      },\n" +
                "      {\n" +
                "        \"_id\": \"5767006bf8a4037b446f9d53\",\n" +
                "        \"status\": \"approved\",\n" +
                "        \"category\": {\n" +
                "          \"title\": \"Restaurant\",\n" +
                "          \"id\": \"57669bd68fe61d3c40d2ae71\",\n" +
                "          \"icon\": \"\"\n" +
                "        },\n" +
                "        \"request\": \"5766fb5ef8a4037b446f9d4e\",\n" +
                "        \"children\": [\n" +
                "          \"57670157f8a4037b446f9d55\",\n" +
                "          \"57670180f8a4037b446f9d56\",\n" +
                "          \"57670081f8a4037b446f9d54\",\n" +
                "          \"576701dff8a4037b446f9d57\"\n" +
                "        ]\n" +
                "      },\n" +
                "      {\n" +
                "        \"_id\": \"57670081f8a4037b446f9d54\",\n" +
                "        \"category\": {\n" +
                "          \"title\": \"Eetvoorkeuren\",\n" +
                "          \"id\": \"57669be08fe61d3c40d2ae72\",\n" +
                "          \"icon\": \"\"\n" +
                "        },\n" +
                "        \"parent\": \"5767006bf8a4037b446f9d53\",\n" +
                "        \"request\": \"5766fb5ef8a4037b446f9d4e\",\n" +
                "        \"children\": [\n" +
                "          \"576701dff8a4037b446f9d57\"\n" +
                "        ]\n" +
                "      },\n" +
                "      {\n" +
                "        \"_id\": \"57670081f8a4037b446f9d54z\",\n" +
                "        \"category\": {\n" +
                "          \"title\": \"Eetvoorkeuren\",\n" +
                "          \"id\": \"57669be08fe61d3c40d2ae72\",\n" +
                "          \"icon\": \"\"\n" +
                "        },\n" +
                "        \"parent\": \"5767006bf8a4037b446f9d53\",\n" +
                "        \"request\": \"5766fb5ef8a4037b446f9d4e\",\n" +
                "        \"children\": [\n" +
                "          \"576701dff8a4037b446f9d57\"\n" +
                "        ]\n" +
                "      }\n" +
                "    ],\n" +
                "    \"organisation\": {\n" +
                "      \"_id\": \"57669b4b8fe61d3c40d2ae6e\",\n" +
                "      \"title\": \"CitizenM\",\n" +
                "      \"icon\": \"http://project.cmi.hro.nl/2015_2016/emedia_mt2b_t4/digipass/images/organisations/citizenm.jpg\"\n" +
                "    }\n" +
                "  },\n" +
                "  {\n" +
                "    \"id\": \"5766bc6a7c32934b42f14f61\",\n" +
                "    \"status\": \"pending\",\n" +
                "    \"permissions\": [\n" +
                "      {\n" +
                "        \"_id\": \"5766be567c32934b42f14f64\",\n" +
                "        \"preference\": {\n" +
                "          \"_id\": \"57669e6ed0bf105540f743d3\",\n" +
                "          \"title\": \"Vegetarisch\",\n" +
                "          \"description\": \"\",\n" +
                "          \"category\": [\n" +
                "            \"57669bd68fe61d3c40d2ae71\"\n" +
                "          ],\n" +
                "          \"type\": \"switch\",\n" +
                "          \"values\": [\n" +
                "            {\n" +
                "              \"title\": \"Vegetarisch\",\n" +
                "              \"value\": \"false\",\n" +
                "              \"_id\": \"57669e6ed0bf105540f743d4\"\n" +
                "            }\n" +
                "          ],\n" +
                "          \"icon\": \"\"\n" +
                "        },\n" +
                "        \"status\": \"pending\",\n" +
                "        \"parent\": \"5766bddb7c32934b42f14f63\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"_id\": \"5766be637c32934b42f14f65\",\n" +
                "        \"preference\": {\n" +
                "          \"_id\": \"57669e2ed0bf105540f743d0\",\n" +
                "          \"title\": \"Allergieen\",\n" +
                "          \"description\": \"\",\n" +
                "          \"category\": [\n" +
                "            \"57669bd68fe61d3c40d2ae71\"\n" +
                "          ],\n" +
                "          \"type\": \"checkbox\",\n" +
                "          \"values\": [\n" +
                "            {\n" +
                "              \"title\": \"Noten\",\n" +
                "              \"value\": \"false\",\n" +
                "              \"_id\": \"57669e2ed0bf105540f743d2\"\n" +
                "            },\n" +
                "            {\n" +
                "              \"title\": \"Gluten\",\n" +
                "              \"value\": \"false\",\n" +
                "              \"_id\": \"57669e2ed0bf105540f743d1\"\n" +
                "            }\n" +
                "          ],\n" +
                "          \"icon\": \"\"\n" +
                "        },\n" +
                "        \"status\": \"pending\",\n" +
                "        \"parent\": \"5766bddb7c32934b42f14f63\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"_id\": \"5766be807c32934b42f14f66\",\n" +
                "        \"preference\": {\n" +
                "          \"_id\": \"57669f1ad0bf105540f743d8\",\n" +
                "          \"title\": \"Kinderstoel\",\n" +
                "          \"description\": \"\",\n" +
                "          \"category\": [\n" +
                "            \"57669bd68fe61d3c40d2ae71\"\n" +
                "          ],\n" +
                "          \"type\": \"switch\",\n" +
                "          \"values\": [\n" +
                "            {\n" +
                "              \"title\": \"Kinderstoel\",\n" +
                "              \"value\": \"false\",\n" +
                "              \"_id\": \"57669f1ad0bf105540f743d9\"\n" +
                "            }\n" +
                "          ],\n" +
                "          \"icon\": \"\"\n" +
                "        },\n" +
                "        \"status\": \"pending\",\n" +
                "        \"parent\": \"5766bdb27c32934b42f14f62\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"_id\": \"5766be807c32934b42f14f66\",\n" +
                "        \"preference\": {\n" +
                "          \"_id\": \"57669f1ad0bf105540f743d8\",\n" +
                "          \"title\": \"Kinderstoel\",\n" +
                "          \"description\": \"\",\n" +
                "          \"category\": [\n" +
                "            \"57669bd68fe61d3c40d2ae71\"\n" +
                "          ],\n" +
                "          \"type\": \"switch\",\n" +
                "          \"values\": [\n" +
                "            {\n" +
                "              \"title\": \"Kinderstoel\",\n" +
                "              \"value\": \"false\",\n" +
                "              \"_id\": \"57669f1ad0bf105540f743d9\"\n" +
                "            }\n" +
                "          ],\n" +
                "          \"icon\": \"\"\n" +
                "        },\n" +
                "        \"status\": \"pending\",\n" +
                "        \"parent\": \"5766bdb27c32934b42f14f62\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"_id\": \"5766be807c32934b42f14f66\",\n" +
                "        \"preference\": {\n" +
                "          \"_id\": \"57669f1ad0bf105540f743d8\",\n" +
                "          \"title\": \"Kinderstoel\",\n" +
                "          \"description\": \"\",\n" +
                "          \"category\": [\n" +
                "            \"57669bd68fe61d3c40d2ae71\"\n" +
                "          ],\n" +
                "          \"type\": \"switch\",\n" +
                "          \"values\": [\n" +
                "            {\n" +
                "              \"title\": \"Kinderstoel\",\n" +
                "              \"value\": \"false\",\n" +
                "              \"_id\": \"57669f1ad0bf105540f743d9\"\n" +
                "            }\n" +
                "          ],\n" +
                "          \"icon\": \"\"\n" +
                "        },\n" +
                "        \"status\": \"pending\",\n" +
                "        \"parent\": \"5766bdb27c32934b42f14f62\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"_id\": \"5766bdb27c32934b42f14f62\",\n" +
                "        \"category\": {\n" +
                "          \"title\": \"Restaurant\",\n" +
                "          \"id\": \"57669bd68fe61d3c40d2ae71\",\n" +
                "          \"icon\": \"\"\n" +
                "        },\n" +
                "        \"request\": \"5766bc6a7c32934b42f14f61\",\n" +
                "        \"children\": [\n" +
                "          \"5766be807c32934b42f14f66\",\n" +
                "          \"5766bddb7c32934b42f14f63\",\n" +
                "          \"5766be567c32934b42f14f64\",\n" +
                "          \"5766be637c32934b42f14f65\"\n" +
                "        ]\n" +
                "      },\n" +
                "      {\n" +
                "        \"_id\": \"5766bddb7c32934b42f14f63\",\n" +
                "        \"category\": {\n" +
                "          \"title\": \"Eetvoorkeuren\",\n" +
                "          \"id\": \"57669be08fe61d3c40d2ae72\",\n" +
                "          \"icon\": \"\"\n" +
                "        },\n" +
                "        \"parent\": \"5766bdb27c32934b42f14f62\",\n" +
                "        \"request\": \"5766bc6a7c32934b42f14f61\",\n" +
                "        \"children\": [\n" +
                "          \"5766be567c32934b42f14f64\",\n" +
                "          \"5766be637c32934b42f14f65\"\n" +
                "        ]\n" +
                "      }\n" +
                "    ],\n" +
                "    \"organisation\": {\n" +
                "      \"_id\": \"5768f60a0526c1100071d3ba\",\n" +
                "      \"title\": \"HRO\",\n" +
                "      \"icon\": \"http://project.cmi.hro.nl/2015_2016/emedia_mt2b_t4/digipass/images/organisations/hro.jpg\"\n" +
                "    }\n" +
                "  }\n" +
                "]";
        Map<String, ArrayList<DefaultListItem>> requests_list = new HashMap<>();
        ArrayList<DefaultListItem> preference_list = new ArrayList<>();
        ArrayList<DefaultListItem> group_list = new ArrayList<>();
        ArrayList<DefaultListItem> organisation_list = new ArrayList<>();
        JSONArray organisations;
        try {
            organisations = new JSONArray(json);
            for(int n = 0; n < organisations.length(); n++) {
                JSONObject request = (JSONObject)organisations.get(n);
                JSONArray permissions = request.getJSONArray("permissions");
                JSONArray values = new JSONArray();
                for(int i = 0; i < permissions.length(); i++) {
                    JSONObject permission = (JSONObject)permissions.get(i);

                    if ((permission.has("parent") && Objects.equals(permission.getString("parent"), key)) || (!permission.has("parent") && Objects.equals(request.getJSONObject("organisation").getString("_id"), key))) {
                        for(int p = 0; p < permissions.length(); p++) {
                            JSONObject _per = (JSONObject)permissions.get(p);
                            if (_per.has("parent") && Objects.equals(_per.getString("parent"), permission.getString("_id"))) {
                                JSONObject _v = new JSONObject();
                                _v.put("title", _per.has("category") ? _per.getJSONObject("category").getString("title") : _per.getJSONObject("preference").getString("title"));
                                values.put(_v);
                            }
                        }
                        if (permission.has("category")) {
                            JSONObject cat = permission.getJSONObject(("category"));
                            group_list.add(new StatusListItem(permission.getString("_id"), cat.getString("title"), "", values, "group", cat.getString("icon"), permission.has("status") ? permission.getString("status") : "pending", permission.getJSONArray("children")));
                        } else {
                            JSONObject pref = permission.getJSONObject("preference");
                            preference_list.add(new StatusListItem(permission.getString("_id"), pref.getString("title"), "", values, "preference", "", permission.getString("status"), new JSONArray()));
                        }
                    } else if ((permission.has("parent") && Objects.equals(request.getJSONObject("organisation").getString("_id"), permission.getString("parent"))) || (!permission.has("parent") && Objects.equals(key, "0"))) {
                        JSONObject _v = new JSONObject();
                        _v.put("title", permission.has("category") ? permission.getJSONObject("category").getString("title") : permission.getJSONObject("preference").getString("title"));
                        values.put(_v);
                    }
                }
                if (Objects.equals(key, "0")) {
                    JSONObject org = request.getJSONObject("organisation");
                    organisation_list.add(new StatusListItem(org.getString("_id"), org.getString("title"), "", values, "organisation", org.getString("icon"), request.getString("status"), new JSONArray()));
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
            for(int n = 0; n < activities.length(); n++) {
                JSONObject activity = (JSONObject)activities.get(n);
                activities_list.add(new DefaultListItem(activity.getString("_id"), activity.getString("title"), activity.getString("description"), new JSONArray(), "activity", activity.getString("icon"), activity.getString("createdAt")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return activities_list;
    }

    public void PrePermissionsPost(  ArrayList<String> children, String status) {
        API.getInstance(context).PostPermissionsTask(children, status);
    }
}
