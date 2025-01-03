package com.example.pregaboo.views;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import com.example.pregaboo.R;
import com.example.pregaboo.database.DataManager;
import com.example.pregaboo.models.User;
import com.google.android.gms.auth.api.signin.*;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.*;
import com.google.firebase.firestore.*;

public class LoginActivity2 extends AppCompatActivity {
    private static final String TAG = "LoginActivity2";
    private static final int RC_SIGN_IN = 9001;
    private static final int DISTRICT_SELECTION_REQUEST = 1002;
    
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private DataManager dataManager;
    private Button signBtn;
    private ImageButton googleBtn;
    private EditText txtUsername;
    private EditText txtPassword;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);

        // Initialize Firebase components
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        dataManager = new DataManager(this);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Initialize views
        txtUsername = findViewById(R.id.txt_username);
        txtPassword = findViewById(R.id.txt_password);
        loginButton = findViewById(R.id.login_button);
        signBtn = findViewById(R.id.sign_btn);
        googleBtn = findViewById(R.id.google_btn);

        // Add login button click listener
        loginButton.setOnClickListener(v -> loginWithEmailPassword());

        // Keep existing button listeners
        signBtn.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity2.this, CreateAccountActivity.class);
            startActivity(intent);
        });

        googleBtn.setOnClickListener(v -> signIn());
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
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
            String contactNumber = data.getStringExtra("contact_number");
            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null && selectedDistrict != null && contactNumber != null) {
                saveNewUser(user, selectedDistrict, contactNumber);
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

    private void saveNewUser(FirebaseUser firebaseUser, String district, String contact) {
        User newUser = new User(
            firebaseUser.getUid(),
            firebaseUser.getDisplayName(),
            firebaseUser.getEmail(),
            district,
            contact
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

    private void loginWithEmailPassword() {
        String email = txtUsername.getText().toString().trim();
        String password = txtPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        goToDashboard();
                    }
                } else {
                    Toast.makeText(LoginActivity2.this, 
                        "Authentication failed: " + task.getException().getMessage(),
                        Toast.LENGTH_SHORT).show();
                }
            });
    }
}