package com.awshackathon.goforpic.service;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;

import androidx.documentfile.provider.DocumentFile;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.awshackathon.goforpic.data.FilterDetectionServiceData;
import com.awshackathon.goforpic.domain.ImageForProcessing;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

public class FiltersDetectionService {
    Context context;
    ProcessedImagedReadWriteService savedImagesRWService;
    ResponseParserService responseParserService;
    AtomicInteger atomicInteger = new AtomicInteger();
    Map<Integer, Uri> imagesForProcessing = new HashMap<Integer, Uri>();

    int totalImages, processedImages = 0;

    ArrayList<Uri> alreadyMatchedImgs = new ArrayList<>();

    private ArrayList<String> objectFilters;
    private ArrayList<String> emotionFilters;
    private ArrayList<String> textFilters = new ArrayList<>();

    RequestQueue requestQueue;
    Queue<Uri> matchedImagesUriQueue;

    public void startProcessing(FilterDetectionServiceData filterDetectionServiceData) {
        resetFields();
        if (filterDetectionServiceData.getSelectedFilters().getObjectsFiltersList() != null)
            objectFilters = filterDetectionServiceData.getSelectedFilters().getObjectsFiltersList();
        if (filterDetectionServiceData.getSelectedFilters().getTextFilter() != null )
            textFilters = filterDetectionServiceData.getSelectedFilters().getTextFilter();
        if (filterDetectionServiceData.getSelectedFilters().getEmotionsFiltersList() != null)
            emotionFilters = filterDetectionServiceData.getSelectedFilters().getEmotionsFiltersList();
        addUnprocessedImgsToReqQueue(filterDetectionServiceData);
    }

    private void resetFields() {
        totalImages = 0;
        processedImages = 0;
        imagesForProcessing = new HashMap<Integer, Uri>();
        matchedImagesUriQueue = new PriorityQueue<>();
    }

    private void addUnprocessedImgsToReqQueue(FilterDetectionServiceData filterDetectionServiceData) {
      //  savedImagesRWService.resetPreferences();
        DocumentFile documentFile = DocumentFile.fromTreeUri(context, filterDetectionServiceData.getSelectedFolderPath());
        savedImagesRWService.load();
        for (DocumentFile file : documentFile.listFiles()) {
            Log.i("Processing File ", file.getUri().getPath());
            boolean textFilterFailed = false, objectFilterFailed = false, emotionFilterFailed = false;
            try {
                if (textFilters != null && savedImagesRWService.ifImageExistWithTextFilter(file.getUri().getPath())) {
                    if (savedImagesRWService.isCriteriaMatchesForTextFilter(file.getUri().getPath(), textFilters)) {
                        matchedImagesUriQueue.add(file.getUri());
                        continue;
                    }
                    textFilterFailed = true;
                }
                if (objectFilters != null && savedImagesRWService.ifImageExistWithObjectFilter(file.getUri().getPath())) {
                    if (savedImagesRWService.isCriteriaMatchesForTagsFilter(file.getUri().getPath(), objectFilters)) {
                        matchedImagesUriQueue.add(file.getUri());
                        continue;
                    }
                    objectFilterFailed = true;
                }
                if (emotionFilters != null && savedImagesRWService.ifImageExistWithEmotionFilter(file.getUri().getPath())) {
                    if (savedImagesRWService.isCriteriaMatchesForEmotionsFilter(file.getUri().getPath(), emotionFilters)) {
                        matchedImagesUriQueue.add(file.getUri());
                        continue;
                    }
                    emotionFilterFailed = true;
                }
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), file.getUri());
                //  bitmap=Bitmap.createScaledBitmap(bitmap,100,100,true);
                String base64Encoded = getBase64ImageString(bitmap);
                Log.i("base64","Prepared base64 image for "+file.getUri().getPath());
                int imgReference = atomicInteger.getAndIncrement();
                imagesForProcessing.put(imgReference, file.getUri());
                final String requestPayload = prepareRequestPayload(base64Encoded, imgReference);

                if (objectFilters != null && !objectFilterFailed) {
                    StringRequest stringRequest = getObjectDetectionRequest(filterDetectionServiceData.getObjectDetectorEndpoint(), requestPayload);
                    requestQueue.add(stringRequest);
                    totalImages++;
                }
                if (textFilters != null && !textFilterFailed) {
                    StringRequest stringRequest = getDocumentReadRequest(filterDetectionServiceData.getDocumentReaderEndpoint(), requestPayload);
                    requestQueue.add(stringRequest);
                    totalImages++;
                }
                if (emotionFilters != null && !emotionFilterFailed) {
                    StringRequest stringRequest = getEmotionDetectionRequest(filterDetectionServiceData.getEmotionDetectorEndpoint(), requestPayload);
                    requestQueue.add(stringRequest);
                    totalImages++;


                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isProcessingCompleted() {
        return totalImages == processedImages;
    }

    public ArrayList<Uri> getMatchedImages() {
        ArrayList<Uri> listOfImages = new ArrayList<>();
        while (!matchedImagesUriQueue.isEmpty())
            listOfImages.add(matchedImagesUriQueue.poll());
        return listOfImages;
    }

    private StringRequest getObjectDetectionRequest(final String url, final String requestPayload) {

        return new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("Response is: ", response.toString());
                        try {
                            Uri matchedResultPath = responseParserService.parseObjectDetectorResponse(response, imagesForProcessing, objectFilters);
                            {
                                if (matchedResultPath != null && !alreadyMatchedImgs.contains(matchedResultPath)) {
                                    matchedImagesUriQueue.add(matchedResultPath);
                                    alreadyMatchedImgs.add(matchedResultPath);
                                }
                                processedImages++;

                            }
                        } catch (Exception e) {
                            processedImages++;
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("volley error", error.toString());
                    }
                }) {
            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return requestPayload.getBytes("utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return super.getBody();
            }
        };
    }

    private StringRequest getEmotionDetectionRequest(final String url, final String requestPayload) {

        return new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("Response is: ", response.toString());
                        try {
                            Uri matchedResultPath = responseParserService.parseEmotionDetectorResponse(response, imagesForProcessing, emotionFilters);
                            {
                                if (matchedResultPath != null && !alreadyMatchedImgs.contains(matchedResultPath)) {
                                    matchedImagesUriQueue.add(matchedResultPath);
                                    alreadyMatchedImgs.add(matchedResultPath);
                                }
                                processedImages++;
                            }
                        } catch (Exception e) {
                            processedImages++;
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("volley error", error.toString());
                    }
                }) {
            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return requestPayload.getBytes("utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return super.getBody();
            }
        };
    }

    private StringRequest getDocumentReadRequest(final String url, final String requestPayload) {
        return new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("Response is: ", response.toString());
                        try {
                            Uri matchedResultPath = responseParserService.parseTextReaderResponse(response, imagesForProcessing, textFilters);
                            if (matchedResultPath != null && !alreadyMatchedImgs.contains(matchedResultPath)) {
                                matchedImagesUriQueue.add(matchedResultPath);
                                alreadyMatchedImgs.add(matchedResultPath);
                            }
                            processedImages++;
                        } catch (Exception e) {
                            processedImages++;
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("volley error", error.toString());
                    }
                }) {
            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return requestPayload.getBytes("utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return super.getBody();
            }
        };
    }

    private String prepareRequestPayload(String base64Encoded, int imgReference) throws JSONException {
        JSONObject requestData = new JSONObject();
        requestData.put("data", base64Encoded);
        requestData.put("ref", imgReference);
        return requestData.toString();
    }

    private String getBase64ImageString(Bitmap bitmap) {
        Log.i("base64","Going to prepare base 64");
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, stream);
        byte[] byteArray = stream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    public FiltersDetectionService(Context context) {
        this.context = context;
        requestQueue = Volley.newRequestQueue(this.context);
        savedImagesRWService = new ProcessedImagedReadWriteService(context);
        responseParserService = new ResponseParserService(savedImagesRWService,this.context);
    }

}
