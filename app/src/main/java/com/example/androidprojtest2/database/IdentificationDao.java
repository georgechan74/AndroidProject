package com.example.androidprojtest2.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface IdentificationDao {
    @Insert
    void insert(Identification identification);

    @Query("SELECT * FROM identification_table WHERE description LIKE :value")
    List<Identification> getEntryFromDatabase(String value);

    @Query("SELECT * FROM identification_table")
    LiveData<List<Identification>> getAllEntries();
}
