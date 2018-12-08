package com.example.androidprojtest2.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {Identification.class}, version = 1)
public abstract class IdentificationRoomDatabase extends RoomDatabase {

    public abstract IdentificationDao identificationDao();

    private static volatile IdentificationRoomDatabase INSTANCE;

    static IdentificationRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (IdentificationRoomDatabase.class) {
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        IdentificationRoomDatabase.class, "identification_database")
                        .build();
            }
        }
        return INSTANCE;
    }
}
