package com.digipass.android.objects;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.digipass.android.GenericListActivity;
import com.digipass.android.MainActivity;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PreferenceTask extends AsyncTask<Void, Void, ArrayList<Preference>> {

    private Context c;

    public PreferenceTask(Context c) {
        this.c = c;
    }

    public ArrayList<Preference> getPreference() {
        ArrayList<Preference> result = new ArrayList<>();

        HttpURLConnection con = null;
        Log.d(MainActivity.LOG_TAG, "Getting a preference");

        InputStream stream = null;
        String u_pref = "http://toinfinity.nl/digipass-api/preference/2?_format=json";
        String u_tax = "http://toinfinity.nl/digipass-api/categories?_format=json";
        SaveResult(u_pref, "preferences_data");
        SaveResult(u_tax, "preference_taxonomy_data");

        return result;

    }

    private void SaveResult(String url, String pref_key) {
        String encoding = "YWRtaW46dGhlYWxscm91bmRlcnM=";
        DefaultHttpClient httpclient = new DefaultHttpClient();

        HttpGet httpget_pref = new HttpGet(url);
        httpget_pref.setHeader("Authorization", "Basic " + encoding);
        HttpResponse response = null;
        try {
            response = httpclient.execute(httpget_pref);
            HttpEntity entity = response.getEntity();
            String str = readIt(entity.getContent());
            JSONArray arr = new JSONArray(str);
            SharedPreferences.Editor prefEditor = c.getSharedPreferences(pref_key, Context.MODE_PRIVATE).edit();
            prefEditor.putString(pref_key, arr.toString());
            prefEditor.apply();
            httpclient.getConnectionManager().shutdown();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected ArrayList<Preference> doInBackground(Void... params) {
        ArrayList pl = getPreference();
        return pl;
    }

    @Override
    protected void onPostExecute(ArrayList<Preference> result) {
        super.onPostExecute(result);
        // klaar geef seintje voor update

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

