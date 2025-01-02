package com.example.pregaboo.views;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;
import com.example.pregaboo.R;

public class GoogleSigningLocation extends AppCompatActivity {
    private Spinner districtSpinner;
    private Button confirmButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_signing_location);
        
        // Initialize views
        districtSpinner = findViewById(R.id.districtSpinner);
        confirmButton = findViewById(R.id.confirmButton);

        // Set up the spinner with districts array from resources
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
            R.array.sri_lanka_districts,
            android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        districtSpinner.setAdapter(adapter);

        // Handle confirm button click
        confirmButton.setOnClickListener(v -> {
            String selectedDistrict = districtSpinner.getSelectedItem().toString();
            Intent resultIntent = new Intent();
            resultIntent.putExtra("selected_district", selectedDistrict);
            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }
}