package rx.downloadlibrary

class Downloadable {
    var downloadUrl: String
    var progress: Int = 0
    var type: Type

    enum class Type {
        DOWNLOAD,
        CANCEL
    }

    constructor(url: String, contentLength: Long, bytesRead: Long) {
        this.progress = (bytesRead / (contentLength / 100f)).toInt()
        this.type = Type.DOWNLOAD
        this.downloadUrl = url
    }

    constructor(url: String) {
        this.progress = 0
        this.type = Type.DOWNLOAD
        this.downloadUrl = url
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