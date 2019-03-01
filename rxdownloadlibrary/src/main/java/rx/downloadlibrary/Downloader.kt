package rx.downloadlibrary

import android.net.Uri
import android.util.Log
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okio.Okio
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url
import rx.RxBus
import java.io.File
import java.io.IOException


class Downloader {

    private val compositeDisposable: CustomCompositeDisposable = CustomCompositeDisposable()
    private val downloadService: RetrofitService
    private lateinit var filesDir: File

    interface RetrofitService {
        @GET
        @Streaming
        fun downloadFile(@Url url: String): Observable<Response<ResponseBody>>
    }

    init {
        val retrofit = Retrofit.Builder()
                .baseUrl("https://google.com")
                .client(OkHttpClient.Builder()
                        .addNetworkInterceptor(DownloadProgressInterceptor())
                        .build())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
        downloadService = retrofit.create(RetrofitService::class.java)
    }

    fun setFilesDir(filesDir: File) {
        this.filesDir = filesDir
    }


    fun downloadList(tag: String, downloadables: List<Downloadable>) {
        if (!compositeDisposable.isDisposed(tag)) return
        compositeDisposable.add(tag, Observable.fromIterable(downloadables)
                .subscribeOn(Schedulers.single())
                .flatMap { d -> downloadService.downloadFile(d.downloadUrl) }
                .doOnError { Log.e("error", "" + it.message) }
                .map { response -> saveSource(response) }
                .subscribe({ name -> print("name$name") }, { error -> print("error$error") }))
    }


    fun download(downloadable: Downloadable) {
        val url = downloadable.downloadUrl
        if (!compositeDisposable.isDisposed(url)) return
        compositeDisposable.add(url, downloadService.downloadFile(url)
                .subscribeOn(Schedulers.single())
                .map { response -> saveSource(response) }
                .subscribe({ name -> print("name$name") }, { error -> print("error$error") }))
    }

    private fun saveSource(response: Response<ResponseBody>): String? {
        var name: String? = null
        try {
            name = Uri.parse(response.raw().request().url().toString()).lastPathSegment
            val sink = Okio.buffer(Okio.sink(File(filesDir!!.toString() + name)))
            sink.writeAll(response.body().source())
            sink.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return name
    }


    fun listenProgress(consumer: Consumer<Downloadable>) {
        RxBus.listen(Downloadable::class.java).observeOn(AndroidSchedulers.mainThread()).subscribe(consumer)
    }

    fun cancel(downloadable: Downloadable) {
        val url = downloadable.downloadUrl
        compositeDisposable.dispose(url)
    }

    fun dispose() {
        compositeDisposable.dispose()
    }
}