package com.example.pregaboo.views;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.pregaboo.R;
import com.example.pregaboo.models.User;
import com.example.pregaboo.database.DataManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class CreateAccountActivity extends AppCompatActivity {
    private EditText emailInput;
    private EditText passwordInput;
    private EditText contactInput;
    private Button signUpButton;
    private FirebaseAuth mAuth;
    private DataManager dataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        mAuth = FirebaseAuth.getInstance();
        dataManager = new DataManager(this);

        // Initialize views with correct IDs from layout
        emailInput = findViewById(R.id.email);
        passwordInput = findViewById(R.id.password);
        contactInput = findViewById(R.id.contact_number);
        signUpButton = findViewById(R.id.sign_btn);

        signUpButton.setOnClickListener(v -> createAccount());
    }

    private void createAccount() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String contact = contactInput.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty() || contact.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            User user = new User(
                                firebaseUser.getUid(),
                                contact,
                                email,
                                "Unknown Location"
                            );

                            dataManager.saveUser(user);

                            Intent intent = new Intent(CreateAccountActivity.this, DashboardActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        Toast.makeText(CreateAccountActivity.this, 
                            "Sign up failed: " + task.getException().getMessage(),
                            Toast.LENGTH_SHORT).show();
                    }
                });
    }
}