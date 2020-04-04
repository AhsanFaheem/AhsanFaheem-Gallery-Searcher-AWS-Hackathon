package com.awshackathon.goforpic.data;

import android.net.Uri;

import java.util.ArrayList;

public class GluonOpenCVObjectDetectorData {
    private String endpointUrl;
    private Uri selectedFolderPath ;
    private ArrayList<String> selectedFilters;

    public Uri getSelectedFolderPath() {
        return selectedFolderPath;
    }

    public void setSelectedFolderPath(Uri selectedFolderPath) {
        this.selectedFolderPath = selectedFolderPath;
    }

    public ArrayList<String> getSelectedFilters() {
        return selectedFilters;
    }

    public void setSelectedFilters(ArrayList<String> selectedFilters) {
        this.selectedFilters = selectedFilters;
    }

    public String getEndpointUrl() {
        return endpointUrl;
    }

    public void setEndpointUrl(String endpointUrl) {
        this.endpointUrl = endpointUrl;
    }
}
