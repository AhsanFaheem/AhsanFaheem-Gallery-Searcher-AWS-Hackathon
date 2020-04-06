package com.awshackathon.goforpic.data;

import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

public class GluonOpenCVObjectDetectorData {
    private String objectDetectorEndpoint;
    private String documentReaderEndpoint;
    private Uri selectedFolderPath ;
    private ArrayList<String> selectedFilters;
    private ArrayList<String> selectedHumanFilters;
    private List<String> selectedTextFilters;

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

    public String getObjectDetectorEndpoint() {
        return objectDetectorEndpoint;
    }

    public void setObjectDetectorEndpoint(String objectDetectorEndpoint) {
        this.objectDetectorEndpoint = objectDetectorEndpoint;
    }

    public String getDocumentReaderEndpoint() {
        return documentReaderEndpoint;
    }

    public void setDocumentReaderEndpoint(String documentReaderEndpoint) {
        this.documentReaderEndpoint = documentReaderEndpoint;
    }

    public ArrayList<String> getSelectedHumanFilters() {
        return selectedHumanFilters;
    }

    public void setSelectedHumanFilters(ArrayList<String> selectedHumanFilters) {
        this.selectedHumanFilters = selectedHumanFilters;
    }

    public List<String> getSelectedTextFilters() {
        return selectedTextFilters;
    }

    public void setSelectedTextFilters(List<String> selectedTextFilters) {
        this.selectedTextFilters = selectedTextFilters;
    }
}
