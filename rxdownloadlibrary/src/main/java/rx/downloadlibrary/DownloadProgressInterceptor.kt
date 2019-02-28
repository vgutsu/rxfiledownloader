package rx.downloadlibrary


import okhttp3.Interceptor
import okhttp3.Response
import rx.RxBus

class DownloadProgressInterceptor : Interceptor, DownloadProgressListener {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalResponse = chain.proceed(chain.request())
        return originalResponse.newBuilder()
                .body(DownloadProgressResponseBody(originalResponse.request().url().toString(), originalResponse.body(), this))
                .build()
    }

    override fun update(url: String, bytesRead: Long, contentLength: Long) {
        RxBus.publish(Downloadable.newEvent(url, contentLength, bytesRead))
    }
}