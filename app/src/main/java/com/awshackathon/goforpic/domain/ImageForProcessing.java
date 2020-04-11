package com.awshackathon.goforpic.domain;

import android.net.Uri;

import java.nio.file.Path;
import java.util.ArrayList;

public class ImageForProcessing {
    private String imageUri;
    private ArrayList<String> listOfIds;
    private ArrayList<String> humanEmotions;
    private String text;

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public ArrayList<String> getListOfIds() {
        return listOfIds;
    }

    public void setListOfIds(ArrayList<String> listOfIds) {
        this.listOfIds = listOfIds;
    }

    public ArrayList<String> getHumanEmotions() {
        return humanEmotions;
    }

    public void setHumanEmotions(ArrayList<String> humanEmotions) {
        this.humanEmotions = humanEmotions;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
