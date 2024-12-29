package com.example.pregaboo.controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.pregaboo.database.DatabaseHelper;
import com.example.pregaboo.models.User;

public class UserController {
    private DatabaseHelper dbHelper;

    public UserController(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public boolean createUser(String name, String email, String password) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_NAME, name);
        values.put(DatabaseHelper.COLUMN_EMAIL, email);
        values.put(DatabaseHelper.COLUMN_PASSWORD, password);

        long result = db.insert(DatabaseHelper.TABLE_USERS, null, values);
        db.close();
        return result != -1;
    }

    public User getUser(String email, String password) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_USERS,
                null,
                DatabaseHelper.COLUMN_EMAIL + "=? AND " + DatabaseHelper.COLUMN_PASSWORD + "=?",
                new String[]{email, password},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            User user = new User(
                    cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EMAIL)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PASSWORD))
            );
            cursor.close();
            db.close();
            return user;
        }

        db.close();
        return null;
    }
} 