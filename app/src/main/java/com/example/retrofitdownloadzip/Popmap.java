package com.example.retrofitdownloadzip;

import android.util.Pair;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import rx.downloadlibrary.Downloadable;

public class Popmap {

    @SerializedName("secret_token")
    String secretToken;
    @SerializedName("installation_id")
    String installId;
    @SerializedName("os_version")
    String osVersion;
    @SerializedName("app_version")
    String appVersion;
    @SerializedName("pop_map_id")
    String pop_map_id;
    @SerializedName("language_id")
    String language_id;

    private List<Downloadable> downloadables;

    public void setInstallId(String installId) {
        this.installId = installId;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public void setSecretToken(String secretToken) {
        this.secretToken = secretToken;
    }

    public void setLanguage_id(String language_id) {
        this.language_id = language_id;
    }

    public void setPop_map_id(String pop_map_id) {
        this.pop_map_id = pop_map_id;
    }

    public void setAppVersion(String i) {
        this.appVersion = i;
    }


    Pair getProgress() {
        long p = 0;
        long s = 0;
        if (downloadables != null)
            for (Downloadable d : downloadables) {
                p = p + d.getByteRead();
                s = s + d.getContentLength();
            }
        return new Pair(p, s);
    }

    public void setDownloadables(List<Downloadable> downloadables) {
        this.downloadables = downloadables;
    }

    public void updateProgress(Downloadable d) {
        if (downloadables != null && downloadables.contains(d)) {
            int index = downloadables.indexOf(d);
            if (index > 0) downloadables.set(index, d);
        }
    }

    public boolean contains(Downloadable d) {
        return downloadables != null && downloadables.contains(d);
    }
}