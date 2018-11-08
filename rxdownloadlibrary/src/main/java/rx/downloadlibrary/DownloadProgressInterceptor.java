package rx.downloadlibrary;


import com.mindorks.nybus.NYBus;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;
import rx.RxBus;

public class DownloadProgressInterceptor implements Interceptor, DownloadProgressListener {

    public DownloadProgressInterceptor() {
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());
        return originalResponse.newBuilder()
                .body(new DownloadProgressResponseBody(originalResponse.body(), this))
                .build();
    }

    @Override
    public void update(String downloadIdentifier, long bytesRead, long contentLength, boolean done) {
        ProgressEvent progressEvent = new ProgressEvent(downloadIdentifier, contentLength, bytesRead);
//        NYBus.get().post(progressEvent);
        RxBus.publish(progressEvent);
    }
}