package com.example.retrofitdownloadzip.v53;

import android.text.TextUtils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import rx.downloadlibrary.Downloadable;

public class PopMapDetails {
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("uid")
    @Expose
    private String uid;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("lat")
    @Expose
    private String lat;
    @SerializedName("lng")
    @Expose
    private String lng;
    @SerializedName("header")
    @Expose
    private Header header;
    @SerializedName("location")
    @Expose
    private Location location;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("transport_map")
    @Expose
    private String transportMap;
    @SerializedName("show_point_numbers")
    @Expose
    private boolean showPointNumbers;
    @SerializedName("content")
    @Expose
    private List<Content> contentList;


    private List<Downloadable> downloadables;

    public List<Downloadable> getDownloadables() {
        prepareDownloadables();
        return downloadables;
    }

    public void prepareDownloadables() {
        this.downloadables = new ArrayList<>();
        if (header != null) downloadables.addAll(header.getDownloadables());
        if (!TextUtils.isEmpty(transportMap)) downloadables.add(new Downloadable(transportMap));
        for (Content c : contentList) downloadables.addAll(c.getDownloadables());
    }
}
