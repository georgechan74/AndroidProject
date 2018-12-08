package com.example.androidprojtest2.database;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

public class IdentificationViewModel extends AndroidViewModel {

    private IdentificationRepository mRepository;
    private LiveData<List<Identification>> mAllIDs;

    public IdentificationViewModel(@NonNull Application application) {
        super(application);
        mRepository = new IdentificationRepository(application);
        mAllIDs = mRepository.getAllIDs();
    }

    public Identification checkDatabase(String description) {
        return mRepository.checkDatabase(description);
    }

    public void insert(String identification) {
        mRepository.insert(identification);
    }

    public LiveData<List<Identification>> getAllEntries() {
        return mAllIDs;
    }
}
