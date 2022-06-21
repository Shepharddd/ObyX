package com.example.obyx_test.main_activities;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.example.obyx_test.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


        View decorView = getWindow().getDecorView();
        // Hide both the navigation bar and the status bar.
        // SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
        // a general rule, you should design your app to hide the status bar whenever you
        // hide the navigation bar.
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        decorView.setSystemUiVisibility(uiOptions);

        Handler handler = new Handler();


        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms
                if (user != null) {
                    startActivity(new Intent(Splash.this, Home.class));
                } else {
                    startActivity(new Intent(Splash.this, Landing.class));
                }
                finish();
            }
        }, 2000);

        // code to be run while initial loading


    }
}