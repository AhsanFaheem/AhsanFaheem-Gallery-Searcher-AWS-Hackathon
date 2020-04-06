package com.awshackathon.goforpic.service;

import android.content.Context;
import android.graphics.Bitmap;
import android.provider.MediaStore;

import com.amazonaws.services.textract.AmazonTextract;
import com.amazonaws.services.textract.AmazonTextractClient;
import com.amazonaws.services.textract.model.DetectDocumentTextRequest;
import com.amazonaws.services.textract.model.Document;
import com.android.volley.RequestQueue;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;



public class DocumentSearchService {
    Context context;
    RequestQueue requestQueue;
    public void startProcessing(){
/*
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), file.getUri());
        ByteArrayOutputStream stream=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,70,stream);
        byte[] imgBytesArray = stream.toByteArray();
        DetectDocumentTextRequest detectDocumentTextRequest1=new DetectDocumentTextRequest();
        detectDocumentTextRequest1.withDocument(new Document().withBytes(ByteBuffer.wrap(imgBytesArray)));
        AmazonTextractClient am=new AmazonTextractClient();

        am.detectDocumentText(detectDocumentTextRequest1);
        */

    //    DetectDocumentTextRequest detectDocumentTextRequest= DetectDocumentTextRequest.builder().document(Document.builder().bytes(SdkBytes.fromByteArray(imgBytesArray)).build()).build();


/*
        TextractAsyncClient cli=new TextractAsyncClientBuilder().build();

        TextractClient client=new TextractClientBuilder().build();
        client.detectDocumentText(detectDocumentTextRequest);
*/
        //TextractClient client=  new TextractClientBuilder().region(Region.US_EAST_1).build();

    }
}
