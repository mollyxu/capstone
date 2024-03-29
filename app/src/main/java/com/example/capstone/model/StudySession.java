package com.example.capstone.model;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.Date;


@ParseClassName("StudySession")
public class StudySession extends ParseObject {
    public static final String KEY_NAME = "name";
    public static final String KEY_NUM_PARTICIPANTS = "num_participants";
    public static final String KEY_LOCATION = "location";
    public static final String KEY_START_TIME = "start_time";
    public static final String KEY_END_TIME = "end_time";
    public static final String KEY_SUBJECTS = "subjects";
    public static final String KEY_STUDY_PREFERENCE = "study_preference";
    public static final String KEY_OPEN_SESSION = "open_session";
    public static final String KEY_ORGANIZER_ID = "organizer_id";

    public static final String KEY_TILE_COORDINATE_ZOOM_15 = "tile_coordinate_zoom_15";
    public static final String KEY_TILE_COORDINATE_ZOOM_14 = "tile_coordinate_zoom_14";
    public static final String KEY_TILE_COORDINATE_ZOOM_13 = "tile_coordinate_zoom_13";
    public static final String KEY_TILE_COORDINATE_ZOOM_12 = "tile_coordinate_zoom_12";

    public String getName(){
        return getString(KEY_NAME);
    }
    public void setName(String name){
        put(KEY_NAME, name);
    }

    public int getNumParticipants(){
        return getInt(KEY_NUM_PARTICIPANTS);
    }
    public void setNumParticipants(int numParticipants){
        put(KEY_NUM_PARTICIPANTS, numParticipants);
    }

    public ParseGeoPoint getLocation(){
        return getParseGeoPoint(KEY_LOCATION);
    }
    public void setLocation(ParseGeoPoint location){
        put(KEY_LOCATION, location);
    }

    public String getTileCoordinateZoom15(){
        return getString(KEY_TILE_COORDINATE_ZOOM_15);
    }
    public void setTileCoordinateZoom15(String tileCoordinateZoom15){
        put(KEY_TILE_COORDINATE_ZOOM_15, tileCoordinateZoom15);
    }

    public String getTileCoordinateZoom14(){
        return getString(KEY_TILE_COORDINATE_ZOOM_14);
    }
    public void setTileCoordinateZoom14(String tileCoordinateZoom14){
        put(KEY_TILE_COORDINATE_ZOOM_14, tileCoordinateZoom14);
    }

    public String getTileCoordinateZoom13(){
        return getString(KEY_TILE_COORDINATE_ZOOM_13);
    }
    public void setTileCoordinateZoom13(String tileCoordinateZoom13){
        put(KEY_TILE_COORDINATE_ZOOM_13, tileCoordinateZoom13);
    }

    public String getTileCoordinateZoom12(){
        return getString(KEY_TILE_COORDINATE_ZOOM_12);
    }
    public void setTileCoordinateZoom12(String tileCoordinateZoom12){
        put(KEY_TILE_COORDINATE_ZOOM_12, tileCoordinateZoom12);
    }

    public Date getStartTime(){
        return getDate(KEY_START_TIME);
    }
    public void setStartTime(Date startTime){
        put(KEY_START_TIME, startTime);
    }
    public Date getEndTime(){
        return getDate(KEY_END_TIME);
    }
    public void setEndTime(Date endTime){
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

    public String getOrganizerId(){
        return getString(KEY_ORGANIZER_ID);
    }
    public void setOrganizerId(String organizerId){
        put(KEY_ORGANIZER_ID, organizerId);
    }
}
