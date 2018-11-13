package com.example.retrofitdownloadzip;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import rx.RxBus;
import rx.downloadlibrary.ProgressEvent;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

    private List<String> mData;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    public MyRecyclerViewAdapter(List<String> data) {
        this.mData = data;
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
        String link = mData.get(position);
        holder.linkTextView.setText(link);

        RxBus.listen(ProgressEvent.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<ProgressEvent>() {
                    @Override
                    public void onNext(ProgressEvent progressEvent) {
                        if (progressEvent.getDownloadIdentifier().equals(link))
                            holder.cancel.setText(""+progressEvent.getProgress());
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        holder.cancel.setText("done");
                    }
                });
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setProgress(ProgressEvent progress) {
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView linkTextView;
        Button cancel;

        ViewHolder(View itemView) {
            super(itemView);
            linkTextView = itemView.findViewById(R.id.link);
            cancel = itemView.findViewById(R.id.cancel);
            linkTextView.setOnClickListener(this);
            cancel.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.cancel:
                    mClickListener.onCancel(getAdapterPosition());
                    break;
                case R.id.link:
                    mClickListener.onDownload(getAdapterPosition());
                    break;
            }
        }
    }

    // convenience method for getting data at click position
    String getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onDownload(int position);

        void onCancel(int position);
    }
}