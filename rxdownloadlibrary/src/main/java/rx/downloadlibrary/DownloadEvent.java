package rx.downloadlibrary;

public class DownloadEvent {
    public enum Type {
        DOWNLOAD,
        CANCEL
    }

    Type type;
    String downloadUrl;
    int progress;

    public DownloadEvent(String url, long contentLength, long bytesRead) {
        this.progress = (int) (bytesRead / (contentLength / 100f));
        this.downloadUrl = url;
        this.type = Type.DOWNLOAD;
    }

    public DownloadEvent(String url, Type type) {
        this.progress = 0;
        this.downloadUrl = url;
        this.type = type;
    }

    public void update(DownloadEvent event) {
        this.type = event.getType();
        this.progress = event.getProgress();
        this.downloadUrl = event.getDownloadUrl();
    }

    public static DownloadEvent newEvent(String url, long contentLength, long bytesRead) {
        return new DownloadEvent(url, contentLength, bytesRead);
    }

    public static DownloadEvent newEvent(String url, Type download) {
        return new DownloadEvent(url, download);
    }


    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public int getProgress() {
        return progress;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    @Override
    public String toString() {
        return String.format("%s %d%%", getDownloadUrl(), getProgress());
    }

    @Override
    public boolean equals(Object obj) {
        return downloadUrl.equals(((DownloadEvent) obj).downloadUrl);
    }
}