package com.example.speech_to_sign_language;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClickRecordButton(View view) {
        final TextView debugTextView = findViewById(R.id.debugText);
        final TextView inputTextView = findViewById(R.id.inputText); // TODO
        final String inputText = inputTextView.getText().toString();
        debugTextView.setText(inputText);

        final GridView signImagesGrid = findViewById(R.id.signImagesGrid);

        String[] plants = new String[]{
                "Catalina ironwood",
                "Cabinet cherry",
                "Pale corydalis",
                "Pink corydalis",
                "Land cress",
                "Coast polypody",
                "Water fern"
        };

        // Populate a List from Array elements
        final List<String> plantsList = new ArrayList<String>(Arrays.asList(plants));

        // Create a new ArrayAdapter
        final ArrayAdapter<String> gridViewArrayAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1, plantsList);

        signImagesGrid.setAdapter(gridViewArrayAdapter);
    }
}
>>>>>>> Stashed changes
