package com.awshackathon.goforpic;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.awshackathon.goforpic.data.FilterDetectionServiceData;
import com.awshackathon.goforpic.domain.Filters;
import com.awshackathon.goforpic.service.FiltersDetectionService;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {


    ImageView folderSelectedIconImageView;
    ImageView filterSelectedIconImageView;
    Button startButton;
    GridView outputImagesGridView;

    LinearLayout outputLinearLayout;
    TextView processingStatusTextView;
    ProgressBar processingImagesProgressBar;
    final int FOLDER_SELECTED_RESULT = 1;
    final int FILTER_SELECTED_RESULT = 2;
    final int TEXT_FILTER_SELECTED_RESULT = 3;
    Uri selectedFolderPath = null;
    Filters selectedFilters = null;

    FilterDetectionServiceData objectDetectorData;
    FiltersDetectionService objectDetectorService;
    private Timer statusCheckTimer;
    private ArrayList<Uri> matchedImagesUrl;
    private ImageAdapter gridViewAdapter;
    private String documentFilterSelectedText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        folderSelectedIconImageView = findViewById(R.id.folderSelectedIcon);
        filterSelectedIconImageView = findViewById(R.id.filterSelectedIcon);
        startButton = findViewById(R.id.startButton);
        outputLinearLayout = findViewById(R.id.outputLayout);
        processingStatusTextView = findViewById(R.id.processingStatus);
        processingImagesProgressBar = findViewById(R.id.processingLoadbar);
        outputImagesGridView = findViewById(R.id.gridView);
        matchedImagesUrl = new ArrayList<>();
        outputImagesGridView.setAdapter(gridViewAdapter);
        validateIfActiveNetworkConnectionAvailable();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FOLDER_SELECTED_RESULT) {
            if (resultCode == Activity.RESULT_OK) {
                Log.i("selectedFolder", data.getDataString());
                selectedFolderPath = data.getData();
                folderSelectedIconImageView.setImageResource(R.drawable.ic_done);
            }

        } else if (requestCode == FILTER_SELECTED_RESULT) {
            if (resultCode == Activity.RESULT_OK) {
                selectedFilters = (Filters) data.getSerializableExtra("selectedFilters");
                Log.i("selectedFilters", selectedFilters.toString());
                filterSelectedIconImageView.setImageResource(R.drawable.ic_done);
            }
        }
        if (selectedFolderPath != null && selectedFilters != null) {
            startButton.setEnabled(true);
        }
    }

    public void openFolderBrowser(View view) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_OPEN_DOCUMENT_TREE);
        startActivityForResult(intent, FOLDER_SELECTED_RESULT);
    }

    public void openFiltersActivity(View view) {

        Intent intent = new Intent(MainActivity.this, AllFilters.class);
        startActivityForResult(intent, FILTER_SELECTED_RESULT);
    }

    public void postProcessing() {
        ArrayList<Uri> matchedImages = objectDetectorService.getMatchedImages();
        if (!matchedImages.isEmpty()) {
            matchedImagesUrl.addAll(matchedImages);
            gridViewAdapter = new ImageAdapter(MainActivity.this, (ArrayList<Uri>) matchedImagesUrl.clone());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    outputImagesGridView.setAdapter(gridViewAdapter);
                }
            });
        }
        boolean status = objectDetectorService.isProcessingCompleted();
        Log.i("processing-status", String.valueOf(status));
        if (status == true) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    statusCheckTimer.cancel();
                    processingCompletedFrontendChanges();

                }
            });
        }
    }

    public void startStopProcessing(View view) {

        if (startButton.getTag().equals("start")) {
            matchedImagesUrl.clear();
            outputImagesGridView.setAdapter(null);

            startButton.setText("Stop");
            startButton.setTag("stop");
            processingImagesProgressBar.setVisibility(View.VISIBLE);
            outputLinearLayout.setVisibility(View.VISIBLE);

            if (objectDetectorData == null)
                objectDetectorData = new FilterDetectionServiceData();

            objectDetectorData.setSelectedFilters(selectedFilters);
            objectDetectorData.setSelectedFolderPath(selectedFolderPath);
            objectDetectorData.setObjectDetectorEndpoint(getResources().getString(R.string.object_detector_url));
            objectDetectorData.setEmotionDetectorEndpoint(getResources().getString(R.string.emotion_detector_url));
            objectDetectorData.setDocumentReaderEndpoint(getResources().getString(R.string.document_reader_url));
            if (objectDetectorService == null)
                objectDetectorService = new FiltersDetectionService(this);

            objectDetectorService.startProcessing(objectDetectorData);
            statusCheckTimer = new Timer();
            statusCheckTimer.scheduleAtFixedRate(new TimerTask() {

                @Override
                public void run() {
                    postProcessing();
                }
            }, 0, 4000);
        } else {
            doFrontEndChangesIdle();
        }
    }

    private void validateIfActiveNetworkConnectionAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new AlertDialog.Builder(MainActivity.this).setTitle("No Active Connection Available").setMessage("Please connect to a working internet connection").setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            validateIfActiveNetworkConnectionAvailable();
                        }
                    }).show();
                }
            });
        }
    }

    private void doFrontEndChangesIdle() {
        startButton.setText("Start");
        startButton.setTag("start");
        folderSelectedIconImageView.setImageResource(R.drawable.ic_not_done);
        filterSelectedIconImageView.setImageResource(R.drawable.ic_not_done);
        processingImagesProgressBar.setVisibility(View.INVISIBLE);
        startButton.setEnabled(false);
        selectedFolderPath = null;
        selectedFilters = null;
    }

    private void processingCompletedFrontendChanges() {
        doFrontEndChangesIdle();
        if (matchedImagesUrl.isEmpty())
            processingStatusTextView.setText("Processing Completed");
        else
            processingStatusTextView.setText("Processing Completed (Click on the image to open it)");
    }

    public class ImageAdapter extends BaseAdapter {

        Context context;
        ArrayList<Uri> imagesUri;

        @Override
        public int getCount() {
            return imagesUri.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            ImageView imageView;
            if (convertView == null) {
                imageView = new ImageView(context);
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                imageView.setLayoutParams(new GridView.LayoutParams(270, 270));
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setDataAndType(imagesUri.get(position), "image/*");
                        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                        startActivity(intent);
                    }
                });

            } else {
                imageView = (ImageView) convertView;
            }
            Glide.with(context).load(imagesUri.get(position)).placeholder(R.drawable.ic_launcher_foreground).centerCrop().into(imageView);
            return imageView;
        }

        public ImageAdapter(Context c, ArrayList<Uri> matchedImagesUrl) {
            context = c;
            imagesUri = matchedImagesUrl;
        }

        public ArrayList<Uri> getData() {
            return imagesUri;
        }
    }

}
