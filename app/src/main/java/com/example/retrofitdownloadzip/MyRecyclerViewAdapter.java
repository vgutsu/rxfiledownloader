package com.example.retrofitdownloadzip;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import rx.downloadlibrary.Downloadable;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

    private List<Popmap> popmaps = new ArrayList<>();
    private ItemListener listener;

    public void addPackages(List<Popmap> data) {
        this.popmaps = data;
        notifyDataSetChanged();
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_row, parent, false);
        return new ViewHolder(view);
    }

    public void setItemListener(ItemListener l) {
        this.listener = l;
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Popmap p = popmaps.get(position);
        holder.linkTextView.setText(String.format("lang_id = %d %d - %d", position, p.getProgress().first, p.getProgress().second));
        holder.progress.setProgress(((Long) p.getProgress().first).intValue());
        holder.progress.setMax(((Long) p.getProgress().second).intValue());
        holder.itemView.setOnClickListener(v -> listener.onLoad(p));
        holder.cancel.setOnClickListener(v -> listener.onCancel(p));
    }

    public void updateItemProgress(Downloadable d) {
        for (Popmap p : popmaps) {
            if (p.contains(d)) {
                p.updateProgress(d);
                notifyItemChanged(popmaps.indexOf(p), p);
            }
        }
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return popmaps.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progress;
        TextView linkTextView;
        Button cancel;

        ViewHolder(View itemView) {
            super(itemView);
            linkTextView = itemView.findViewById(R.id.link);
            progress = itemView.findViewById(R.id.progress);
            cancel = itemView.findViewById(R.id.cancel);
        }
    }

    public interface ItemListener {
        void onLoad(Popmap p);

        void onCancel(Popmap p);
    }
}