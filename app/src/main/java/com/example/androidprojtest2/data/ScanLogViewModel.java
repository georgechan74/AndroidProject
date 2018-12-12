package com.example.androidprojtest2.data;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

public class ScanLogViewModel extends AndroidViewModel {

    private ScanLogRepository mRepository;
    private LiveData<List<ScanLog>> mAllLogs;

    public ScanLogViewModel(@NonNull Application application) {
        super(application);
        mRepository = new ScanLogRepository(application);
        mAllLogs = mRepository.getAllLogs();
    }

    public LiveData<List<ScanLog>> getAllLogs() {
        return mAllLogs;
    }

    public void insert(ScanLog scanLog) {
        mRepository.insert(scanLog);
    }

    public int getSize() {
        return mRepository.getSize();
    }
}
