package uk.cg0.vortex.webserver.thread

import uk.cg0.vortex.Vortex
import uk.cg0.vortex.controller.respondable.Respondable
import uk.cg0.vortex.webserver.enum.HttpStatus
import uk.cg0.vortex.webserver.handle.HttpHandler
import uk.cg0.vortex.webserver.objects.Response
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.lang.Exception
import java.net.Socket

class HttpParserThread(private val socket: Socket): Runnable {
    override fun run() {
        lateinit var reader: InputStreamReader
        lateinit var writer: OutputStreamWriter
        try {
            reader = InputStreamReader(socket.getInputStream())
            val parser = HttpHandler()
            val request = parser.handleInput(reader)

            writer = OutputStreamWriter(socket.getOutputStream())
            val domain = request.headers["Host"]?.split(":")?.first() ?: "*"
            var controller = Vortex.routingEngine[domain, request.httpVerb, request.route]
            val response = Response()
            if (controller == null) {
                controller = Vortex.routingEngine.getError(domain, HttpStatus.NOT_FOUND)
            }

            try {
                val respondable = controller?.execute(request, response)
                if (respondable is Respondable) {
                    // If Respondable was returned
                    response.headers.clear()
                    response.outputStream.reset()

                    respondable.handleResponse(request, response)
                }
            } catch (exception: Exception) {
                controller = Vortex.routingEngine.getError(domain, HttpStatus.INTERNAL_SERVER_ERROR)
                controller?.execute(request, response)
            }

            response.headers["Content-Length"] = response.outputStream.size().toString()
            response.headers["Content-Type"] = response.contentType.toString()
            response.headers["Server"] = "Vortex"

            parser.handleOutput(writer, response)
        } catch (exception: Exception) {
            exception.printStackTrace()
        } finally {
            writer.close()
            reader.close()
            socket.close()
        }

    }
}