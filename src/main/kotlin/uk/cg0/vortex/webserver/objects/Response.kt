package uk.cg0.vortex.webserver.objects

import uk.cg0.vortex.webserver.enum.HttpStatus
import java.io.ByteArrayOutputStream
import java.nio.charset.Charset

class Response {
    var statusCode = HttpStatus.OK
    val outputStream = ByteArrayOutputStream()
    val headers = HashMap<String, String>()

    init {
        headers["Server"] = "Vortex"
        headers["Content-Type"] = "text/html"
    }

    fun writeString(string: String) {
        outputStream.write(string.toByteArray(Charset.forName("UTF-8")))
    }
}