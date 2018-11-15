package rx.downloadlibrary;


import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;
import rx.RxBus;

public class DownloadProgressInterceptor implements Interceptor, DownloadProgressListener {

    public DownloadProgressInterceptor() {}

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());
        return originalResponse.newBuilder()
                .body(new DownloadProgressResponseBody(originalResponse.request().url().toString(), originalResponse.body(), this))
                .build();
    }

    @Override
    public void update(String url, long bytesRead, long contentLength, boolean done) {
        RxBus.publish(DownloadEvent.newEvent(url, contentLength, bytesRead));
    }
}