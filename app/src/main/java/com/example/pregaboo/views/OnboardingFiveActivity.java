package com.example.pregaboo.views;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pregaboo.R;
import com.example.pregaboo.views.LoginActivity;

public class OnboardingFiveActivity extends AppCompatActivity {
    private Button buttonGetStarted;
    private ImageView[] dots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding_five);

        buttonGetStarted = findViewById(R.id.buttonGetStarted);
        
        dots = new ImageView[4];
        dots[0] = findViewById(R.id.dot1);
        dots[1] = findViewById(R.id.dot2);
        dots[2] = findViewById(R.id.dot3);
        dots[3] = findViewById(R.id.dot4);

        buttonGetStarted.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
} 