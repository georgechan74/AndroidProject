package com.example.androidprojtest2;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.androidprojtest2.data.ScanLog;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {
    Context mContext;
    ArrayList<ScanLog> mLogs;

    public HistoryAdapter(Context context, ArrayList<ScanLog> logs) {
        this.mContext = context;
        this.mLogs = logs;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.history_log, parent, false);
        HistoryViewHolder viewHolder = new HistoryViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mLogs.size();
    }

    class HistoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView description;
        TextView date;

        public HistoryViewHolder(View itemView){
            super(itemView);
            description = (TextView) itemView.findViewById(R.id.history_description);
            date = (TextView) itemView.findViewById(R.id.history_date);
        }

        void bind(final int position) {
            description.setText(mLogs.get(position).getDescription());

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            String theDate = dateFormat.format(mLogs.get(position).getDateLogged());
            date.setText(theDate);
        }

        @Override
        public void onClick(View view) {

        }
    }
}