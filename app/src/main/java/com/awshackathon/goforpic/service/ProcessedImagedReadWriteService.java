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
    private Map<Uri, ImageForProcessing> processedImages = new HashMap<>();
    private GsonBuilder gsonBuilder;
    private Gson gson;
    private Context context;

    SharedPreferences sharedPreferences;

    public void load() {

        String jsonValue = null;
        Map<Uri, ImageForProcessing> imageForProcessing = gson.fromJson(jsonValue, (Map.class));
        processedImages.putAll(imageForProcessing);
    }

    public boolean ifImageExist(Uri uri) {
        return processedImages.containsKey(uri);
    }

    public boolean isCriteriaMatchesForTagsFilter(Uri uri, ArrayList<String> filters) {
        ImageForProcessing imageForProcessing = processedImages.get(uri);
        for (int i = 0; i < imageForProcessing.getListOfIds().size(); i++) {
            for (int j = 0; j < filters.size(); j++) {
                if (imageForProcessing.getListOfIds().get(i).equalsIgnoreCase(filters.get(j)))
                    return true;
            }
        }
        return false;
    }
    public void saveImage(Map<Uri, ImageForProcessing> toSave) {

        processedImages.putAll(toSave);
        String imagesInfoJson = gson.toJson(processedImages);
        editor.putString("processedImages", imagesInfoJson);
        editor.commit();
    }
    public void saveImage(Uri uri, ImageForProcessing toSave) {
        ImageForProcessing img;
        if(processedImages.containsKey(uri)){
           img = processedImages.get(uri);
            if(toSave.getListOfIds()!=null)
                img.setListOfIds(toSave.getListOfIds());
            if(toSave.getHumanEmotions()!=null)
                img.setHumanEmotions(toSave.getHumanEmotions());
            if(toSave.getText()!=null)
                img.setText(toSave.getText());
        }
        else{
            img=toSave;
        }
        processedImages.put(uri,img);
        String imagesInfoJson = gson.toJson(processedImages);
        editor.putString("processedImages", imagesInfoJson);
        editor.commit();
    }
    /*public void saveImage(Uri uri, ArrayList<String> tags) {

        processedImages.put(uri,tags);
        String imagesInfoJson = gson.toJson(processedImages);
        editor.putString("processedImages", imagesInfoJson);


    }*/
    public ProcessedImagedReadWriteService(Context context) {
        this.context = context;
        gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();
        sharedPreferences = context.getSharedPreferences("ProcessedImagesPreference", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        processedImages = new HashMap<>();
    }
}
