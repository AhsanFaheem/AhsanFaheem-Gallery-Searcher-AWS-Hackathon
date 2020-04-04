package com.awshackathon.goforpic.domain;

import android.net.Uri;

import java.util.ArrayList;

public class ImageForProcessing {
    Uri imageUri;
    ArrayList<String> listOfIds;

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }

    public ArrayList<String> getListOfIds() {
        return listOfIds;
    }

    public void setListOfIds(ArrayList<String> listOfIds) {
        this.listOfIds = listOfIds;
    }
}
