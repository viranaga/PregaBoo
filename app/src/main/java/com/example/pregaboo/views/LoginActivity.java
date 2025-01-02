package com.example.pregaboo.views;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.pregaboo.R;
import com.example.pregaboo.database.DataManager;
import com.example.pregaboo.models.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import com.google.android.gms.tasks.Task;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int RC_SIGN_IN = 9001;
    private static final int DISTRICT_SELECTION_REQUEST = 1002;
    
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private DataManager dataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        dataManager = new DataManager(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        Button buttonExpecting = findViewById(R.id.buttonExpecting);
        Button buttonMidwife = findViewById(R.id.buttonExpecting2);
        ImageButton buttonGoogle = findViewById(R.id.buttonGoogle);

        buttonExpecting.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, LoginActivity2.class);
            startActivity(intent);
        });

        buttonMidwife.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, MidwifeLogin.class);
            startActivity(intent);
        });

        buttonGoogle.setOnClickListener(v -> signIn());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Log.w(TAG, "Google sign in failed", e);
                Toast.makeText(this, "Google Sign In failed", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == DISTRICT_SELECTION_REQUEST && resultCode == RESULT_OK) {
            String selectedDistrict = data.getStringExtra("selected_district");
            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null && selectedDistrict != null) {
                saveNewUser(user, selectedDistrict);
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            checkExistingUser(user);
                        }
                    } else {
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkExistingUser(FirebaseUser user) {
        firestore.collection("users")
                .document(user.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            goToDashboard();
                        } else {
                            Intent intent = new Intent(this, GoogleSigningLocation.class);
                            startActivityForResult(intent, DISTRICT_SELECTION_REQUEST);
                        }
                    } else {
                        Log.w(TAG, "Error checking user", task.getException());
                        Toast.makeText(this, "Authentication error", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveNewUser(FirebaseUser firebaseUser, String district) {
        User newUser = new User(
            firebaseUser.getUid(),
            firebaseUser.getDisplayName(),
            firebaseUser.getEmail(),
            district
        );
        dataManager.saveUser(newUser);
        goToDashboard();
    }

    private void goToDashboard() {
        Intent intent = new Intent(this, DashboardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
}