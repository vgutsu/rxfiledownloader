package rx.downloadlibrary

import android.net.Uri
import android.util.Log
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okio.Okio
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url
import rx.RxBus
import java.io.File


class Downloader {

    private val downloadService: RetrofitService
    private lateinit var parentDir: File

    interface RetrofitService {
        @GET
        @Streaming
        fun downloadFile(@Url url: String): Observable<ResponseBody>
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
        this.parentDir = filesDir
    }


    fun listenProgress(): Observable<Downloadable>? {
        return RxBus.listen(Downloadable::class.java).observeOn(AndroidSchedulers.mainThread())
    }

    fun download(d: Downloadable): Observable<String?> {
        return downloadService.downloadFile(d.url)
                .subscribeOn(Schedulers.io())
                .filter { !File(parentDir!!.toString() + Uri.parse(d.url).lastPathSegment).exists() }
                .map { r -> saveSource(r, filePath = parentDir!!.toString() + Uri.parse(d.url).lastPathSegment) }
                .doOnError { error -> showError(error) }
    }

    private fun saveSource(response: ResponseBody, filePath: String): String? {
        val file = File(filePath);
        try {
            val sink = Okio.buffer(Okio.sink(file))
            sink.writeAll(response.source())
            sink.close()
        } catch (e: Exception) {
            if (file.exists()) file.delete()
            Log.e("save", e.message)
        }
        return filePath
    }

    private fun showError(error: Throwable) {
        Log.e("error", "" + error.message)
    }
}