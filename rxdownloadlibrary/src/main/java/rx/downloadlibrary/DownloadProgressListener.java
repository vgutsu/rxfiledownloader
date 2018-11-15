package rx.downloadlibrary;

public interface DownloadProgressListener {
    void update(String url, long bytesRead, long contentLength, boolean done);
}