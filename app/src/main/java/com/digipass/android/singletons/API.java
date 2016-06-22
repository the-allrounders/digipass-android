package com.digipass.android.singletons;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Looper;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class API extends ContextWrapper {

    /**
     * Will be prefixed on all API requests
     */
    private String BaseUrl = "http://digipass-api.herokuapp.com/api";

    /**
     * Stores all saved settings
     */
    private SharedPreferences preferences;

    /**
     * The JSON object
     */
    public JSONObject user;

    /**
     * The request queue from the Volley library
     */
    RequestQueue queue;

    private static API instance;

    /**
     * Context of the activity where the API is created
     */
    Context c;

    /**
     * Initializes all variables
     * @param base The context that is used to get the sharedPreferences
     */
    private API(Context base){

        super(base);

        queue = Volley.newRequestQueue(this, new HurlStack(){
            @Override
            protected HttpURLConnection createConnection(URL url) throws IOException {
                HttpURLConnection connection = super.createConnection(url);
                connection.setInstanceFollowRedirects(false);
                return connection;
            }
        });
        preferences = getSharedPreferences("User", 0);

        String userString = preferences.getString("user", null);
        try {
            if(userString != null) user = new JSONObject(userString);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        c = base;

        Log.d("API", "Done initializing");
    }

    public static API getInstance(Context c)
    {
        if(instance == null) {
            instance = new API(c);
        }

        return instance;

    }

    public String getUserEndpoint() {
        try {
            return BaseUrl + "/users/" + user.getJSONObject("User").getString("id");
        } catch (JSONException e) {
            e.printStackTrace();
            return "/";
        }
    }

    /**
     * Checks the given credentials, and updates the preferences if successful.
     * @param name The username of the user
     * @param pass The password of the user
     * @param callback Is called when the login was successful or unsuccessful
     */
    public void login(final String name, final String pass, final Runnable callback){

        Log.d("API", "Checking password for username "+name);

        // Remove previous username and password
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("user");
        editor.apply();
        user = null;

        // Creating the request
        JSONObject json = new JSONObject();
        try {
            json.put("username", name);
            json.put("password", pass);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                BaseUrl + "/users/login",
                json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("user", response.toString());
                        editor.apply();
                        user = response;
                        callback.run();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("API", "API error: " + error.toString());
                        callback.run();
                    }
                }
        );

        queue.add(request);
    }

    /**
     * Checks the given credentials, and updates the preferences if successful.
     * @param userData The data to pass along with the register request
     * @param callback Is called when the register was successful or unsuccessful
     */
    public void register(final JSONObject userData, final Runnable callback){

        Log.d("API", "Registering user " + userData.toString());

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                BaseUrl + "/users",
                userData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("user", response.toString());
                        editor.apply();
                        user = response;
                        callback.run();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("API", "API error: " + error.toString());
                        callback.run();
                    }
                }
        );

        queue.add(request);
    }

    public void GetJSONResult() {
        new GetJSONTask(c).execute();
    }

    public class GetJSONTask extends AsyncTask<Void, Void, String> {

        private Context c;

        public GetJSONTask(Context c) {
            this.c = c;
        }

        private void SaveResult(String url, String pref_key) {
            DefaultHttpClient httpclient = new DefaultHttpClient();


            HttpGet httpget_pref = new HttpGet(url);
            HttpResponse response;
            try {
                response = httpclient.execute(httpget_pref);
                HttpEntity entity = response.getEntity();
                String str = readIt(entity.getContent());
                JSONArray arr = new JSONArray(str);
                SharedPreferences.Editor prefEditor = c.getSharedPreferences(pref_key, Context.MODE_PRIVATE).edit();
                prefEditor.putString(pref_key, arr.toString());
                prefEditor.apply();
                httpclient.getConnectionManager().shutdown();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(Void... params) {
            SaveResult(getUserEndpoint() + "/preferences", "preferences_data");
            SaveResult(BaseUrl + "/categories", "preference_category_data");
            SaveResult(getUserEndpoint() + "/requests", "requests_data");
//            SaveResult("http://project.cmi.hro.nl/2015_2016/emedia_mt2b_t4/json/pref.json", "preferences_data");
//            SaveResult("http://project.cmi.hro.nl/2015_2016/emedia_mt2b_t4/json/cat.json", "preference_category_data");
            return "";
        }

        public String readIt(InputStream stream) throws IOException, UnsupportedEncodingException {

            if (stream != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "utf-8"), 8);

                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                stream.close();
                return sb.toString();
            }
            return "error: ";
        }

    }


    public void PostPreferenceTask(final ArrayList<String> values, final String preference_id) {
        Thread t = new Thread() {

            public void run() {
                Looper.prepare(); //For Preparing Message Pool for the child Thread
                HttpClient client = new DefaultHttpClient();
                HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
                JSONObject json = new JSONObject();

                try {
                    HttpPost post = new HttpPost(getUserEndpoint() + "/preferences");
                    json.put("values", values);
                    json.put("preference", preference_id);
                    StringEntity se = new StringEntity(json.toString());
                    se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                    post.setEntity(se);
                    client.execute(post);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Looper.loop(); //Loop in the message queue
            }
        };

        t.start();
    }

    public void PostPermissionsTask(final ArrayList<String> permissions, final String status) {
        if (permissions == null || permissions.size() == 0) return;
        Thread t = new Thread() {

            public void run() {
                Looper.prepare(); //For Preparing Message Pool for the child Thread
                HttpClient client = new DefaultHttpClient();
                HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
                JSONObject json = new JSONObject();

                try {
                    HttpPut put = new HttpPut(getUserEndpoint() + "/permissions");
                    json.put("permissions", permissions);
                    json.put("status", status);
                    StringEntity se = new StringEntity(json.toString());
                    se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                    put.setEntity(se);
                    client.execute(put);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Looper.loop(); //Loop in the message queue
            }
        };

        t.start();
    }

}
