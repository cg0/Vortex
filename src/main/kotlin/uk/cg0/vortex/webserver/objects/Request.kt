package uk.cg0.vortex.webserver.objects

import uk.cg0.vortex.webserver.enum.HttpContentType
import uk.cg0.vortex.webserver.enum.HttpVerb
import uk.cg0.vortex.webserver.enum.HttpVersion
import uk.cg0.vortex.webserver.handle.UrlEncodeHandler
import java.io.ByteArrayInputStream
import java.io.InputStreamReader

data class Request(val httpVerb: HttpVerb,
                   val route: String,
                   val httpVersion: HttpVersion,
                   val headers: HashMap<String, String>,
                   val get: HashMap<String, String>,
                   val postData: ByteArray) {

    val body: String by lazy {
        val reader = InputStreamReader(ByteArrayInputStream(postData))
        val data = reader.readText()
        reader.close()
        data
    }

    val post: HashMap<String, String> by lazy {
        if (headers["Content-Type"] == HttpContentType.APPLICATION_X_WWW_FORM_URLENCODED.toString()) {
            UrlEncodeHandler().handleRead(body)
        } else {
            throw NotImplementedError("POST data reading for content type ${headers["Content-Type"]}")
        }
    }
}
