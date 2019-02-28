package rx.downloadlibrary

import android.net.Uri

class Downloadable {
    var downloadUrl: String
    var progress: Int = 0
    var destinationName: String? = null
    var type: Type

    enum class Type {
        DOWNLOAD,
        CANCEL
    }

    constructor(url: String, contentLength: Long, bytesRead: Long) {
        this.progress = (bytesRead / (contentLength / 100f)).toInt()
        this.type = Type.DOWNLOAD
        this.downloadUrl = url
        this.destinationName = Uri.parse(downloadUrl).lastPathSegment
    }

    constructor(url: String) {
        this.progress = 0
        this.type = Type.DOWNLOAD
        this.downloadUrl = url
        this.destinationName = Uri.parse(downloadUrl).lastPathSegment
    }

    override fun toString(): String {
        return String.format("%s %d%%", downloadUrl, progress)
    }

    override fun equals(obj: Any?): Boolean {
        return downloadUrl == (obj as Downloadable).downloadUrl
    }

    companion object {

        fun newEvent(url: String, contentLength: Long, bytesRead: Long): Downloadable {
            return Downloadable(url, contentLength, bytesRead)
        }

        fun newEvent(url: String): Downloadable {
            return Downloadable(url)
        }
    }
}