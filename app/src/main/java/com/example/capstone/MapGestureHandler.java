package com.example.capstone;

import com.google.android.gms.maps.model.LatLng;

public interface MapGestureHandler {
    public void onZoomChange(int zoomLevel, LatLng currentLatLng);
}
