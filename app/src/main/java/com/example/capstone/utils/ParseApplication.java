package com.example.capstone.utils;

import android.app.Application;

import com.example.capstone.model.StudySession;
import com.example.capstone.model.User;
import com.parse.Parse;
import com.parse.ParseObject;


public class ParseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(User.class);
        ParseObject.registerSubclass(StudySession.class);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("6kExN6kQpbHcaeee60WtY6Ndgs0XZHFyg6HaKh7Y")
                .clientKey("8B4yQ9HH2tWlm14VN6IbSfjwFFzTw19DI8DhHgZI")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}