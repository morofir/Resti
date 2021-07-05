package com.example.Resti;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

public class MarkerRestObject implements Serializable {
    private String address;
    private String name;
    private String restType;
    private String hours;



    public String getRestType() {
        return restType;
    }

    public void setRestType(String restType) {
        this.restType = restType;
    }

    public String getHours() {
        return hours;
    }

    public void setHours(String hours) {
        this.hours = hours;
    }

    public String getPriceRange() {
        return priceRange;
    }

    public void setPriceRange(String priceRange) {
        this.priceRange = priceRange;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    private String priceRange;
    private String phoneNumber;



    public MarkerRestObject() {
    }

    public MarkerRestObject(String address, String name, String free, LatLng latLng) {
        this.address = address;
        this.name = name;
        this.free = free;
        this.latLng = latLng;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    private String free;
    private LatLng latLng;

}
