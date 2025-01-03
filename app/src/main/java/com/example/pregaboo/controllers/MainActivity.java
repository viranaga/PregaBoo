package com.example.pregaboo.controllers;

import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.pregaboo.models.DateModel;

public class MainActivity extends AppCompatActivity {

    private DateModel dateModel; // Model

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the DateModel
        dateModel = new DateModel();

        // View components
        DatePicker datePicker = findViewById(R.id.datePicker);
        Button btnSubmit = findViewById(R.id.btnSubmit);
        TextView tvSelectedDate = findViewById(R.id.tvSelectedDate);

        // Initialize the date picker
        datePicker.init(2025, 0, 1, (view, year, monthOfYear, dayOfMonth) -> {
            // Update model with selected date
            dateModel.setDate(dayOfMonth, monthOfYear, year);
        });

        // Submit button action
        btnSubmit.setOnClickListener(view -> {
            // Update TextView with the formatted date from the model
            tvSelectedDate.setText("Selected Date: " + dateModel.getFormattedDate());
        });
    }
}
