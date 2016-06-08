package com.digipass.android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.digipass.android.singletons.API;

public class SplashScreen extends AppCompatActivity {

    // Splash screen timer
//    private static int SPLASH_TIME_OUT = 3000;
    private static int SPLASH_TIME_OUT = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Context c = this;

    }

    @Override
    protected void onStart() {
        super.onStart();

        startActivity(new Intent(this, MainActivity.class));

        API api = new API(this);
        api.GetJSONResult();

        final Context c = this;

        if(api.username == null){
            Log.d("SplashScreen ", "Not logged in!");
            startActivity(new Intent(this, LoginActivity.class));
        }
        else{
            Log.d("SplashScreen", "Logged in as " + api.username);

            new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

                @Override
                public void run() {
//                    Preference.ShowPreferenceList(c.getApplicationContext());
//                    finish();
                }
            }, SPLASH_TIME_OUT);
        }


    }
}
