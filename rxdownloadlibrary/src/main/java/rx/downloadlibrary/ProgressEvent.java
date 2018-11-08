package rx.downloadlibrary;

public class ProgressEvent {
    final int progress;
    final long contentLength;
    final String downloadIdentifier;
    final long bytesRead;

    public ProgressEvent(String identifier, long contentLength, long bytesRead) {
        this.contentLength = contentLength;
        this.progress = (int) (bytesRead / (contentLength / 100f));
        downloadIdentifier = identifier;
        this.bytesRead = bytesRead;
    }

    public int getProgress() {
        return progress;
    }

    public String getDownloadIdentifier() {
        return downloadIdentifier;
    }

    public long getBytesRead() {
        return bytesRead;
    }

    public boolean percentIsAvailable() {
        return contentLength > 0;
    }
}