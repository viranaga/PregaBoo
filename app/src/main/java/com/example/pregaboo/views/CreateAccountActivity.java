package com.example.pregaboo.views;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import com.example.pregaboo.R;
import com.example.pregaboo.models.User;
import com.example.pregaboo.database.DataManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class CreateAccountActivity extends AppCompatActivity {
    private EditText emailInput;
    private EditText passwordInput;
    private EditText contactInput;
    private Spinner districtSpinner;
    private Button signUpButton;
    private FirebaseAuth mAuth;
    private DataManager dataManager;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST = 1001;

    private final String[] sriLankaDistricts = {
        "Colombo", "Gampaha", "Kalutara", "Kandy", "Matale", "Nuwara Eliya",
        "Galle", "Matara", "Hambantota", "Jaffna", "Kilinochchi", "Mannar",
        "Vavuniya", "Mullaitivu", "Batticaloa", "Ampara", "Trincomalee",
        "Kurunegala", "Puttalam", "Anuradhapura", "Polonnaruwa", "Badulla",
        "Monaragala", "Ratnapura", "Kegalle"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        mAuth = FirebaseAuth.getInstance();
        dataManager = new DataManager(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        initializeViews();
        setupDistrictSpinner();
        requestLocationPermission();
    }

    private void initializeViews() {
        emailInput = findViewById(R.id.email);
        passwordInput = findViewById(R.id.password);
        contactInput = findViewById(R.id.contact_number);
        districtSpinner = findViewById(R.id.district_spinner);
        signUpButton = findViewById(R.id.next_button);

        signUpButton.setOnClickListener(v -> createAccount());
    }

    private void setupDistrictSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
            this, 
            android.R.layout.simple_spinner_item, 
            sriLankaDistricts
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        districtSpinner.setAdapter(adapter);
    }

    private void requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) 
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                LOCATION_PERMISSION_REQUEST);
        } else {
            getNearestDistrict();
        }
    }

    private void getNearestDistrict() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) 
            == PackageManager.PERMISSION_GRANTED) {
            
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                if (location != null) {
                    String nearestDistrict = findNearestDistrict(location);
                    int position = findDistrictPosition(nearestDistrict);
                    districtSpinner.setSelection(position);
                }
            });
        }
    }

    private String findNearestDistrict(Location userLocation) {
        // Approximate district center coordinates
        double[][] districtCoordinates = {
            {6.9271, 79.8612}, // Colombo
            {7.0878, 80.0168}, // Gampaha
            {6.5854, 79.9607}  // Kalutara
            // Add more district coordinates
        };

        double minDistance = Double.MAX_VALUE;
        String nearestDistrict = sriLankaDistricts[0];

        for (int i = 0; i < districtCoordinates.length; i++) {
            Location districtLocation = new Location("");
            districtLocation.setLatitude(districtCoordinates[i][0]);
            districtLocation.setLongitude(districtCoordinates[i][1]);

            float distance = userLocation.distanceTo(districtLocation);
            if (distance < minDistance) {
                minDistance = distance;
                nearestDistrict = sriLankaDistricts[i];
            }
        }

        return nearestDistrict;
    }

    private int findDistrictPosition(String district) {
        for (int i = 0; i < sriLankaDistricts.length; i++) {
            if (sriLankaDistricts[i].equals(district)) {
                return i;
            }
        }
        return 0;
    }

    private void createAccount() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String contact = contactInput.getText().toString().trim();
        String district = districtSpinner.getSelectedItem().toString();

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
                                contact,  // Using contact as name
                                email,
                                district,  // Using selected district as location
                                contact   // Adding contact number
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getNearestDistrict();
            }
        }
    }
}