package com.example.retrofitdownloadzip.v53;

import android.text.TextUtils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import rx.downloadlibrary.Downloadable;

public class Point {

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("linked_area_id")
    @Expose
    private Integer linkedAreaId;
    @SerializedName("num")
    @Expose
    private Integer num;
    @SerializedName("picture")
    @Expose
    private String picture;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("audio")
    @Expose
    private String audio;

    @SerializedName("coords")
    @Expose
    private Coords coords;

    @SerializedName("icon")
    @Expose
    private String icon;
    @SerializedName("icon_brandable")
    @Expose
    private boolean brandableIcon;

    @SerializedName("radius")
    @Expose
    private int radius;

    @SerializedName("audio_size")
    @Expose
    private int audioSize;
    @SerializedName("picture_size")
    @Expose
    private int pictureSize;

    @SerializedName("icon_size")
    @Expose
    private int iconSize;


    public List getDownloadables() {
        List<Downloadable> downloadables = new ArrayList<>();
        if (!TextUtils.isEmpty(picture)) downloadables.add(new Downloadable(picture));
        if (!TextUtils.isEmpty(audio)) downloadables.add(new Downloadable(audio));
        if (!TextUtils.isEmpty(icon)) downloadables.add(new Downloadable(icon));
        return downloadables;
    }

}