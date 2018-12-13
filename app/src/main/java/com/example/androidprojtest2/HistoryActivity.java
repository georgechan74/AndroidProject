package com.example.androidprojtest2;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.androidprojtest2.data.ScanLog;
import com.example.androidprojtest2.data.ScanLogViewModel;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private HistoryAdapter mAdapter;
    private ScanLogViewModel mViewModel;
    private ArrayList<ScanLog> logs = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        mViewModel = ViewModelProviders.of(this).get(ScanLogViewModel.class);

        mRecyclerView = (RecyclerView) findViewById(R.id.history_view);
        mAdapter = new HistoryAdapter(this, logs);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mViewModel.getAllLogs().observe(this, new Observer<List<ScanLog>>() {
            @Override
            public void onChanged(@Nullable List<ScanLog> scanLogs) {
                Log.d("logssize", scanLogs.size() + "");
                populateRecyclerView(scanLogs);
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.history_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemThatWasClickedId = item.getItemId();

        if (itemThatWasClickedId == R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public void populateRecyclerView(List<ScanLog> logs) {
        mAdapter.mLogs.addAll(logs);
        mAdapter.notifyDataSetChanged();
    }
}