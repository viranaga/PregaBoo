package com.example.pregaboo.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "PregaBooDB";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    private static final String TABLE_USERS = "users";
    private static final String TABLE_PREGNANCY_DATA = "pregnancy_data";

    // Common column names
    private static final String KEY_ID = "id";
    private static final String KEY_CREATED_AT = "created_at";

    // Users Table Columns
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_FIREBASE_ID = "firebase_id";

    // Pregnancy Data Table Columns
    private static final String KEY_WEEK = "week";
    private static final String KEY_WEIGHT = "weight";
    private static final String KEY_MOOD = "mood";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_FIREBASE_ID + " TEXT,"
                + KEY_NAME + " TEXT,"
                + KEY_EMAIL + " TEXT,"
                + KEY_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                + ")";

        String CREATE_PREGNANCY_DATA_TABLE = "CREATE TABLE " + TABLE_PREGNANCY_DATA + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_FIREBASE_ID + " TEXT,"
                + KEY_WEEK + " INTEGER,"
                + KEY_WEIGHT + " REAL,"
                + KEY_MOOD + " TEXT,"
                + KEY_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                + ")";

        db.execSQL(CREATE_USERS_TABLE);
        db.execSQL(CREATE_PREGNANCY_DATA_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PREGNANCY_DATA);
        onCreate(db);
    }
}