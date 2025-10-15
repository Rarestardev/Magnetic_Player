package com.rarestardev.magneticplayer.database.db;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.rarestardev.magneticplayer.utilities.Constants;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


// Singleton
public class DatabaseClient {
    private static DatabaseClient instance;
    private final AppDatabase appDatabase;
    private static final String DB_NAME = "com.rarestardev.magneticplayer.MAGNETIC_PLAYER_DATABASE";

    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    private DatabaseClient(Context context) {
        appDatabase = Room.databaseBuilder(context, AppDatabase.class, DB_NAME)
                .fallbackToDestructiveMigration()
                .addCallback(roomCallback)
                .build();
    }

    public static synchronized DatabaseClient getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseClient(context);
        }
        return instance;
    }

    public AppDatabase getAppDatabase() {
        return appDatabase;
    }

    private static final RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            Log.d(Constants.appLog,"db created");
        }

        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
            // This will be called each time the database is opened
            Log.d(Constants.appLog, "Database Opened");
        }
    };
}




