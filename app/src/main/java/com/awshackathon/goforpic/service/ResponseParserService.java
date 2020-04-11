package com.awshackathon.goforpic.service;

import android.net.Uri;

import com.awshackathon.goforpic.domain.ImageForProcessing;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

public class ResponseParserService {
    ProcessedImagedReadWriteService savedImagesRWService;

    public Uri parseObjectDetectorResponse(String response, Map<Integer, Uri> imagesForProcessing, ArrayList<String> objectFilters) throws JSONException {
        JSONObject data = new JSONObject(response);
        JSONArray idsJsonArray = data.getJSONArray("result");
        ArrayList listOfTags = new ArrayList();
        Uri path = (imagesForProcessing.get(Integer.parseInt(data.getString("ref"))));
        boolean matched = false;
        for (int i = 0; i < idsJsonArray.length(); i++) {
            String id = idsJsonArray.getJSONObject(i).getString("id");
            listOfTags.add(id);
            for (int j = 0; j < objectFilters.size(); j++) {
                if (id.equalsIgnoreCase(objectFilters.get(j))) {
                    matched = true;
                    break;
                }
            }
        }
        ImageForProcessing imageForProcessing = new ImageForProcessing();
        imageForProcessing.setImageUri(path.getPath());
        imageForProcessing.setListOfIds(listOfTags);

        savedImagesRWService.saveImage(path.getPath(), imageForProcessing);
        if (matched) return path;
        return null;
    }

    public Uri parseEmotionDetectorResponse(String response, Map<Integer, Uri> imagesForProcessing, ArrayList<String> emotionFilters) throws JSONException {
        JSONObject data = new JSONObject(response);
        JSONArray idsJsonArray = data.getJSONArray("result");
        ArrayList listOfEmotions = new ArrayList();
        boolean matched = false;

        Uri path = (imagesForProcessing.get(Integer.parseInt(data.getString("ref"))));
        for (int i = 0; i < idsJsonArray.length(); i++) {
            String id = idsJsonArray.getJSONObject(i).getString("emotion");
            listOfEmotions.add(id);
            for (int j = 0; j < emotionFilters.size(); j++) {
                if (id.equalsIgnoreCase(emotionFilters.get(j))) {
                    matched = true;
                    break;
                }
            }
        }
        ImageForProcessing imageForProcessing = new ImageForProcessing();
        imageForProcessing.setImageUri(path.getPath());
        imageForProcessing.setHumanEmotions(listOfEmotions);
        savedImagesRWService.saveImage(path.getPath(), imageForProcessing);
        if (matched) return path;
        return null;

    }

    public Uri parseTextReaderResponse(String response, Map<Integer, Uri> imagesForProcessing, ArrayList<String> textFilters) throws JSONException {
        JSONObject data = new JSONObject(response);
        String textInResponse = data.getString("result");
        Uri path = (imagesForProcessing.get(Integer.parseInt(data.getString("ref"))));
        boolean matched = false;

        for (int j = 0; j < textFilters.size(); j++) {
            if (textInResponse.length() >= textFilters.get(j).length()) {
                if (textInResponse.toLowerCase().contains(textFilters.get(j).toLowerCase())) {
                    matched = true;
                    break;
                }
            } else {
                if (textFilters.get(j).toLowerCase().contains(textInResponse.toLowerCase())) {
                    matched = true;
                    break;
                }
            }
        }
        ImageForProcessing imageForProcessing = new ImageForProcessing();
        imageForProcessing.setText(textInResponse);
        savedImagesRWService.saveImage(path.getPath(), imageForProcessing);
        if (matched) return path;
        return null;
    }

    public ResponseParserService(ProcessedImagedReadWriteService savedImagesRWService) {
        this.savedImagesRWService = savedImagesRWService;
    }
}