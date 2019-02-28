package com.example.retrofitdownloadzip;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import rx.downloadlibrary.Downloadable;
import rx.downloadlibrary.Downloader;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private MyRecyclerViewAdapter myRecyclerViewAdapter;
    private Downloader downloader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RecyclerView recyclerView = findViewById(R.id.links);
        myRecyclerViewAdapter = new MyRecyclerViewAdapter();
        recyclerView.setAdapter(myRecyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<Downloadable> events = new ArrayList<>();
        events.add(Downloadable.Companion.newEvent("https://pkg.popguide.me/PM_000579_en_v20181018_124932.tgz"));
        events.add(Downloadable.Companion.newEvent("https://pkg.popguide.me/PM_000183_ru_v20180703_082725.tgz"));
        events.add(Downloadable.Companion.newEvent("https://pkg.popguide.me/PM_000626_zh_v20181005_150653.tgz"));
        events.add(Downloadable.Companion.newEvent("https://pkg.popguide.me/PM_000202_it_v20180914_155611.tgz"));
        myRecyclerViewAdapter.setEvents(events);


        downloader = new Downloader();
        downloader.setFilesDir(getFilesDir());
        downloader.listenProgress(event -> myRecyclerViewAdapter.updateItem(event));
        myRecyclerViewAdapter.subscribe(event -> {
            Toast.makeText(getBaseContext(), event.getType().name(), Toast.LENGTH_SHORT).show();
            if (event.getType() == Downloadable.Type.DOWNLOAD) downloader.download(event);
            if (event.getType() == Downloadable.Type.CANCEL) downloader.cancel(event);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (downloader != null) downloader.dispose();
    }
}
