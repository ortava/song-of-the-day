package com.example.sotdprototype;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Track.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract TrackDAO trackDAO();
    private static AppDatabase dbInstance;

    public static AppDatabase getDbInstance(Context context) {
        if(dbInstance == null) {
            dbInstance = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "app-db")
                    .allowMainThreadQueries()
                    .build();
        }
        return dbInstance;
    }
}
