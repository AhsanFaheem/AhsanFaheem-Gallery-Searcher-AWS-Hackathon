package com.awshackathon.goforpic;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;

import com.awshackathon.goforpic.domain.Filters;

import java.util.ArrayList;
import java.util.Arrays;

public class AllFilters extends AppCompatActivity {

    ListView objectsListView;
    ListView emotionsListView;
    EditText toSearchInImages;
    
    private ArrayList<String> selectedObjectFilters;
    private ArrayList<String> selectedEmotionsFilters;
    private ArrayAdapter<String> objectsFiltersAdapter;
    private ArrayAdapter<String> emotionsFiltersAdapter;
LinearLayout textFiltersEditTextsLayout;
    ScrollView textFiltersScrollView;

    private ArrayList<EditText> toSearchTextEditTextsLists;
    int editTextsCount=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_filters);

        objectsListView = findViewById(R.id.objectsListView);
        objectsListView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        objectsFiltersAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, Arrays.asList(getResources().getStringArray(R.array.objects_filter_list)));
        objectsListView.setAdapter(objectsFiltersAdapter);
        emotionsListView = findViewById(R.id.emotionsListView);
        emotionsListView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        emotionsFiltersAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, Arrays.asList(getResources().getStringArray(R.array.emotions_filter_list)));
        emotionsListView.setAdapter(emotionsFiltersAdapter);
        toSearchInImages=findViewById(R.id.textToSearch);

        textFiltersEditTextsLayout=findViewById(R.id.testToSearchEditTextsLayout);
        textFiltersScrollView=findViewById(R.id.textToSearchScrollView);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        toSearchTextEditTextsLists=new ArrayList<>();
        toSearchTextEditTextsLists.add(toSearchInImages);
    }

    public void filtersSelectionCompleted(View view) {
        selectedObjectFilters=getNamesOfFilters(objectsListView.getCheckedItemPositions(),objectsFiltersAdapter);
        selectedEmotionsFilters=getNamesOfFilters(emotionsListView.getCheckedItemPositions(),emotionsFiltersAdapter);
        

        Filters filters=new Filters();
        filters.setObjectsFiltersList(selectedObjectFilters);
        filters.setEmotionsFiltersList(selectedEmotionsFilters);

        ArrayList<String> textFilters = new ArrayList<>();
        for(int i=0;i<toSearchTextEditTextsLists.size();i++){
            if(!toSearchTextEditTextsLists.get(i).getText().toString().isEmpty())
               textFilters.add(toSearchTextEditTextsLists.get(i).getText().toString());
        }

        filters.setTextFilterList(textFilters);

        Intent intent=new Intent(AllFilters.this,MainActivity.class);
        intent.putExtra("selectedFilters", filters);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
    public ArrayList<String> getNamesOfFilters(SparseBooleanArray selectedFilterItems,ArrayAdapter<String> adapter) {
        ArrayList<String> selectedFilters = new ArrayList<>();
        for (int i = 0; i < selectedFilterItems.size(); i++) {
            if (selectedFilterItems.valueAt(i)) {
                selectedFilters.add(adapter.getItem(selectedFilterItems.keyAt(i)).toString());
            }
        }
        return selectedFilters;
    }

    public void addMoreTextFilter(View view) {


        ScrollView.LayoutParams layoutParams=new ScrollView.LayoutParams(300,LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(100,0,0,0);
        EditText editText=new EditText(this);
        editText.setHint("Enter Text To Search");
        editText.setTextSize(12);
        editText.setLayoutParams(layoutParams);
        toSearchTextEditTextsLists.add(editText);

        textFiltersEditTextsLayout.addView(editText);
        textFiltersScrollView.post(new Runnable() {
            @Override
            public void run() {
                textFiltersScrollView.fullScroll(View.FOCUS_DOWN);
            }
        });
    }
}
