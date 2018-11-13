package com.example.retrofitdownloadzip;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.io.File;
import java.util.Arrays;

import rx.downloadlibrary.Downloader;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private Downloader downloader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RecyclerView recyclerView = findViewById(R.id.links);
        String[] links = {
                "https://pkg.popguide.me/PM_000579_en_v20181018_124932.tgz",
                "https://pkg.popguide.me/PM_000183_ru_v20180703_082725.tgz",
                "https://pkg.popguide.me/PM_000202_it_v20180914_155611.tgz",
                "https://pkg.popguide.me/PM_000626_zh_v20181005_150653.tgz"
        };
        MyRecyclerViewAdapter myRecyclerViewAdapter = new MyRecyclerViewAdapter(Arrays.asList(links));
        recyclerView.setAdapter(myRecyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        myRecyclerViewAdapter.setClickListener(new MyRecyclerViewAdapter.ItemClickListener() {

            @Override
            public void onCancel(int position) {
                if (downloader != null) downloader.cancel(links[position]);
            }

            @Override
            public void onDownload(int position) {
                if (downloader != null) downloader.downloadFileTgz(links[position],
                        new File(getFilesDir() + File.separator + position + ".tgz"));
            }
        });

        downloader = new Downloader();
        downloader.registerListener(progress -> {
            myRecyclerViewAdapter.setProgress(progress);
            Log.i("progress", progress.getDownloadIdentifier() + " " + progress.getProgress());
        });

    }
}
