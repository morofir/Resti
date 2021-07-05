package com.example.Resti;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

public class MarkerObject implements Serializable {
    private String address;
    private String title;
    private String free;
    private LatLng latLng;

    public MarkerObject( LatLng latLng) {
        this.latLng = latLng;
    }

    public MarkerObject(String address, String title, String free, LatLng latLng) {
        this.address = address;
        this.title = title;
        this.free = free;
        this.latLng = latLng;
    }

    public MarkerObject() {

    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFree() {
        return free;
    }

    public void setFree(String free) {
        this.free = free;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }



}
