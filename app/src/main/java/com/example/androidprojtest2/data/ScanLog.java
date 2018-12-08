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

    @ColumnInfo(name = "location")
    private String location;

    @ColumnInfo(name = "date_logged")
    @TypeConverters(DateConverter.class)
    private Date dateLogged;

    public ScanLog(int id, String location, Date dateLogged) {
        this.id = id;
        this.location = location;
        this.dateLogged = dateLogged;
    }

    @Ignore
    public ScanLog(String location, Date dateLogged) {
        this.location = location;
        this.dateLogged = dateLogged;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Date getDateLogged() {
        return dateLogged;
    }

    public void setDateLogged(Date dateLogged) {
        this.dateLogged = dateLogged;
    }
}
