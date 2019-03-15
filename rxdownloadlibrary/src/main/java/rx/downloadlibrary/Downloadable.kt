package rx.downloadlibrary

class Downloadable {
    var url: String
    var byteRead: Long = 0
    var contentLength: Long = 0

    constructor(url: String) {
        this.url = url
    }

    override fun equals(obj: Any?): Boolean {
        return url == (obj as Downloadable).url
    }
}