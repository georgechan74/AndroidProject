package com.example.androidprojtest2.database;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;
import android.util.Log;

import java.util.List;

public class IdentificationRepository {
    private IdentificationDao mIdentificationDao;
    private LiveData<List<Identification>> mAllIDs;

    LiveData<List<Identification>> getAllIDs() {
        return mAllIDs;
    }

    public IdentificationRepository(Application application) {
        IdentificationRoomDatabase db = IdentificationRoomDatabase.getDatabase(application.getApplicationContext());
        mIdentificationDao = db.identificationDao();
        mAllIDs = mIdentificationDao.getAllEntries();
    }

    public Identification checkDatabase(String description) {
//        String output = new checkAsyncTask(mIdentificationDao).execute(identification);
//        return mIdentificationDao.getDescription(identification);
        List<Identification> ids = mIdentificationDao.getEntryFromDatabase(description);
        if (ids == null) {
            Log.d("isdatabaseempty", "yes");
            return null;
        }
        Log.d("isdatabaseempty", "no");
        if (ids.size() != 0) {
            Log.d("isdatabaseempty", ids.get(0).getDescription());
            return ids.get(0);
        }
        else {
            return null;
        }
    }

    public void insert(String description) {
        new insertAsyncTask(mIdentificationDao).execute(description);
    }

    private static class insertAsyncTask extends AsyncTask<String, Void, Void> {
        private IdentificationDao mAsyncTaskDao;
        insertAsyncTask(IdentificationDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(String... strings) {
            mAsyncTaskDao.insert(new Identification(strings[0]));
            return null;
        }
    }
}
