package com.awshackathon.goforpic.domain;

import java.io.Serializable;
import java.util.ArrayList;

public class Filters implements Serializable {
    private String textFilter;
    private ArrayList<String> objectsFiltersList;
    private ArrayList<String> emotionsFiltersList;

    public String getTextFilter() {
        return textFilter;
    }

    public void setTextFilter(String textFilter) {
        this.textFilter = textFilter;
    }

    public ArrayList<String> getObjectsFiltersList() {
        return objectsFiltersList;
    }

    public void setObjectsFiltersList(ArrayList<String> objectsFiltersList) {
        this.objectsFiltersList = objectsFiltersList;
    }

    public ArrayList<String> getEmotionsFiltersList() {
        return emotionsFiltersList;
    }

    public void setEmotionsFiltersList(ArrayList<String> emotionsFiltersList) {
        this.emotionsFiltersList = emotionsFiltersList;
    }
}
