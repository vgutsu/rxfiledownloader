package com.example.retrofitdownloadzip;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.File;

import io.reactivex.functions.Consumer;
import rx.downloadlibrary.Downloader;
import rx.downloadlibrary.FileDownloader;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private TextView progressView;
    private Downloader downloader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressView = findViewById(R.id.progress);

        findViewById(R.id.download_rxjava).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadZipFileRx();
            }
        });
        findViewById(R.id.download_rxkotlin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadZipFileRxKotlin();
            }
        });
        findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloader.unRegisterListener();
                downloader.cancel();
            }
        });

    }

    private void downloadZipFileRxKotlin() {
        // does not work for now

        String url = "https://pkg.popguide.me/PM_000579_en_v20181018_124932.tgz";
        File saveLocation = new File(getFilesDir() + File.separator + "PM_000579_en_v20181018_124932.tgz");

        FileDownloader fileDownloader = new FileDownloader("https://google.com");
        fileDownloader.download(url, saveLocation, new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.e("", integer.toString());
            }
        });

    }


    private void downloadZipFileRx() {
        String url = "https://pkg.popguide.me/PM_000579_en_v20181018_124932.tgz";
        File saveLocation = new File(getFilesDir() + File.separator + "PM_000579_en_v20181018_124932.tgz");
        downloader = new Downloader();
        downloader.registerListener(progress -> progressView.setText("" + progress));
        downloader.downloadFile(url, saveLocation);
    }
}
