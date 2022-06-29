package com.example.capstone;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;


@ParseClassName("CustomUser")
public class User extends ParseObject {
    public static final String KEY_FIREBASE_UID = "firebase_uid";

    public String getFirebaseUid(){
        return getString(KEY_FIREBASE_UID);
    }

    public void setFirebaseUid(String firebase_uid){
        put(KEY_FIREBASE_UID, firebase_uid);
    }
}
