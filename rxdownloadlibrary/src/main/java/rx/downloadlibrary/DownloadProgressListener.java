package rx.downloadlibrary;

public interface DownloadProgressListener {
    void update(String downloadIdentifier, long bytesRead, long contentLength, boolean done);
}