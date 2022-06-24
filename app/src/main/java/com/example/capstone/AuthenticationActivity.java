package com.example.capstone;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;

import com.google.firebase.FirebaseApp;

//public class AuthenticationActivity extends AppCompatActivity {
//
//    FragmentManager fragmentManager = getSupportFragmentManager();
////    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//fragmentManager.beginTransaction()
//        .replace(R.id.fragment_container, ExampleFragment.class, null)
//    .setReorderingAllowed(true)
//    .addToBackStack("name") // name can be null
//    .commit();
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_authentication);
//    }
//
//}

public class AuthenticationActivity extends AppCompatActivity {
    public AuthenticationActivity() {
        super(R.layout.activity_authentication);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
        if (savedInstanceState == null) {
            Bundle bundle = new Bundle();
            bundle.putInt("some_int", 0);

            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.authentication, LoginFragment.class, bundle)
                    .commit();
        }
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