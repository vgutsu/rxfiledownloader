package com.example.retrofitdownloadzip;

import android.content.Context;
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
import rx.downloadlibrary.Downloadable;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

    PublishSubject<Downloadable> publisher = PublishSubject.create();

    private List<Downloadable> downloadEvents = new ArrayList<>();

    public void setEvents(List<Downloadable> data) {
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
        Downloadable downloadEvent = downloadEvents.get(position);
        holder.linkTextView.setText(downloadEvent.getDownloadUrl());
        holder.cancelButton.setText("" + downloadEvent.getProgress());
        holder.itemView.setOnClickListener(v -> {
            downloadEvent.setType(Downloadable.Type.DOWNLOAD);
            publisher.onNext(downloadEvent);
        });
        holder.cancelButton.setOnClickListener(v -> {
            downloadEvent.setType(Downloadable.Type.CANCEL);
            publisher.onNext(downloadEvent);
        });

    }

    public void updateItem(Downloadable downloadable) {
        downloadEvents.set(downloadEvents.indexOf(downloadable), downloadable);
        notifyItemChanged(downloadEvents.indexOf(downloadable), downloadable);
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

        public Context getContext() {
            return itemView.getContext();
        }
    }

    public Disposable subscribe(Consumer<Downloadable> consumer) {
        return publisher.subscribe(consumer);
    }
}