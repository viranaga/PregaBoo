package com.example.pregaboo.views;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.pregaboo.R;
import com.example.pregaboo.database.DataManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DashboardActivity extends AppCompatActivity {
    private TextView welcomeText;
    private Button buttonHome;
    private Button buttonSocial;
    private ImageButton buttonTracker;
    private ImageButton buttonNutrition;
    private ImageButton buttonExercise;
    private ImageButton buttonCommunity;
    private FirebaseAuth mAuth;
    private DataManager dataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        mAuth = FirebaseAuth.getInstance();
        dataManager = new DataManager(this);

        initializeViews();
        setupClickListeners();
        updateUserInfo();
    }

    private void initializeViews() {
        welcomeText = findViewById(R.id.welcomeText);
        buttonHome = findViewById(R.id.buttonHome);
        buttonSocial = findViewById(R.id.buttonSocial);
        buttonTracker = findViewById(R.id.buttonTracker);
        buttonNutrition = findViewById(R.id.buttonNutrition);
        buttonExercise = findViewById(R.id.buttonExercise);
        buttonCommunity = findViewById(R.id.buttonCommunity);
    }

    private void setupClickListeners() {
        buttonHome.setOnClickListener(v -> {
            // Already on home screen
        });

        buttonSocial.setOnClickListener(v -> {
            // TODO: Implement social screen navigation
        });

        buttonTracker.setOnClickListener(v -> {
            // TODO: Implement pregnancy tracker
        });

        buttonNutrition.setOnClickListener(v -> {
            // TODO: Implement nutrition guide
        });

        buttonExercise.setOnClickListener(v -> {
            // TODO: Implement exercise guide
        });

        buttonCommunity.setOnClickListener(v -> {
            // TODO: Implement community features
        });
    }

    private void updateUserInfo() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String displayName = user.getDisplayName();
            welcomeText.setText("Welcome, " + (displayName != null ? displayName : "User"));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // If not signed in, return to login screen
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }
} 