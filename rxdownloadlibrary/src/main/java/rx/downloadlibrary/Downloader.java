package rx.downloadlibrary;

import android.util.Log;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.mindorks.nybus.NYBus;
import com.mindorks.nybus.annotation.Subscribe;
import com.mindorks.nybus.thread.NYThread;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.Okio;
import retrofit2.Response;
import retrofit2.Retrofit;

public class Downloader {

    private RetrofitService downloadService;
    private ProgressListener listener;

    private List<Disposable> subscritions = new ArrayList<>();

    public Downloader() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://google.com")
                .client(new OkHttpClient.Builder()
                        .addNetworkInterceptor(new DownloadProgressInterceptor())
                        .build())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        downloadService = retrofit.create(RetrofitService.class);
    }


    public void registerListener(ProgressListener l) {
        NYBus.get().register(this);
        this.listener = l;
    }

    public void unRegisterListener() {
        NYBus.get().unregister(this);
        this.listener = null;
    }


    @Subscribe(threadType = NYThread.MAIN)
    public void onEvent(ProgressEvent event) {
        if (listener != null) listener.onProgress(event);
    }

    public void downloadZipFile(String url, File destination) {
        // we register our progress subscriber to the Bus
        // and we only listen for ProgressEvent class events
        downloadService.downloadFile(url)
                .subscribeOn(Schedulers.io())
                .flatMap(saveFile(destination))
                .flatMap(unPackZip()).subscribe(handleResult());
    }

    public void downloadFile(String url, File destination) {
        // we register our progress subscriber to the Bus
        // and we only listen for ProgressEvent class events
        downloadService.downloadFile(url)
                .subscribeOn(Schedulers.io())
                .flatMap(saveFile(destination))
                .subscribe(handleResult());
    }

    public void downloadFileTgz(String url, File destination) {
        // we register our progress subscriber to the Bus
        // and we only listen for ProgressEvent class events
        downloadService.downloadFile(url)
                .subscribeOn(Schedulers.io())
                .flatMap(saveFile(destination))
                .flatMap(unPackTgz())
                .subscribe(handleResult());
    }


    private Function<? super File, ? extends ObservableSource<File>> unPackZip() {
        return new Function<File, ObservableSource<File>>() {
            @Override
            public ObservableSource<File> apply(File file) throws Exception {
                InputStream is;
                ZipInputStream zis;
                String parentFolder;
                String filename;
                try {
                    parentFolder = file.getParentFile().getPath();

                    is = new FileInputStream(file.getAbsolutePath());
                    zis = new ZipInputStream(new BufferedInputStream(is));
                    ZipEntry ze;
                    byte[] buffer = new byte[1024];
                    int count;

                    while ((ze = zis.getNextEntry()) != null) {
                        filename = ze.getName();

                        if (ze.isDirectory()) {
                            File fmd = new File(parentFolder + "/" + filename);
                            fmd.mkdirs();
                            continue;
                        }

                        FileOutputStream fout = new FileOutputStream(parentFolder + "/" + filename);

                        while ((count = zis.read(buffer)) != -1) {
                            fout.write(buffer, 0, count);
                        }

                        fout.close();
                        zis.closeEntry();
                    }

                    zis.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }


                File extractedFile = new File(file.getAbsolutePath().replace(".zip", ""));
                if (!file.delete()) Log.d("unpackZip", "Failed to deleted the zip file.");
                return Observable.just(extractedFile);
            }
        };
    }

    private Function<? super File, ? extends ObservableSource<File>> unPackTgz() {
        return new Function<File, ObservableSource<File>>() {
            @Override
            public ObservableSource<File> apply(File file) {

                //TODO unzip tgz file

                return Observable.just(file);
            }
        };
    }

    private Observer<File> handleResult() {
        return new Observer<File>() {
            @Override
            public void onSubscribe(Disposable d) {
                subscritions.add(d);
                Log.d("OnSubscribe", "OnSubscribe");
            }

            @Override
            public void onNext(File file) {
                Log.d("OnNext", "File downloaded to " + ((File) file).getAbsolutePath());
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                Log.d("Error", "Error " + e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.d("OnComplete", "onCompleted");
            }
        };
    }

    private Function<? super Response<ResponseBody>, ? extends ObservableSource<File>> saveFile(final File destination) {
        return new Function<Response<ResponseBody>, ObservableSource<File>>() {
            @Override
            public ObservableSource<File> apply(Response<ResponseBody> responseBodyResponse) {
                try {
                    BufferedSink sink = Okio.buffer(Okio.sink(destination));
                    sink.writeAll(responseBodyResponse.body().source());
                    sink.close();

                    return Observable.just(destination);
                } catch (IOException e) {
                    return Observable.error(e);
                }
            }
        };
    }

    public void cancel() {
        if (subscritions != null) {
            for (Disposable d : subscritions) {
                d.dispose();
                subscritions.remove(d);
            }
        }
    }
}