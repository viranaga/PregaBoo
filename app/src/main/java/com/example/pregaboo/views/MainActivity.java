package com.example.pregaboo.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.pregaboo.R;
import com.google.firebase.FirebaseApp;

public class MainActivity extends AppCompatActivity {
    private Button buttonSkip;
    private Button buttonNext;
    private ImageView[] dots;
    private int currentPage = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);

        // Initialize views
        buttonSkip = findViewById(R.id.buttonSkip);
        buttonNext = findViewById(R.id.buttonNext);

        // Initialize dots array
        dots = new ImageView[3];
        dots[0] = findViewById(R.id.dot1);
        dots[1] = findViewById(R.id.dot2);
        dots[2] = findViewById(R.id.dot3);

        // Update dots to show initial state
        updateDots(currentPage);

        // Set click listener for Skip button
        buttonSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Implement skip functionality
                // For example, go to main app screen
                finishOnboarding();
            }
        });

        // Set click listener for Next button
        buttonNext.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, OnboardingTwoActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void updateDots(int currentPage) {
        for (int i = 0; i < dots.length; i++) {
            if (i == currentPage) {
                dots[i].setImageResource(R.drawable.dot_selected);
            } else {
                dots[i].setImageResource(R.drawable.dot_unselected);
            }
        }
    }

    private void updateButtonText() {
        if (currentPage == 2) {
            buttonNext.setText("GET STARTED");
        } else {
            buttonNext.setText("NEXT");
        }
    }

    private void finishOnboarding() {
        // TODO: Implement transition to main app
        // For now, just finish the activity
        finish();
    }
}