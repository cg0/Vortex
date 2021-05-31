package uk.cg0.vortex.webserver.thread

import uk.cg0.vortex.Vortex
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
            val controller = Vortex.routingEngine[request.httpVerb, request.route]
            val response = Response()
            if (controller == null) {
                response.statusCode = HttpStatus.NOT_FOUND
            } else {
                controller.execute(request, response)
            }
            response.headers["Content-Length"] = response.outputStream.size().toString()
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