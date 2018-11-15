package com.example.retrofitdownloadzip;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.reactivex.disposables.Disposable;
import rx.downloadlibrary.DownloadEvent;
import rx.downloadlibrary.Downloader;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private MyRecyclerViewAdapter myRecyclerViewAdapter;
    private Disposable commandClickSubscriber;
    private Downloader downloader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RecyclerView recyclerView = findViewById(R.id.links);
        myRecyclerViewAdapter = new MyRecyclerViewAdapter();
        recyclerView.setAdapter(myRecyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<DownloadEvent> events = new ArrayList<>();
        events.add(DownloadEvent.newEvent("https://pkg.popguide.me/PM_000579_en_v20181018_124932.tgz", DownloadEvent.Type.DOWNLOAD));
        events.add(DownloadEvent.newEvent("https://pkg.popguide.me/PM_000183_ru_v20180703_082725.tgz", DownloadEvent.Type.DOWNLOAD));
        events.add(DownloadEvent.newEvent("https://pkg.popguide.me/PM_000626_zh_v20181005_150653.tgz", DownloadEvent.Type.DOWNLOAD));
        events.add(DownloadEvent.newEvent("https://pkg.popguide.me/PM_000202_it_v20180914_155611.tgz", DownloadEvent.Type.DOWNLOAD));
        myRecyclerViewAdapter.setEvents(events);


        commandClickSubscriber = myRecyclerViewAdapter.subscribe(event -> {
            Toast.makeText(getBaseContext(), event.getType().name(), Toast.LENGTH_SHORT).show();
            if (downloader != null) {
                if (event.getType() == DownloadEvent.Type.DOWNLOAD)
                    downloader.downloadFileTgz(event.getDownloadUrl(),
                            new File(getFilesDir() + File.separator + new Random().nextInt() + ".tgz"));
            }
        });
            
        downloader = new Downloader();
        downloader.subscribe(event -> {
            if (myRecyclerViewAdapter != null) myRecyclerViewAdapter.updateItem(event);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (commandClickSubscriber != null) commandClickSubscriber.dispose();
        if (downloader != null) downloader.dispose();
        commandClickSubscriber = null;
        downloader = null;
    }
}
