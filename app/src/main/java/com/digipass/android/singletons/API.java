package com.digipass.android.singletons;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class API extends ContextWrapper {

    /**
     * Will be prefixed on all API requests
     */
    private String BaseUrl = "http://toinfinity.nl/digipass-api";

    /**
     * Stores all saved settings
     */
    private SharedPreferences preferences;

    /**
     * The username of the logged in user, or null if not logged in
     */
    public String username;

    /**
     * The display name of the user currently logged in
     */
    public String firstname;

    /**
     * The password of the logged in user, or null if not logged in
     */
    private String password;

    /**
     * The request queue from the Volley library
     */
    RequestQueue queue;

    /**
     * Initializes all variables
     * @param base The context that is used to get the sharedPreferences
     */
    public API(Context base) {

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
        username = preferences.getString("username", null);
        password = preferences.getString("password", null);
        firstname = preferences.getString("firstname", null);

        Log.d("API", "Done initializing");
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
        editor.remove("username");
        editor.remove("password");
        username = null;
        password = null;
        firstname = null;

        // Creating the request
        StringRequest request = new StringRequest(
                Request.Method.POST,
                BaseUrl + "/user/login",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("API", "Username and password are not right.");
                        callback.run();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(error.networkResponse.statusCode == 303){
                            Log.d("API", "Username and password are right.");

                            // Save in class
                            username = name;
                            password = pass;
                            firstname = Objects.equals(username.toLowerCase(), "schcj@hr.nl") ? "Stan" : username;

                            // Save in preferences
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("username", name);
                            editor.putString("password", pass);
                            editor.putString("firstname", firstname);
                            editor.apply();
                        }
                        else{
                            Log.d("API", "Unexpected API error: " + error.toString());
                        }
                        callback.run();
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();

                params.put("name", name);
                params.put("pass", pass);
                params.put("form_id", "user_login_form");

                return params;
            }

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }
        };

        // Add timeout of 30 seconds
        request.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Request!
        queue.add(request);

    }
}
