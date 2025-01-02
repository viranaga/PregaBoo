package com.example.pregaboo.views;

import android.content.Intent;
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

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int RC_SIGN_IN = 9001;
    
    private Button buttonExpecting;
    private ImageButton buttonGoogle;
    private ImageButton buttonApple;
    private ImageButton buttonEmail;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private DataManager dataManager;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        dataManager = new DataManager(this);
        firestore = FirebaseFirestore.getInstance();

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .requestProfile()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        buttonExpecting = findViewById(R.id.buttonExpecting);
        buttonGoogle = findViewById(R.id.buttonGoogle);
        buttonApple = findViewById(R.id.buttonApple);
        buttonEmail = findViewById(R.id.buttonEmail);

        buttonExpecting.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, LoginActivity2.class);
            startActivity(intent);
        });

        buttonGoogle.setOnClickListener(v -> signInWithGoogle());

        buttonApple.setOnClickListener(v -> {
            // TODO: Handle Apple sign in
        });

        buttonEmail.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, CreateAccountActivity.class);
            startActivity(intent);
        });
    }

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            try {
                GoogleSignInAccount account = GoogleSignIn.getSignedInAccountFromIntent(data)
                        .getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Log.w(TAG, "Google sign in failed", e);
                Toast.makeText(this, "Google sign in failed", Toast.LENGTH_SHORT).show();
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
                            // Check if user exists in Firestore
                            firestore.collection("users")
                                    .document(user.getUid())
                                    .get()
                                    .addOnCompleteListener(task2 -> {
                                        if (task2.isSuccessful()) {
                                            DocumentSnapshot document = task2.getResult();
                                            if (document != null && document.exists()) {
                                                // Existing user - go to dashboard
                                                goToDashboard();
                                            } else {
                                                // New user - save to Firestore and SQLite
                                                User newUser = new User(
                                                    user.getUid(),
                                                    user.getDisplayName(),
                                                    user.getEmail()
                                                );
                                                dataManager.saveUser(newUser);
                                                goToDashboard();
                                            }
                                        } else {
                                            Log.w(TAG, "Error checking user existence", task2.getException());
                                            Toast.makeText(this, "Authentication error", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    } else {
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void goToDashboard() {
        Intent intent = new Intent(this, DashboardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}