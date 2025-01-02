package com.example.pregaboo.controllers;

import android.content.Context;
import com.example.pregaboo.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class UserController {
    private FirebaseFirestore db;

    public UserController(Context context) {
        db = FirebaseFirestore.getInstance();
    }

    public void createUser(String id, String name, String email, OnCompleteListener<Void> onCompleteListener) {
        User user = new User(id, name, email);
        db.collection("users").document(id).set(user).addOnCompleteListener(onCompleteListener);
    }

    public void getUser(String email, OnCompleteListener<QuerySnapshot> onCompleteListener) {
        db.collection("users").whereEqualTo("email", email).get().addOnCompleteListener(onCompleteListener);
    }
}