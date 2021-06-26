package uk.cg0.vortex.webserver.objects

import uk.cg0.vortex.template.Template
import uk.cg0.vortex.webserver.enum.HttpContentType
import uk.cg0.vortex.webserver.enum.HttpStatus
import java.io.ByteArrayOutputStream
import java.io.File
import java.nio.charset.Charset
import java.nio.file.Files

data class Response(var statusCode: HttpStatus,
                    val headers: HashMap<String, String>,
                    var contentType: HttpContentType,
                    var cookies: HashMap<String, String>) {
    constructor(): this(HttpStatus.OK, HashMap(), HttpContentType.TEXT_PLAIN, HashMap())

    val outputStream = ByteArrayOutputStream()

    fun writeString(string: String) {
        outputStream.write(string.toByteArray(Charset.forName("UTF-8")))
    }

    fun renderView(template: Template) {
        outputStream.write(template.render().toByteArray(Charset.forName("UTF-8")))
        contentType = HttpContentType.TEXT_HTML
    }

    fun serveFile(file: File) {
        val mimeType = Files.probeContentType(file.toPath())
        contentType = HttpContentType.getByValue(mimeType)
        outputStream.write(file.readBytes())
    }
}