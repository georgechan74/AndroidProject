package com.example.androidprojtest2.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {ScanLog.class}, version = 1)
public abstract class ScanLogRoomDatabase extends RoomDatabase {

    public abstract ScanLogDao scanLogDao();
    private static volatile ScanLogRoomDatabase INSTANCE;

    static ScanLogRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (ScanLogRoomDatabase.class) {
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        ScanLogRoomDatabase.class, "log_database")
                        .build();
            }
        }
        return INSTANCE;
    }
}
