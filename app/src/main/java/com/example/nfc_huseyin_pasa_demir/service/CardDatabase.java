package com.example.nfc_huseyin_pasa_demir.service;

import android.content.Context;
import android.util.Log;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Card.class}, version = 1, exportSchema = false)
public abstract class CardDatabase extends RoomDatabase {
    private static final String LOG_TAG = CardDatabase.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "nfc_db";
    private static CardDatabase sInstance;

    public abstract CardDao nfcDao();

    public static CardDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                Log.d(LOG_TAG, "Creating new database instance");
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                                CardDatabase.class, CardDatabase.DATABASE_NAME)
                        .build();
            }
        }
        Log.d(LOG_TAG, "Getting the database instance");
        return sInstance;
    }


}
