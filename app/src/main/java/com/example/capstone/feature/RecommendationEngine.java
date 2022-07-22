package com.example.capstone.feature;

import android.location.Location;
import android.util.Log;

import com.example.capstone.model.StudySession;
import com.example.capstone.model.User;
import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseGeoPoint;

import java.util.List;

public class RecommendationEngine {
    private static final String TAG = "RecommendationEngine";

    // declare weights HERE
    private static final int FIVE_KILOMETER = 5000;
    private static final int FIVE_KILOMETER_WEIGHT = 3;

    private static final int TEN_KILOMETER = 10000;
    private static final int TEN_KILOMETER_WEIGHT = 2;

    private static final int FIFTEEN_KILOMETER = 15000;
    private static final int FIFTEEN_KILOMETER_WEIGHT = 1;

    private static final double GREATER_THAN_FIFTEEN_KILOMETER_WEIGHT = 0.5;


    private static final double PREFERENCE_MULTIPLIER = 0.5;

    // all computation, assigning weights => all methods within this class

    // List<StudySession> -> StudySession

    // COME BACK and FILL methods, SPLIT methods into specific tasks, each method 4-6 lines
    public StudySession getHighestScoredSession(List<StudySession> studySessions, LatLng currentLatLng, User currentUser){
        double highestScore = 0;
        StudySession highestScoredSession = null;
        Log.i(TAG, Integer.toString(studySessions.size()));
        for (int i = 0; i < studySessions.size(); i++) {
            Log.i(TAG, studySessions.get(i).getObjectId());
            double studySessionScore = getSessionScore(studySessions.get(i), currentLatLng, currentUser.getStudyPreference());
            if (studySessionScore > highestScore) {
                highestScore = studySessionScore;
                highestScoredSession = studySessions.get(i);
            }
        }
        return highestScoredSession;
    }

    private double getSessionScore(StudySession studySession, LatLng currentLatLng, int userStudyPreference){
        return getLocationScore(studySession, currentLatLng) - getPreferencesScore(studySession, userStudyPreference);
    }

    private double getDistance(LatLng pointA, LatLng pointB){
        float results[] = new float[2];
        Location.distanceBetween(pointA.latitude, pointA.longitude, pointB.latitude, pointB.longitude, results);
        float distance = results[0];
        return distance;
    }

    private double getLocationScore(StudySession studySession, LatLng currentLatLng) {
        LatLng studySessionLocation = new LatLng(studySession.getLocation().getLatitude(), studySession.getLocation().getLongitude());
        double distance = getDistance(studySessionLocation, currentLatLng);
        if (distance <= FIVE_KILOMETER) {
            return FIVE_KILOMETER_WEIGHT;
        } else if (distance <= TEN_KILOMETER) {
            return TEN_KILOMETER_WEIGHT;
        } else if (distance <= FIFTEEN_KILOMETER) {
            return FIFTEEN_KILOMETER_WEIGHT;
        } else {
            return GREATER_THAN_FIFTEEN_KILOMETER_WEIGHT;
        }
    }

    private double getPreferencesScore(StudySession studySession, int userStudyPreference){
        return Math.abs((int) studySession.getStudyPreference() - userStudyPreference) * PREFERENCE_MULTIPLIER;
    }







}
