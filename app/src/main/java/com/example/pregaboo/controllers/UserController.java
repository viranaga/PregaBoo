package com.example.pregaboo.controllers;

import android.content.Context;
import com.example.pregaboo.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.auth.FirebaseUser;

public class UserController {
    private FirebaseFirestore db;

    public UserController(Context context) {
        db = FirebaseFirestore.getInstance();
    }

    // For manual registration
    public void createManualUser(String id, String contact, String email, String district, 
                               OnCompleteListener<Void> onCompleteListener) {
        User user = new User(id, contact, email, district, contact);
        saveUserToFirestore(user, onCompleteListener);
    }

    // For Google Sign-In
    public void createGoogleUser(FirebaseUser firebaseUser, String district, String contact,
                               OnCompleteListener<Void> onCompleteListener) {
        User user = new User(
            firebaseUser.getUid(),
            firebaseUser.getDisplayName(),
            firebaseUser.getEmail(),
            district,
            contact
        );
        saveUserToFirestore(user, onCompleteListener);
    }

    private void saveUserToFirestore(User user, OnCompleteListener<Void> onCompleteListener) {
        db.collection("users")
          .document(user.getId())
          .set(user)
          .addOnCompleteListener(onCompleteListener);
    }

    public void getUser(String email, OnCompleteListener<QuerySnapshot> onCompleteListener) {
        db.collection("users")
          .whereEqualTo("email", email)
          .get()
          .addOnCompleteListener(onCompleteListener);
    }
}