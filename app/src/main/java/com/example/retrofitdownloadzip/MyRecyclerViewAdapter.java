package com.example.retrofitdownloadzip;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;
import rx.downloadlibrary.DownloadEvent;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

    PublishSubject<DownloadEvent> publisher = PublishSubject.create();

    private List<DownloadEvent> downloadEvents = new ArrayList<>();

    public void setEvents(List<DownloadEvent> data) {
        this.downloadEvents = data;
        notifyDataSetChanged();
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_row, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        DownloadEvent downloadEvent = downloadEvents.get(position);
        holder.linkTextView.setText(downloadEvent.getDownloadUrl());
        holder.cancelButton.setText("" + downloadEvent.getProgress());
        holder.itemView.setOnClickListener(v -> {
            if (downloadEvent.getProgress() == 0 || downloadEvent.getProgress() == 100) {
                downloadEvent.setType(DownloadEvent.Type.DOWNLOAD);
                publisher.onNext(downloadEvent);
            }
        });
        holder.cancelButton.setOnClickListener(v -> {
            downloadEvent.setType(DownloadEvent.Type.CANCEL);
            publisher.onNext(downloadEvent);
        });

    }

    public void updateItem(DownloadEvent event) {
        downloadEvents.set(downloadEvents.indexOf(event), event);
        notifyItemChanged(downloadEvents.indexOf(event), event);
    }


    // total number of rows
    @Override
    public int getItemCount() {
        return downloadEvents.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView linkTextView;
        Button cancelButton;

        ViewHolder(View itemView) {
            super(itemView);
            linkTextView = itemView.findViewById(R.id.link);
            cancelButton = itemView.findViewById(R.id.cancel);
        }
    }

    public Disposable subscribe(Consumer<DownloadEvent> consumer) {
        return publisher.subscribe(consumer);
    }
}