package com.example.androidprojtest2.data;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

public class ScanLogRepository {
    private ScanLogDao mScanLogDao;
    private LiveData<List<ScanLog>> mAllLogs;

    public ScanLogRepository(Application application){
        ScanLogRoomDatabase db = ScanLogRoomDatabase.getDatabase(application.getApplicationContext());
        mScanLogDao = db.scanLogDao();
        mAllLogs = mScanLogDao.getAllLogs();
    }

    LiveData<List<ScanLog>> getAllLogs() {
        return mAllLogs;
    }

    public void insert(ScanLog scanLog) {
        new insertAsyncTask(mScanLogDao).execute(scanLog);
    }

    private static class insertAsyncTask extends AsyncTask<ScanLog, Void, Void> {
        private ScanLogDao mAsyncTaskDao;
        insertAsyncTask(ScanLogDao dao) { mAsyncTaskDao = dao; }

        @Override
        protected Void doInBackground(ScanLog... scanLogs) {
            mAsyncTaskDao.insert(scanLogs[0]);
            return null;
        }
    }

    int getSize() {
        return mScanLogDao.getSize();
    }
}
