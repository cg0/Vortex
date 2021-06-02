package uk.cg0.vortex.webserver.objects

import uk.cg0.vortex.template.Template
import uk.cg0.vortex.webserver.enum.HttpContentType
import uk.cg0.vortex.webserver.enum.HttpStatus
import java.io.ByteArrayOutputStream
import java.nio.charset.Charset

class Response {
    var statusCode = HttpStatus.OK
    val outputStream = ByteArrayOutputStream()
    val headers = HashMap<String, String>()
    var contentType = HttpContentType.TEXT_PLAIN

    fun writeString(string: String) {
        outputStream.write(string.toByteArray(Charset.forName("UTF-8")))
    }

    fun renderView(template: Template) {
        outputStream.write(template.render().toByteArray(Charset.forName("UTF-8")))
        contentType = HttpContentType.TEXT_HTML
    }
}