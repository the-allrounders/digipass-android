package com.digipass.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.digipass.android.singletons.API;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();

        startActivity(new Intent(this, MainActivity.class));

        API api = API.getInstance(this);
        api.GetJSONResult();

        if(api.username == null){
            Log.d("SplashScreen ", "Not logged in!");
            startActivity(new Intent(this, LoginActivity.class));
        }
        else{
            Log.d("SplashScreen", "Logged in as " + api.username);
        }


    }
}
