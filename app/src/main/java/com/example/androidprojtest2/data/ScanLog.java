package com.example.androidprojtest2.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import java.util.Date;

@Entity(tableName = "log_table")
public class ScanLog {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "date_logged")
    @TypeConverters(DateConverter.class)
    private Date dateLogged;

    public ScanLog(int id, String description, Date dateLogged) {
        this.id = id;
        this.description = description;
        this.dateLogged = dateLogged;
    }

    @Ignore
    public ScanLog(String description, Date dateLogged) {
        this.description = description;
        this.dateLogged = dateLogged;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDateLogged() {
        return dateLogged;
    }

    public void setDateLogged(Date dateLogged) {
        this.dateLogged = dateLogged;
    }
}
