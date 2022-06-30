package com.example.capstone;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;


@ParseClassName("StudySession")
public class StudySession extends ParseObject {
    public static final String KEY_NUM_PARTICIPANTS = "num_participants";
    public static final String KEY_LOCATION = "location";
    public static final String KEY_START_TIME = "start_time";
    public static final String KEY_END_TIME = "end_time";
    public static final String KEY_SUBJECTS = "subjects";
    public static final String KEY_STUDY_PREFERENCE = "study_preference";
    public static final String KEY_OPEN_SESSION = "open_session";

    public Number getNumParticipants(){
        return getNumber(KEY_NUM_PARTICIPANTS);
    }
    public void setNumParticipants(Number numParticipants){
        put(KEY_NUM_PARTICIPANTS, numParticipants);
    }
    public String getLocation(){
        return getString(KEY_LOCATION);
    }
    public void setLocation(String location){
        put(KEY_LOCATION, location);
    }
    public String getStartTime(){
        return getString(KEY_START_TIME);
    }
    public void setStartTime(String startTime){
        put(KEY_START_TIME, startTime);
    }
    public String getEndTime(){
        return getString(KEY_END_TIME);
    }
    public void setEndTime(String endTime){
        put(KEY_END_TIME, endTime);
    }
    public String getSubjects(){
        return getString(KEY_SUBJECTS);
    }
    public void setSubjects(String subjects){
        put(KEY_SUBJECTS, subjects);
    }
    public Number getStudyPreference(){
        return getNumber(KEY_STUDY_PREFERENCE);
    }
    public void setStudyPreference(Number studyPreference){
        put(KEY_STUDY_PREFERENCE, studyPreference);
    }
    public Boolean getOpenSession(){
        return getBoolean(KEY_OPEN_SESSION);
    }
    public void setOpenSession(Boolean openSession){
        put(KEY_OPEN_SESSION, openSession);
    }

}
