package com.awshackathon.goforpic.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

import com.awshackathon.goforpic.domain.ImageForProcessing;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProcessedImagedReadWriteService {
    private final SharedPreferences.Editor editor;
    private Map<Uri, ArrayList<String>> processedImages = new HashMap<>();
    private GsonBuilder gsonBuilder;
    private Gson gson;
    private Context context;

    SharedPreferences sharedPreferences;

    public void load() {

        String jsonValue = null;
        Map<Uri, ArrayList<String>> imageForProcessing = gson.fromJson(jsonValue, (Map.class));
        processedImages.putAll(imageForProcessing);
    }

    public boolean ifImageExist(Uri uri) {
        return processedImages.containsKey(uri);
    }

    public boolean isCriteriaMatches(Uri uri, ArrayList<String> filters) {
        ArrayList<String> tags = processedImages.get(uri);
        for (int i = 0; i < tags.size(); i++) {
            for (int j = 0; j < filters.size(); j++) {
                if (tags.get(i).equalsIgnoreCase(filters.get(j)))
                    return true;
            }
        }
        return false;
    }

    public void saveImages(Map<Uri, ArrayList<String>> toSave) {

        processedImages.putAll(toSave);
        String imagesInfoJson = gson.toJson(processedImages);
        editor.putString("processedImages", imagesInfoJson);

    }
    public void saveImage(Uri uri, ArrayList<String> tags) {

        processedImages.put(uri,tags);
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
}
