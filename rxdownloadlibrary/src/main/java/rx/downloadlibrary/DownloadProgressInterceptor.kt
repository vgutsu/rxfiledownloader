package rx.downloadlibrary


import okhttp3.Interceptor
import okhttp3.Response
import rx.RxBus

class DownloadProgressInterceptor : Interceptor, DownloadProgressListener {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalResponse = chain.proceed(chain.request())
        val url = chain.request().url().toString()
        return originalResponse.newBuilder()
                .body(DownloadProgressResponseBody(url, originalResponse.body(), this))
                .build()
    }

    override fun update(url: String, bytesRead: Long, contentLength: Long) {
        val downloadable = Downloadable(url)
        downloadable.contentLength = contentLength
        downloadable.byteRead = bytesRead
        RxBus.publish(downloadable)
    }
}