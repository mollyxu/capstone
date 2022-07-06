package com.example.capstone;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;


@ParseClassName("CustomUser")
public class User extends ParseObject {
    public static final String KEY_FIREBASE_UID = "firebase_uid";
    public static final String KEY_FULL_NAME = "full_name";

    public String getFirebaseUid(){
        return getString(KEY_FIREBASE_UID);
    }
    public void setFirebaseUid(String firebaseUid){
        put(KEY_FIREBASE_UID, firebaseUid);
    }

    public String getFullName() { return getString(KEY_FULL_NAME); }
    public void setFullName(String fullName){
        put(KEY_FIREBASE_UID, fullName);
    }
}
