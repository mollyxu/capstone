package com.example.capstone;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class HomescreenActivity extends AppCompatActivity {

    private static final String TAG = "HomescreenActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homescreen);

        Button btnStartSession = findViewById(R.id.btn_start_session);


        btnStartSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick start session button");
                replaceFragment(R.id.homescreen, StartSessionFragment.class);

            }
        });
    }

    public void replaceFragment(@IdRes int containerViewId, @NonNull Class<? extends androidx.fragment.app.Fragment> fragmentClass){
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(containerViewId, fragmentClass, null)
                .setReorderingAllowed(true)
                .addToBackStack("name") // name can be null
                .commit();
    }


}