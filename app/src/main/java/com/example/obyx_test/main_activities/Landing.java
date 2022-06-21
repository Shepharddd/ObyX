package com.example.obyx_test.main_activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.obyx_test.R;

public class Landing extends AppCompatActivity {


    Button Register, SignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_landing);

        Register = findViewById(R.id.Register);
        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Register();
            }
        });

        SignIn = findViewById(R.id.SignIn);
        SignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignIn();
            }
        });

    }

    public void onStart() {

        super.onStart();
    }

    public void Register() {
        startActivity(new Intent(Landing.this, com.example.obyx_test.main_activities.Register.class));
    }

    public void SignIn() {
        startActivity(new Intent(Landing.this, com.example.obyx_test.main_activities.SignIn.class));
    }
}