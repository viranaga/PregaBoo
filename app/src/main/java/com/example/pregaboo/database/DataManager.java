package com.example.pregaboo.database;

import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.pregaboo.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class DataManager {
    private DatabaseHelper dbHelper;
    private FirebaseFirestore firestore;
    private Context context;
    private static final String TAG = "DataManager";

    public DataManager(Context context) {
        this.context = context;
        this.dbHelper = new DatabaseHelper(context);
        this.firestore = FirebaseFirestore.getInstance();
    }

    public void saveUser(User user) {
        // Save to Firestore
        firestore.collection("users")
                .document(user.getId())
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    // Save to SQLite after successful Firestore save
                    saveUserToLocal(user);
                })
                .addOnFailureListener(e -> 
                    Log.e(TAG, "Error saving user to Firestore", e));
    }

    private void saveUserToLocal(User user) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("firebase_id", user.getId());
        values.put("name", user.getName());
        values.put("email", user.getEmail());
        values.put("location", user.getLocation());

        try {
            db.insertWithOnConflict("users", null, values, 
                SQLiteDatabase.CONFLICT_REPLACE);
        } catch (Exception e) {
            Log.e(TAG, "Error saving user to SQLite", e);
        } finally {
            db.close();
        }
    }

    public void getUser(String userId, OnCompleteListener<DocumentSnapshot> listener) {
        // First try to get from local database
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("users", null, 
            "firebase_id = ?", new String[]{userId}, 
            null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            // Return data from local database
            User user = new User(
                cursor.getString(cursor.getColumnIndex("firebase_id")),
                cursor.getString(cursor.getColumnIndex("name")),
                cursor.getString(cursor.getColumnIndex("email")),
                cursor.getString(cursor.getColumnIndex("location"))
            );
            cursor.close();
            db.close();
            
            // Create a successful task result
            Task<DocumentSnapshot> task = Tasks.forResult(null);
            listener.onComplete(task);
        } else {
            // If not in local database, fetch from Firestore
            firestore.collection("users")
                    .document(userId)
                    .get()
                    .addOnCompleteListener(listener)
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            User user = documentSnapshot.toObject(User.class);
                            if (user != null) {
                                saveUserToLocal(user);
                            }
                        }
                    });
        }
    }

    public void syncWithFirestore() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) return;

        // Sync users collection
        firestore.collection("users")
                .whereEqualTo("id", currentUser.getUid())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        User user = document.toObject(User.class);
                        if (user != null) {
                            saveUserToLocal(user);
                        }
                    }
                });
    }
}

