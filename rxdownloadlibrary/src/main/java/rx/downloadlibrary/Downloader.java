package rx.downloadlibrary;

import android.util.Log;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.Okio;
import retrofit2.Response;
import retrofit2.Retrofit;
import rx.RxBus;

public class Downloader {

    private RetrofitService downloadService;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

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

    public void downloadZipFile(String url, File destination) {
        // we register our progress subscriber to the Bus
        // and we only downloadSubscriber for DownloadEvent class events
        compositeDisposable.add(downloadService.downloadFile(url)
                .subscribeOn(Schedulers.io())
                .flatMap(saveFile(destination))
                .flatMap(unPackZip())
                .subscribeWith(handleResult()));
    }

    public void downloadFile(String url, File destination) {
        // we register our progress subscriber to the Bus
        // and we only downloadSubscriber for DownloadEvent class events
        compositeDisposable.add(downloadService.downloadFile(url)
                .subscribeOn(Schedulers.io())
                .flatMap(saveFile(destination))
                .subscribeWith(handleResult()));
    }

    public void downloadFileTgz(String url, File destination) {
        // we register our progress subscriber to the Bus
        // and we only downloadSubscriber for DownloadEvent class events
        compositeDisposable.add(downloadService.downloadFile(url)
                .subscribeOn(Schedulers.io())
                .flatMap(saveFile(destination))
                .flatMap(unPackTgz())
                .subscribeWith(handleResult()));
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
            public ObservableSource<File> apply(File tarGz) {
                return Observable.just(new Unarchiver(tarGz, true).unarchive());
            }
        };
    }

    private DisposableObserver<File> handleResult() {
        return new DisposableObserver<File>() {

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


    public void subscribe(Consumer<DownloadEvent> consumer) {
        Disposable disposable = RxBus.listen(DownloadEvent.class).observeOn(AndroidSchedulers.mainThread()).subscribe(consumer);
        compositeDisposable.add(disposable);
    }


    public void dispose() {
        compositeDisposable.dispose();
    }

    public void dispose(String url) {
//        compositeDisposable.dispose();
//        DisposableManager.dispose(url);
    }
}