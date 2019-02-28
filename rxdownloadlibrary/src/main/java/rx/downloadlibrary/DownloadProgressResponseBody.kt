package rx.downloadlibrary

import okhttp3.MediaType
import okhttp3.ResponseBody
import okio.*
import java.io.IOException

class DownloadProgressResponseBody(private val url: String, private val responseBody: ResponseBody, private val progressListener: DownloadProgressListener?) : ResponseBody() {

    private var bufferedSource: BufferedSource? = null

    override fun contentType(): MediaType? {
        return responseBody.contentType()
    }

    override fun contentLength(): Long {
        return responseBody.contentLength()
    }

    override fun source(): BufferedSource? {
        if (bufferedSource == null) {
            bufferedSource = Okio.buffer(source(responseBody.source()))
        }
        return bufferedSource
    }

    private fun source(source: Source): Source {
        return object : ForwardingSource(source) {
            var totalBytesRead = 0L

            override fun read(sink: Buffer, byteCount: Long): Long {
                try {
                    val bytesRead = super.read(sink, byteCount)
                    if (bytesRead > -1) {
                        totalBytesRead += bytesRead
                    }
                    //                    bytesRead == -1
                    progressListener?.update(url, totalBytesRead, responseBody.let { contentLength() })
                    return bytesRead
                } catch (e: IOException) {
                    // ignored
                }
                return -1
            }
        }

    }
}
