package com.example.pregaboo.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import com.example.pregaboo.R;


public class LoginActivity extends AppCompatActivity {
    private Button buttonExpecting;
    private ImageButton buttonGoogle;
    private ImageButton buttonApple;
    private ImageButton buttonEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        buttonExpecting = findViewById(R.id.buttonExpecting);
        buttonGoogle = findViewById(R.id.buttonGoogle);
        buttonApple = findViewById(R.id.buttonApple);
        buttonEmail = findViewById(R.id.buttonEmail);

        buttonExpecting.setOnClickListener(v -> {
        // TODO: Handle expecting baby button click
            Intent intent = new Intent(LoginActivity.this, LoginActivity2.class);
            startActivity(intent);
        });



        buttonGoogle.setOnClickListener(v -> {
            // TODO: Handle Google sign in
        });

        buttonApple.setOnClickListener(v -> {
            // TODO: Handle Apple sign in
        });

        buttonEmail.setOnClickListener(v -> {
            // TODO: Handle Email sign in




        });
    }
}