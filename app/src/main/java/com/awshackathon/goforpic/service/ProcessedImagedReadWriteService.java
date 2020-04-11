package com.awshackathon.goforpic.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.Image;
import android.net.Uri;
import android.util.Log;

import com.awshackathon.goforpic.domain.ImageForProcessing;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProcessedImagedReadWriteService {
    private final SharedPreferences.Editor editor;
    private Map<String, ImageForProcessing> processedImages = new HashMap<>();
    private GsonBuilder gsonBuilder;
    private Gson gson;
    private Context context;

    SharedPreferences sharedPreferences;

    public void load() {
        String jsonValue = sharedPreferences.getString("processedImages", "");
        Type type = new TypeToken<Map<String, ImageForProcessing>>() {
        }.getType();
        Map<String, ImageForProcessing> imageForProcessing = gson.fromJson(jsonValue, type);
        if (imageForProcessing != null)
            processedImages.putAll(imageForProcessing);
    }

    public boolean ifImageExistWithObjectFilter(String uri) {
        ImageForProcessing val = processedImages.get(uri);
        return val != null && val.getListOfIds() != null;
    }

    public boolean ifImageExistWithTextFilter(String uri) {
        ImageForProcessing val = processedImages.get(uri);
        return val != null && val.getText() != null;
    }

    public boolean ifImageExistWithEmotionFilter(String uri) {
        ImageForProcessing val = processedImages.get(uri);
        return val != null && val.getHumanEmotions() != null;
    }

    public boolean isCriteriaMatchesForTagsFilter(String uri, ArrayList<String> filters) {
        ImageForProcessing imageForProcessing = processedImages.get(uri);
        for (int i = 0; i < imageForProcessing.getListOfIds().size(); i++) {
            for (int j = 0; j < filters.size(); j++) {
                if (imageForProcessing.getListOfIds().get(i).equalsIgnoreCase(filters.get(j)))
                    return true;
            }
        }
        return false;
    }

    public boolean isCriteriaMatchesForEmotionsFilter(String uri, ArrayList<String> humanFilters) {
        ImageForProcessing imageForProcessing = processedImages.get(uri);
        for (int i = 0; i < imageForProcessing.getHumanEmotions().size(); i++) {
            for (int j = 0; j < humanFilters.size(); j++) {
                if (imageForProcessing.getHumanEmotions().get(i).equalsIgnoreCase(humanFilters.get(j)))
                    return true;
            }
        }
        return false;
    }

    public void saveImage(String uri, ImageForProcessing toSave) {
        ImageForProcessing img;
        if (processedImages.containsKey(uri)) {
            img = processedImages.get(uri);
            if (toSave.getListOfIds() != null)
                img.setListOfIds(toSave.getListOfIds());
            if (toSave.getHumanEmotions() != null)
                img.setHumanEmotions(toSave.getHumanEmotions());
            if (toSave.getText() != null)
                img.setText(toSave.getText());
        } else {
            img = toSave;
        }
        processedImages.put(uri, img);
        String imagesInfoJson = gson.toJson(processedImages);
        editor.putString("processedImages", imagesInfoJson);
        editor.commit();
    }

    public ProcessedImagedReadWriteService(Context context) {
        this.context = context;
        gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();
        sharedPreferences = context.getSharedPreferences("ProcessedImagesPreference", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        processedImages = new HashMap<>();
    }

    public void resetPreferences() {
        editor.clear();
        editor.commit();
    }

    public boolean isCriteriaMatchesForTextFilter(String path, ArrayList<String> textFilters) {
        ImageForProcessing imageForProcessing = processedImages.get(path);
        String imgText = imageForProcessing.getText();
        if (imgText == null)
            return false;
        for (int i = 0; i < textFilters.size(); i++) {
            if (imgText.length() >= textFilters.get(i).length()) {
                if (imgText.toLowerCase().contains(textFilters.get(i).toLowerCase())) {
                    return true;
                }
            } else {
                if (textFilters.get(i).toLowerCase().contains(imgText.toLowerCase())) {
                    return true;
                }
            }

        }
        return false;
    }
}
