package com.awshackathon.goforpic;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class FiltersScreen extends AppCompatActivity {

    ListView filtersListView;
    String[] filtersItemsList;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filters_screen);
        filtersListView = findViewById(R.id.filtersList);
        filtersListView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        filtersItemsList = getResources().getStringArray(R.array.filters_list);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, filtersItemsList);
        filtersListView.setAdapter(adapter);

    }

    public void filtersSelectionCompleted(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putStringArrayListExtra("selectedFilters", getNamesOfFilters(filtersListView.getCheckedItemPositions()));
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    public ArrayList getNamesOfFilters(SparseBooleanArray selectedFilterItems) {
        ArrayList<String> selectedFilters = new ArrayList<>();
        for (int i = 0; i < selectedFilterItems.size(); i++) {
            if (selectedFilterItems.valueAt(i)) {
                selectedFilters.add(adapter.getItem(selectedFilterItems.keyAt(i)).toString());
            }
        }
        return selectedFilters;
    }
}
