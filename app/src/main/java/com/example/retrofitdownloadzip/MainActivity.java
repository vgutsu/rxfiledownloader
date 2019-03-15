package com.example.retrofitdownloadzip;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.example.retrofitdownloadzip.v53.DetailsResponse;
import com.google.gson.GsonBuilder;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.downloadlibrary.CustomCompositeDisposable;
import rx.downloadlibrary.Downloader;

public class MainActivity extends AppCompatActivity implements MyRecyclerViewAdapter.ItemListener {
    private MyRecyclerViewAdapter myRecyclerViewAdapter;
    private Downloader downloader;
    CustomCompositeDisposable compositeDisposable = new CustomCompositeDisposable();

    @Override
    public void onLoad(Popmap p) {
        if (!compositeDisposable.isDisposed(p)) return;
        compositeDisposable.add("progress", downloader.listenProgress().subscribe(d -> myRecyclerViewAdapter.updateItemProgress(d)));
        compositeDisposable.add(p, fetchPopmap(p).subscribeWith(new DisposableSingleObserver<List<String>>() {
            @Override
            public void onSuccess(List<String> strings) {
                compositeDisposable.dispose(p);
                Toast.makeText(getApplicationContext(), strings.toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Throwable e) {
                compositeDisposable.dispose(p);
                Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
            }
        }));
    }

    @Override
    public void onCancel(Popmap p) {
        compositeDisposable.dispose(p);
    }

    interface v53Service {
        @POST("pop_map_details")
        Observable<DetailsResponse> popMapDetails(@Body Popmap body);
    }


    private Single<List<String>> fetchPopmap(Popmap popmap) {
        OkHttpClient.Builder httpclient = new OkHttpClient.Builder();
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        httpclient.addInterceptor(logging);
        OkHttpClient client = httpclient.build();
        GsonBuilder gsonBuilder = new GsonBuilder();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://popguide-staging.herokuapp.com/api/v53/")
                // add gson here if you want to use custom date,etc
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gsonBuilder.create()))
                .client(client)
                .build();


        return retrofit.create(v53Service.class)
                .popMapDetails(popmap)
                .subscribeOn(Schedulers.io())
                .map(response -> {
                    popmap.setDownloadables(response.popMapDetails.getDownloadables());
                    return response.popMapDetails;
                })
                .flatMapIterable(popMapDetails -> popMapDetails.getDownloadables())
                .distinct(d -> d.getUrl())
                .flatMap(d -> downloader.download(d))
                .toList()
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        downloader = new Downloader();
        downloader.setFilesDir(getFilesDir());

        myRecyclerViewAdapter = new MyRecyclerViewAdapter();
        RecyclerView recyclerView = findViewById(R.id.links);
        recyclerView.setAdapter(myRecyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        List<Popmap> testPackages = getTestPackages();
        myRecyclerViewAdapter.addPackages(testPackages);
        myRecyclerViewAdapter.setItemListener(this);


    }

    private ArrayList<Popmap> getTestPackages() {
        ArrayList<Popmap> packages = new ArrayList<>();
        Popmap p = new Popmap();
        p.setSecretToken("be7df5f7bade7cacadc5deed67f259bd96211ed805f008f2f39567a795a430495ab0757f97da1acd733743db4f255fe97a54baf9bab4f0975966a5109d2a5dcf");
        p.setInstallId("some_new_installation_id");
        p.setOsVersion("android");
        p.setAppVersion("72");
        p.setPop_map_id("586");
        p.setLanguage_id("5");
        packages.add(p);

        p = new Popmap();
        p.setSecretToken("be7df5f7bade7cacadc5deed67f259bd96211ed805f008f2f39567a795a430495ab0757f97da1acd733743db4f255fe97a54baf9bab4f0975966a5109d2a5dcf");
        p.setInstallId("some_new_installation_id");
        p.setOsVersion("android");
        p.setAppVersion("72");
        p.setPop_map_id("586");
        p.setLanguage_id("1");
        packages.add(p);

        p = new Popmap();
        p.setSecretToken("be7df5f7bade7cacadc5deed67f259bd96211ed805f008f2f39567a795a430495ab0757f97da1acd733743db4f255fe97a54baf9bab4f0975966a5109d2a5dcf");
        p.setInstallId("some_new_installation_id");
        p.setOsVersion("android");
        p.setAppVersion("72");
        p.setPop_map_id("586");
        p.setLanguage_id("2");
        packages.add(p);

        p = new Popmap();
        p.setSecretToken("be7df5f7bade7cacadc5deed67f259bd96211ed805f008f2f39567a795a430495ab0757f97da1acd733743db4f255fe97a54baf9bab4f0975966a5109d2a5dcf");
        p.setInstallId("some_new_installation_id");
        p.setOsVersion("android");
        p.setAppVersion("72");
        p.setPop_map_id("586");
        p.setLanguage_id("3");
        packages.add(p);

        p = new Popmap();
        p.setSecretToken("be7df5f7bade7cacadc5deed67f259bd96211ed805f008f2f39567a795a430495ab0757f97da1acd733743db4f255fe97a54baf9bab4f0975966a5109d2a5dcf");
        p.setInstallId("some_new_installation_id");
        p.setOsVersion("android");
        p.setAppVersion("72");
        p.setPop_map_id("586");
        p.setLanguage_id("4");
        packages.add(p);

        return packages;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
    }
}
