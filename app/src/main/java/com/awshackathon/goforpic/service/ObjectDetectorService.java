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
import com.awshackathon.goforpic.data.GluonOpenCVObjectDetectorData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

public class ObjectDetectorService {
    Context context;

    AtomicInteger atomicInteger = new AtomicInteger();
    Queue<Uri> matchedImagesUriQueue;
    Map<Integer, Uri> imagesForProcessing = new HashMap<Integer, Uri>();
    RequestQueue requestQueue;
    int totalImages, processedImages = 0;

    private ArrayList<String> filters = new ArrayList<>();

    ProcessedImagedReadWriteService savedImagesRWService;

    public void startProcessing(GluonOpenCVObjectDetectorData gluonOpenCVObjectDetectorData) {
        resetFields();

        if (gluonOpenCVObjectDetectorData.getSelectedFilters() != null)
            filters = gluonOpenCVObjectDetectorData.getSelectedFilters();


        addUnprocessedImgsToReqQueue(gluonOpenCVObjectDetectorData);
    }

    private void resetFields() {
        totalImages = 0;
        processedImages = 0;
        imagesForProcessing = new HashMap<Integer, Uri>();
        matchedImagesUriQueue = new PriorityQueue<>();
    }

    private void addUnprocessedImgsToReqQueue(GluonOpenCVObjectDetectorData gluonOpenCVObjectDetectorData) {
        DocumentFile documentFile = DocumentFile.fromTreeUri(context, gluonOpenCVObjectDetectorData.getSelectedFolderPath());
        for (DocumentFile file : documentFile.listFiles()) {

            Log.i("Processing File ", file.getUri().getPath());
            try {
                if (savedImagesRWService.ifImageExist(file.getUri())) {
                    if (savedImagesRWService.isCriteriaMatches(file.getUri(), filters)) {
                        matchedImagesUriQueue.add(file.getUri());
                    }
                } else {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), file.getUri());
                    //  bitmap=Bitmap.createScaledBitmap(bitmap,100,100,true);
                    String base64Encoded = getBase64ImageString(bitmap);
                    Log.i("base64 encoded", base64Encoded);
                    totalImages++;
                    int imgReference = atomicInteger.getAndIncrement();
                    imagesForProcessing.put(imgReference, file.getUri());
                    final String requestPayload = prepareRequestPayload(base64Encoded, imgReference);
                    StringRequest stringRequest = getStringRequest(gluonOpenCVObjectDetectorData.getEndpointUrl(), requestPayload);
                    requestQueue.add(stringRequest);
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

    private StringRequest getStringRequest(final String url, final String requestPayload) {
        return new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("Response is: ", response.toString());
                        try {
                            processResult(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    private void processResult(String response) throws JSONException {
                        JSONObject data = new JSONObject(response);
                        JSONArray idsJsonArray = data.getJSONArray("result");
                        ArrayList listOfTags = new ArrayList();

                        Uri path = (imagesForProcessing.get(Integer.parseInt(data.getString("ref"))));
                        for (int i = 0; i < idsJsonArray.length(); i++) {
                            String id = idsJsonArray.getJSONObject(i).getString("id");
                            listOfTags.add(id);
                            for (int j = 0; j < filters.size(); j++) {
                                if (id.equalsIgnoreCase(filters.get(j))) {
                                    matchedImagesUriQueue.add(path);
                                    break;
                                }
                            }
                        }
                        savedImagesRWService.saveImage(path, listOfTags);
                        processedImages++;
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
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, stream);
        byte[] byteArray = stream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    public ObjectDetectorService(Context context) {
        this.context = context;
        requestQueue = Volley.newRequestQueue(this.context);
        savedImagesRWService = new ProcessedImagedReadWriteService(context);
    }

}
