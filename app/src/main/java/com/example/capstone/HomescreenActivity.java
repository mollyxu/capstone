package com.example.capstone;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;

import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;


public class HomescreenActivity extends AppCompatActivity
        implements
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback {
    private FirebaseAuth mAuth;
    private FusedLocationProviderClient fusedLocationClient;

    private static final String TAG = "HomescreenActivity";
    private static final int TILE_SIZE = 256;
    private static final int MIN_ZOOM = 12;
    private static final int MAX_ZOOM = 15;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean permissionDenied = false;

    private GoogleMap map;

    private StudySession draftStudySession;
    protected List<StudySession> allJoinedStudySessions = new ArrayList<>();

    private Location currentLocation;
    private LatLng currentLatLng;
    private LatLng selectedLatLng;

    public MapGestureHandler mapGestureHandler;

    public double currentZoom = -1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homescreen);

        mAuth = FirebaseAuth.getInstance();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.homescreen, HomeFragment.class, null)
                    .commit();
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    public void replaceFragment(@IdRes int containerViewId, @NonNull Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(containerViewId, fragment)
                .addToBackStack(null)
                .commit();
        Log.e(TAG, "fragment replaced");
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        map.setOnMyLocationButtonClickListener(this);
        map.setOnMyLocationClickListener(this);
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                map.clear();
                map.addMarker(new MarkerOptions().position(point));
                selectedLatLng = point;
            }
        });

        map.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener(){
            @Override
            public void onCameraMove() {
                CameraPosition cameraPosition = googleMap.getCameraPosition();
                if (currentZoom == cameraPosition.zoom) {
                    return;
                }
                if (mapGestureHandler == null) {
                    return;
                }

                currentZoom = cameraPosition.zoom;

                if (currentZoom < MIN_ZOOM) {
                    mapGestureHandler.onZoomChange(MIN_ZOOM, currentLatLng);
                }
                if (currentZoom > MAX_ZOOM) {
                    mapGestureHandler.onZoomChange(MAX_ZOOM, currentLatLng);
                }

                mapGestureHandler.onZoomChange((int) currentZoom, currentLatLng);
            }
        });

        enableMyLocation();
    }

    private Boolean isUserGrantedPermission(){
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED);
    }

    @SuppressLint("MissingPermission")
    private void enableMyLocation() {
        if (isUserGrantedPermission()) {
            map.setMyLocationEnabled(true);
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                currentLocation = location;
                                currentLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                                Log.i(TAG, "curr lat: " + currentLatLng);
                            }
                        }
                    });
            return;
        }

        PermissionUtils.requestLocationPermissions(this, LOCATION_PERMISSION_REQUEST_CODE, true);
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION) || PermissionUtils
                .isPermissionGranted(permissions, grantResults,
                        Manifest.permission.ACCESS_COARSE_LOCATION)) {
            enableMyLocation();
        } else {
            permissionDenied = true;
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (permissionDenied) {
            showMissingPermissionError();
            permissionDenied = false;
        }
    }

    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }

    public void prepareMap(SupportMapFragment supportMapFragment) {
        supportMapFragment.getMapAsync(this);
    }

    public StudySession getDraftStudySession() {
        if (draftStudySession == null) {
            FirebaseUser user = mAuth.getCurrentUser();
            String firebase_uid = user.getUid();
            draftStudySession = new StudySession();
            draftStudySession.setOrganizerId(firebase_uid);
        }
        return draftStudySession;
    }

    private ParseQuery<User> getUpdateUserParseQuery(){
        FirebaseUser user = mAuth.getCurrentUser();
        String firebase_uid = user.getUid();
        ParseQuery<User> query = ParseQuery.getQuery(User.class);
        query.whereEqualTo(User.KEY_FIREBASE_UID, firebase_uid);
        return query;
    }

    private void updateUserWithJoinedStudySessionAsync(String studySessionId){
        ParseQuery<User> query = getUpdateUserParseQuery();
        query.getFirstInBackground(new GetCallback<User>() {
            public void done(User user, ParseException e) {
                if (e == null) {
                    user.add("joined_sessions",studySessionId);
                    user.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "Failed to Save", Toast.LENGTH_SHORT).show();
                                Log.d(getClass().getSimpleName(), "User update error: " + e);
                            }
                        }
                    });
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    private void updateUserJoinedStudySession(String studySessionId){
        getUpdateUserParseQuery();
        updateUserWithJoinedStudySessionAsync(studySessionId);
    }

    public void saveDraftStudySessionAndUser(NavigationProvider navigationProvider) {
        draftStudySession.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error while saving", e);
                    Toast.makeText(getApplicationContext(), "Error while saving!", Toast.LENGTH_SHORT).show();
                    return;
                }

                updateUserJoinedStudySession(draftStudySession.getObjectId());

                draftStudySession = null;
                navigationProvider.navigate();
            }
        });
    }

    public Point getTileCoordinate(int zoomLevel, LatLng latLng){
        LatLng worldCoordinate = getMapProjection(latLng);
        Log.i(TAG, "tile coordinate: " + worldCoordinateToTileCoordinate(worldCoordinate, zoomLevel));
        return worldCoordinateToTileCoordinate(worldCoordinate, zoomLevel);
    }

    public Point[] getTileCoordinates(LatLng latLng){
        Point[] tileCoordinates = new Point[4];
        for (int i = 0; i < 4; i++) {
            tileCoordinates[i] = getTileCoordinate(i + MIN_ZOOM, latLng);
        }
        return tileCoordinates;
    }

    private Point worldCoordinateToTileCoordinate(LatLng worldCoordinate, int zoom){
        int scale = 1 << zoom;
        return new Point((int)Math.floor((worldCoordinate.latitude * scale) / TILE_SIZE),
                (int)Math.floor((worldCoordinate.longitude * scale) / TILE_SIZE));
    }

    private LatLng getMapProjection(LatLng latLng){
        double siny = Math.sin((latLng.latitude * Math.PI) / 180);

        siny = Math.min(Math.max(siny, -0.9999), 0.9999);

        return new LatLng(
                TILE_SIZE * (0.5 + latLng.longitude / 360),
                TILE_SIZE * (0.5 - Math.log((1 + siny) / (1 - siny)) / (4 * Math.PI))
        );
    }

    public List<StudySession> getAllJoinedStudySessions() {
        return allJoinedStudySessions;
    }

    public int getZoomLevel(){
        return (int) map.getCameraPosition().zoom;
    }

    public LatLng getCurrentLatLng() {
        return currentLatLng;
    }

    public LatLng getSelectedLatLng() {
        return selectedLatLng;
    }


}