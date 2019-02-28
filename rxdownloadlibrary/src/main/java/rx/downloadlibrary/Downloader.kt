package rx.downloadlibrary

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import io.reactivex.Single
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
        fun downloadFile(@Url url: String): Single<Response<ResponseBody>>
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

    fun download(downloadable: Downloadable) {
        val url = downloadable.downloadUrl
        if (!compositeDisposable.isDisposed(url)) return
        compositeDisposable.add(url, downloadService.downloadFile(downloadable.downloadUrl)
                .subscribeOn(Schedulers.io())
                .map {
                    responseBodyResponse ->
                    try {
                        val sink = Okio.buffer(Okio.sink(File(filesDir!!.toString() + downloadable.destinationName!!)))
                        sink.writeAll(responseBodyResponse.body().source())
                        sink.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }.subscribe())
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