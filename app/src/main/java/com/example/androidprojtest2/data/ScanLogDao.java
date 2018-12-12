package com.example.androidprojtest2.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface ScanLogDao {
    @Insert
    void insert (ScanLog scanLog);

    @Query("SELECT * FROM log_table ORDER BY date_logged ASC")
    LiveData<List<ScanLog>> getAllLogs();

    @Query("SELECT COUNT(description) from log_table")
    int getSize();
}
