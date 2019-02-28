package rx.downloadlibrary

interface DownloadProgressListener {
    fun update(url: String, bytesRead: Long, contentLength: Long)
}