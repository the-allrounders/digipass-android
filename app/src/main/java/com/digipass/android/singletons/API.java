package com.digipass.android.singletons;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Looper;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
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

    public void Post(String url, JSONObject data, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        Log.d("API", "POST "+ url + " - " + data.toString());

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                BaseUrl + url,
                data,
                responseListener,
                errorListener
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                try {
                    String bearer = user.getString("Bearer");
                    headers.put("Authorization", "Bearer " + bearer);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return headers;
            }
        };

        queue.add(request);
    }

    public void GetJSONResult() {
        GetJSONResult(new Runnable() {
            @Override
            public void run() {

            }
        }, true, true, true);
    }

    public void GetJSONResult(Runnable runnable, String req_type) {
        switch (req_type) {
            case "req":
                GetJSONResult(runnable, false, false, true);
                break;
            case "pref":
                GetJSONResult(runnable, true, false, false);
                break;
            case "cat":
                GetJSONResult(runnable, false, true, false);
                break;
            case "all":
            default:
                GetJSONResult(runnable, true, true, true);
        }
    }


    public void GetJSONResult(Runnable runnable, Boolean pref, Boolean cat, Boolean req) {
        new GetJSONTask(c, runnable, pref, cat, req).execute();
    }

    public class GetJSONTask extends AsyncTask<Void, Void, String> {

        private Context c;
        private Runnable runnable;

        private Boolean get_pref;
        private Boolean get_cat;
        private Boolean get_req;

        public GetJSONTask(Context c, Runnable runnable) {
            this(c, runnable, true, true, true);
        }

        public GetJSONTask(Context c, Runnable runnable, Boolean pref, Boolean cat, Boolean req) {
            this.c = c;
            this.runnable = runnable;
            get_pref = pref;
            get_cat = cat;
            get_req = req;
        }

        private void SaveResult(String url, String pref_key) {
            DefaultHttpClient httpclient = new DefaultHttpClient();


            HttpGet request = new HttpGet(url);
            HttpResponse response;
            try {
                request.setHeader("Cache-Control", "no-cache");
                response = httpclient.execute(request);
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
            if (get_pref) SaveResult(getUserEndpoint() + "/preferences", "preferences_data");
            if (get_cat) SaveResult(BaseUrl + "/categories", "preference_category_data");
            if (get_req) SaveResult(getUserEndpoint() + "/requests?transform=true", "requests_data");
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            runnable.run();
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


    public void PostPreferenceTask(final JSONArray values, final String preference_id, final Runnable runnable, final Activity activity) {
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
                    HttpResponse response = client.execute(post);
                    ResponseHandler<String> handler = new BasicResponseHandler();
                    handler.handleResponse(response);
                    if (response.getStatusLine().getStatusCode() == 200) {
                        activity.runOnUiThread(runnable);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Looper.loop(); //Loop in the message queue
            }
        };

        t.start();
    }

    public void PostPermissionsTask(final ArrayList<String> permissions, final String status, final Runnable runnable, final Activity activity) {
        if (permissions == null || permissions.size() == 0) return;
        Thread t = new Thread() {

            public void run() {
                Looper.prepare(); //For Preparing Message Pool for the child Thread
                HttpClient client = new DefaultHttpClient();
                HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
                JSONObject json = new JSONObject();

                try {
                    HttpPut put = new HttpPut(getUserEndpoint() + "/permissions");
                    JSONArray json_permissions = new JSONArray();
                    for (int i = 0; i < permissions.size(); i++) {
                        json_permissions.put(permissions.get(i));
                    }
                    json.put("permissions", json_permissions);
                    json.put("status", status);
                    StringEntity se = new StringEntity(json.toString());
                    se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                    put.setEntity(se);
                    HttpResponse response = client.execute(put);
                    ResponseHandler<String> handler = new BasicResponseHandler();
                    handler.handleResponse(response);
                    if (response.getStatusLine().getStatusCode() == 200) {
                        activity.runOnUiThread(runnable);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Looper.loop(); //Loop in the message queue
            }
        };

        t.start();
    }

}
