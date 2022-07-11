package com.example.capstone;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.parse.ParseException;
import com.parse.SaveCallback;

public class StartSessionMapFragment extends Fragment {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;



    private FirebaseAuth mAuth;

    private static final String TAG = "StartSessionMapFragment";

    private Button btnStartPreferences;

    public StartSessionMapFragment() {}

    public static StartSessionMapFragment newInstance() {
        StartSessionMapFragment fragment = new StartSessionMapFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_start_session_map, container, false);

        // Initialize map fragment
        SupportMapFragment supportMapFragment=(SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.google_map);

        // Async map
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                // When map is loaded
                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        // When clicked on map
                        // Initialize marker options
                        MarkerOptions markerOptions=new MarkerOptions();
                        // Set position of marker
                        markerOptions.position(latLng);
                        // Set title of marker
                        markerOptions.title(latLng.latitude+" : "+latLng.longitude);
                        // Remove all marker
                        googleMap.clear();
                        // Animating to zoom the marker
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));
                        // Add marker on map
                        googleMap.addMarker(markerOptions);
                    }
                });

            }
        });
        // Return view
        return view;


    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        btnStartPreferences = getActivity().findViewById(R.id.btn_start_preferences);
        btnStartPreferences.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick confirm button");
                updateDatabase();
                ((HomescreenActivity)getActivity()).replaceFragment(R.id.homescreen, StartSessionPreferencesFragment.class);
            }
        });
    }


    public void updateDatabase(){
//        FirebaseUser user = mAuth.getCurrentUser();
//        String firebase_uid = user.getUid();
//
//        // update parse db
//        StudySession studySession = new StudySession();
//        studySession.setOrganizerId(firebase_uid);
//
//        studySession.saveInBackground(new SaveCallback() {
//            @Override
//            public void done(ParseException e) {
//                if (e != null){
//                    Log.e(TAG, "Error while saving", e);
//                    Toast.makeText(getActivity(), "Error while saving!", Toast.LENGTH_SHORT).show();
//                }
//                Log.i(TAG, "New study session was created successfully!");
//            }
//        });
    }
}