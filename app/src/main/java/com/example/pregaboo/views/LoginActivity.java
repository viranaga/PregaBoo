package com.example.pregaboo.views;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.example.pregaboo.R;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button buttonExpecting = findViewById(R.id.buttonExpecting);
        Button buttonMidwife = findViewById(R.id.buttonExpecting2);

        buttonExpecting.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, LoginActivity2.class);
            startActivity(intent);
        });

        buttonMidwife.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, MidwifeLogin.class);
            startActivity(intent);
        });
    }
}