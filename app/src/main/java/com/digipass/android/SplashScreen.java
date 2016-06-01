package com.digipass.android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;

import com.digipass.android.objects.Preference;
import com.digipass.android.objects.PreferenceTask;
import com.digipass.android.singletons.API;

public class SplashScreen extends AppCompatActivity {

    // Splash screen timer
//    private static int SPLASH_TIME_OUT = 3000;
    private static int SPLASH_TIME_OUT = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        final Context c = this;

    }

    @Override
    protected void onStart() {
        super.onStart();

        new PreferenceTask(this).execute(); // TODO: TEMP!

        startActivity(new Intent(this, MainActivity.class));

        API api = new API(this);
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
                    Preference.ShowPreferenceList(c.getApplicationContext());
                    finish();
                }
            }, SPLASH_TIME_OUT);
        }


    }
}
