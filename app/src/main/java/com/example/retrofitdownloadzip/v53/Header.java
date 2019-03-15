package com.example.retrofitdownloadzip.v53;

import android.text.TextUtils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import rx.downloadlibrary.Downloadable;

public class Header {

    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("picture")
    @Expose
    private String picture;
    @SerializedName("audio")
    @Expose
    private String audio;

    @SerializedName("audio_size")
    @Expose
    private int audioSize;
    @SerializedName("picture_size")
    @Expose
    private int pictureSize;


    public List getDownloadables() {
        List<Downloadable> downloadables = new ArrayList<>();
        if (!TextUtils.isEmpty(picture)) downloadables.add(new Downloadable(picture));
        if (!TextUtils.isEmpty(audio)) downloadables.add(new Downloadable(audio));
        return downloadables;
    }
}