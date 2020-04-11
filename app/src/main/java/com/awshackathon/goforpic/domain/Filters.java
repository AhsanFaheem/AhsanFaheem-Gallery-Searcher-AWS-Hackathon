package com.awshackathon.goforpic.domain;

import java.io.Serializable;
import java.util.ArrayList;

public class Filters implements Serializable {
    private ArrayList<String> textFilterList;
    private ArrayList<String> objectsFiltersList;
    private ArrayList<String> emotionsFiltersList;

    public ArrayList<String> getTextFilter() {
        if (textFilterList != null && !textFilterList.isEmpty())
            return textFilterList;
        return null;
    }

    public void setTextFilterList(ArrayList<String> textFilterList) {
        this.textFilterList = textFilterList;
    }

    public ArrayList<String> getObjectsFiltersList() {
        if (objectsFiltersList != null && !objectsFiltersList.isEmpty())
            return objectsFiltersList;
        return null;
    }

    public void setObjectsFiltersList(ArrayList<String> objectsFiltersList) {
        this.objectsFiltersList = objectsFiltersList;
    }

    public ArrayList<String> getEmotionsFiltersList() {
        if (emotionsFiltersList != null && !emotionsFiltersList.isEmpty())
            return emotionsFiltersList;
        return null;
    }

    public void setEmotionsFiltersList(ArrayList<String> emotionsFiltersList) {
        this.emotionsFiltersList = emotionsFiltersList;
    }
}
