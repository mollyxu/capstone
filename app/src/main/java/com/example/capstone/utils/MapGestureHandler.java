package com.example.capstone.utils;

import com.google.android.gms.maps.model.LatLng;

public interface MapGestureHandler {
    public void onZoomChange(int zoomLevel, LatLng currentLatLng);
}
