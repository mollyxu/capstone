package com.example.capstone;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONArray;


@ParseClassName("CustomUser")
public class User extends ParseObject {
    public static final String KEY_FIREBASE_UID = "firebase_uid";
    public static final String KEY_FULL_NAME = "full_name";
    public static final String KEY_PROFILE_PICTURE = "profile_picture";
    public static final String KEY_JOINED_SESSIONS = "joined_sessions";

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

    public ParseFile getProfilePicture(){
        return getParseFile(KEY_PROFILE_PICTURE);
    }
    public void setProfilePicture(ParseFile profilePicture){
        put(KEY_PROFILE_PICTURE, profilePicture);
    }

    public JSONArray getJoinedSessions() { return getJSONArray(KEY_JOINED_SESSIONS); }
    public void setJoinedSessions(JSONArray joinedSessions){
        put(KEY_JOINED_SESSIONS, joinedSessions);
    }
}
