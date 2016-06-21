package com.digipass.android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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

        API api = API.getInstance(this);

        if(api.user == null){
            Log.d("SplashScreen ", "Not logged in!");
            startActivity(new Intent(this, LoginActivity.class));
        }
        else{
            Log.d("SplashScreen", "Logged in as " + api.user.toString());
            api.GetJSONResult();
            startActivity(new Intent(this, MainActivity.class));
        }


    }
}
