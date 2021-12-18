package com.example.photoclips.utils;

import android.app.Application;
import android.net.Uri;

import java.util.List;

public class ContactApp extends Application {
    private List<Uri> uris;


    public List<Uri> getUris() {
        return uris;
    }

    public void setUris(List<Uri> uris) {
        this.uris = uris;
    }
}
