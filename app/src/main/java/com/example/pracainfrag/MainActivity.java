package com.example.pracainfrag;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pracainfrag.notLoggedIn.welcomeFragment;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(savedInstanceState == null)
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.welcomeFragmentContainer,new welcomeFragment()).commit();

    }

    @Override
    public void onBackPressed() {
        if(getSupportFragmentManager().findFragmentById(R.id.welcomeFragmentContainer) instanceof welcomeFragment) {
            finishAffinity();
            System.exit(0);
        }
        else
            getSupportFragmentManager().popBackStackImmediate();
    }
}