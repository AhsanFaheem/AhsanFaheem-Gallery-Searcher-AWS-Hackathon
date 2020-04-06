package com.awshackathon.goforpic;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class TextFilterScreen extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_filter_screen);
    }

    public void textFilterSelectionCompleted(View view) {
        EditText searchTextEditText=findViewById(R.id.searchText);
        Intent intent=new Intent(TextFilterScreen.this, MainActivity.class);
        intent.putExtra("searchText",searchTextEditText.getText().toString());
        setResult(Activity.RESULT_OK, intent);
        finish();

    }
}
