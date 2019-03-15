package com.example.retrofitdownloadzip.v53;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import rx.downloadlibrary.Downloadable;

public class Content {
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("pos")
    @Expose
    private int pos;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("gpx")
    @Expose
    private String gpx;
    @SerializedName("color")
    @Expose
    private String gpxColor;
    @SerializedName("map_image")
    @Expose
    private String mapImage;
    @SerializedName("picture")
    @Expose
    private String picture;
    @SerializedName("points")
    @Expose
    private List<Point> points;
    @SerializedName("kind")
    @Expose
    private String kind;

    @SerializedName("audio_size")
    @Expose
    private int audioSize;
    @SerializedName("picture_size")
    @Expose
    private int pictureSize;

    @SerializedName("icon_size")
    @Expose
    private int iconSize;

    private String packageDirPath;

    public Content(Content other) {
        this.id = other.id;
        this.points = new ArrayList<>(other.points);
        this.pos = other.pos;
        this.name = other.name;
        this.mapImage = other.mapImage;
        this.picture = other.picture;
        this.kind = other.kind;
        this.packageDirPath = other.packageDirPath;
        this.gpx = other.gpx;
        this.gpxColor = other.gpxColor;
    }

    public List getDownloadables() {
        List<Downloadable> downloadables = new ArrayList<>();
        if (mapImage != null) downloadables.add(new Downloadable(mapImage));
        if (picture != null) downloadables.add(new Downloadable(picture));
        if (points != null) for (Point p : points) downloadables.addAll(p.getDownloadables());
        return downloadables;
    }
}