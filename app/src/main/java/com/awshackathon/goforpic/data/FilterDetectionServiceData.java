package com.awshackathon.goforpic.data;

import android.net.Uri;

import com.awshackathon.goforpic.domain.Filters;

import java.util.ArrayList;
import java.util.List;

public class FilterDetectionServiceData {
    private String objectDetectorEndpoint;
    private String emotionDetectorEndpoint;
    private String documentReaderEndpoint;
    private Uri selectedFolderPath ;
    private Filters selectedFilters;

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

    public Uri getSelectedFolderPath() {
        return selectedFolderPath;
    }

    public void setSelectedFolderPath(Uri selectedFolderPath) {
        this.selectedFolderPath = selectedFolderPath;
    }

    public Filters getSelectedFilters() {
        return selectedFilters;
    }

    public void setSelectedFilters(Filters selectedFilters) {
        this.selectedFilters = selectedFilters;
    }

    public String getEmotionDetectorEndpoint() {
        return emotionDetectorEndpoint;
    }

    public void setEmotionDetectorEndpoint(String emotionDetectorEndpoint) {
        this.emotionDetectorEndpoint = emotionDetectorEndpoint;
    }
}
