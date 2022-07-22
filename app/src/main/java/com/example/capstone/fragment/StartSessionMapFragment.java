package com.example.capstone.fragment;

import android.graphics.Point;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.capstone.R;
import com.example.capstone.model.StudySession;
import com.example.capstone.activity.HomescreenActivity;
import com.example.capstone.model.User;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.parse.ParseGeoPoint;

public class StartSessionMapFragment extends Fragment {
    private FirebaseAuth mAuth;

    private static final String TAG = "StartSessionMapFragment";

    private Button btnStartPreferences;

    private String studySessionId;

    public StartSessionMapFragment() {}

    public static StartSessionMapFragment newInstance(String studySessionId) {
        StartSessionMapFragment fragment = new StartSessionMapFragment();
        Bundle args = new Bundle();
        args.putString("studySessionId", studySessionId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        if (getArguments() != null) {
            studySessionId = getArguments().getString("studySessionId");
            Log.i(TAG, "check if arguments: " + studySessionId);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_start_session_map, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        btnStartPreferences = getActivity().findViewById(R.id.btn_start_preferences);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.start_session_map);
        ((HomescreenActivity) getActivity()).prepareMap(mapFragment);

        btnStartPreferences.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick confirm button");

                updateDraftStudySession();
                updateCurrentUser();
                navigateToNextFragment();
            }
        });
    }

    private void navigateToNextFragment(){
        StartSessionPreferencesFragment startSessionPreferencesFragment = StartSessionPreferencesFragment.newInstance(studySessionId);
        ((HomescreenActivity)getActivity()).replaceFragment(R.id.homescreen, startSessionPreferencesFragment);
    }

    public void updateDraftStudySession(){
        StudySession draftStudySession = ((HomescreenActivity) getActivity()).getDraftStudySession();

        Point[] tileCoordinates = ((HomescreenActivity) getActivity()).getTileCoordinates(((HomescreenActivity) getActivity()).getSelectedLatLng());

        Log.i(TAG, tileCoordinates[3].toString());

        draftStudySession.setTileCoordinateZoom12(tileCoordinates[0].toString());
        draftStudySession.setTileCoordinateZoom13(tileCoordinates[1].toString());
        draftStudySession.setTileCoordinateZoom14(tileCoordinates[2].toString());
        draftStudySession.setTileCoordinateZoom15(tileCoordinates[3].toString());

        LatLng selectedLatLng = ((HomescreenActivity) getActivity()).getSelectedLatLng();
        ParseGeoPoint selectedGeoPoint = new ParseGeoPoint(selectedLatLng.latitude, selectedLatLng.longitude);
        draftStudySession.setLocation(selectedGeoPoint);
    }

    public void updateCurrentUser(){
        User currentUser = ((HomescreenActivity) getActivity()).getCurrentUser();
        LatLng currentLatLng = ((HomescreenActivity) getActivity()).getCurrentLatLng();
        ParseGeoPoint currentGeoPoint = new ParseGeoPoint(currentLatLng.latitude, currentLatLng.longitude);
        currentUser.setLocation(currentGeoPoint);
    }
}