package rx.downloadlibrary

import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okio.Buffer
import okio.BufferedSink
import okio.ForwardingSource
import okio.Okio
import org.slf4j.LoggerFactory
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Streaming
import retrofit2.http.Url
import java.io.File
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap
import java.util.regex.Pattern

class FileDownloader(val baseUrl: String) {

    private val log = LoggerFactory.getLogger(FileDownloader::class.java)

    private val expectedFileLength = ConcurrentHashMap<String, Long>()
    private val eTag = ConcurrentHashMap<String, String>()

    private val apiChecker: FileDownloaderAPI

    init {
        apiChecker = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(OkHttpClient())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(FileDownloaderAPI::class.java)

    }


    /**
     *
     * @return File Observable
     */
    fun download(
            urlPath: String,
            file: File,
            dlProgressConsumer: Consumer<Int>): Observable<File> {
        return Observable.create(ObservableOnSubscribe<File> {
            val downloadObservable: Observable<Int>

            if (file.exists() &&
                    file.length() > 0L &&
                    file.length() != expectedFileLength[file.name]
                    ) {
                /**
                 * Try to get rest of the file according to:
                 * http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html
                 */
                downloadObservable = apiChecker.downloadFile(
                        urlPath,
                        "bytes=${file.length()}-",
                        eTag[file.name] ?: "0"
                ).flatMap(
                        DownloadFunction(file, it)
                )
            } else {
                /**
                 * Last time file was fully downloaded or not present at all
                 */
                if (!file.exists())
                    eTag[file.name] = ""

                downloadObservable = apiChecker.downloadFile(
                        urlPath,
                        eTag[file.name] ?: "0"
                ).flatMap(
                        DownloadFunction(file, it)
                )

            }

            downloadObservable
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(dlProgressConsumer)

        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    private inner class DownloadFunction(
            val file: File,
            val fileEmitter: ObservableEmitter<File>
    ) : Function<Response<ResponseBody>, Observable<Int>> {

        var contentLength = 0L

        var startingByte = 0L
        var endingByte = 0L
        var totalBytes = 0L


        var contentRangePattern = "bytes ([0-9]*)-([0-9]*)/([0-9]*)"
        fun parseContentRange(contentRange: String) {
            val matcher = Pattern.compile(contentRangePattern).matcher(contentRange)
            if (matcher.find()) {
                startingByte = matcher.group(1).toLong()
                endingByte = matcher.group(2).toLong()
                totalBytes = matcher.group(3).toLong()
            }
        }

        var totalRead = 0L

        var lastPercentage = 0

        override fun apply(response: Response<ResponseBody>): Observable<Int> {
            return Observable.create { subscriber ->
                try {
                    if (!response.isSuccessful) {
                        /**
                         * Including response 304 Not Modified
                         */
                        fileEmitter.onError(IllegalStateException("Code: ${response.code()}, ${response.message()}; Response $response"))
                        return@create
                    }

                    contentLength = response.body().contentLength()


                    log.info("{}", response)
                    /**
                     * Receiving partial content, which in general means that download is resumed
                     */
                    if (response.code() == 206) {
                        parseContentRange(response.headers().get("Content-Range"))
                        log.debug("Getting range from {} to {} of {} bytes", startingByte, endingByte, totalBytes)
                    } else {
                        endingByte = contentLength
                        totalBytes = contentLength
                        if (file.exists())
                            file.delete()
                    }

                    log.info("Starting byte: {}, ending byte {}", startingByte, endingByte)

                    totalRead = startingByte

                    eTag.put(file.name, response.headers().get("ETag"))
                    expectedFileLength.put(file.name, totalBytes)


                    val sink: BufferedSink
                    if (startingByte > 0) {
                        sink = Okio.buffer(Okio.appendingSink(file))
                    } else {
                        sink = Okio.buffer(Okio.sink(file))
                    }

                    sink.use {
                        it.writeAll(object : ForwardingSource(response.body().source()) {

                            override fun read(sink: Buffer, byteCount: Long): Long {
                                val bytesRead = super.read(sink, byteCount)

                                totalRead += bytesRead

                                /**
                                 * May not wok good if we get some shit from the middle of the file,
                                 * though that not the case of this function, as we plan only to
                                 * resume downloads
                                 */
                                val currentPercentage = (totalRead * 100 / totalBytes).toInt()
                                if (currentPercentage > lastPercentage) {
                                    val progress = "$currentPercentage%"
                                    lastPercentage = currentPercentage
                                    subscriber.onNext(currentPercentage)
                                    log.debug("Downloading {} progress: {}", file.name, progress)
                                }
                                return bytesRead
                            }
                        })
                    }

                    subscriber.onComplete()
                    fileEmitter.onNext(file)
                    fileEmitter.onComplete()
                } catch (e: IOException) {
                    log.error("Last percentage: {}, Bytes read: {}", lastPercentage, totalRead)
                    fileEmitter.onError(e)
                }
            }
        }

    }

    interface FileDownloaderAPI {


        @Streaming @GET
        fun downloadFile(
                @Url fileUrl: String,
                @Header("If-None-Match") eTag: String
        ): Observable<Response<ResponseBody>>

        @Streaming @GET
        fun downloadFile(
                @Url fileUrl: String,

                // https://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.35
                @Header("Range") bytesRange: String,

                // https://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.27
                @Header("If-Range") eTag: String
        ): Observable<Response<ResponseBody>>
    }
}