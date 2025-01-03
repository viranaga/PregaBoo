package com.example.pregaboo.views;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;
import com.example.pregaboo.R;

public class GoogleSigningLocation extends AppCompatActivity {
    private Spinner districtSpinner;
    private EditText contactInput;
    private Button confirmButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_signing_location);
        
        districtSpinner = findViewById(R.id.districtSpinner);
        contactInput = findViewById(R.id.contactInput);
        confirmButton = findViewById(R.id.confirmButton);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
            R.array.sri_lanka_districts,
            android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        districtSpinner.setAdapter(adapter);

        confirmButton.setOnClickListener(v -> {
            String contact = contactInput.getText().toString().trim();
            String selectedDistrict = districtSpinner.getSelectedItem().toString();
            
            if (contact.isEmpty()) {
                contactInput.setError("Contact number is required");
                return;
            }

            Intent resultIntent = new Intent();
            resultIntent.putExtra("selected_district", selectedDistrict);
            resultIntent.putExtra("contact_number", contact);
            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }
}