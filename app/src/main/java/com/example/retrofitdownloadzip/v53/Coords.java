package com.example.retrofitdownloadzip.v53;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Coords implements Serializable {
    @SerializedName("lat")
    @Expose
    private double lat;
    @SerializedName("lng")
    @Expose
    private double lng;

    public Coords(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }
}