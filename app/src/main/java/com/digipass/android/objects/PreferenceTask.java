package com.digipass.android.objects;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.digipass.android.GenericListActivity;
import com.digipass.android.MainActivity;

import org.apache.http.client.HttpClient;
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
import java.net.URL;
import java.util.ArrayList;

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

        try {

            URL url = new URL(
                    "http://project.cmi.hro.nl/2015_2016/emedia_mt2b_t4/json/preferences.json");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.connect();

            stream = conn.getInputStream();

            String str = readIt(stream);
            SharedPreferences sharedPref = c.getSharedPreferences("json_data", Context.MODE_PRIVATE);
            SharedPreferences.Editor prefEditor = c.getSharedPreferences("json_data", Context.MODE_PRIVATE).edit();
            prefEditor.putString("json_data", str);
            prefEditor.apply();
            conn.disconnect();
        } catch (IOException e) {
            Log.e(MainActivity.LOG_TAG, "IOException", e);
        } catch (Exception e) {
            Log.d(MainActivity.LOG_TAG, "Something went wrong... ", e);
        }
        // All done

        Log.d(MainActivity.LOG_TAG, "   -> returned: " + result);

        return result;

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

