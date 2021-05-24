package uk.cg0.vortex.webserver.thread

import uk.cg0.vortex.Vortex
import uk.cg0.vortex.webserver.handle.HttpHandler
import uk.cg0.vortex.webserver.objects.Response
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.Socket

class HttpParserThread(private val socket: Socket): Runnable {
    override fun run() {
        val reader = InputStreamReader(socket.getInputStream())
        val parser = HttpHandler()
        val request = parser.handleInput(reader)

        val writer = OutputStreamWriter(socket.getOutputStream())
        val route = Vortex.getRoute(request.route)
        val response = Response()
        route.handleRoute(request, response)
        response.headers["Content-Length"] = response.outputStream.size().toString()
        parser.handleOutput(writer, response)

        writer.close()
        reader.close()
        socket.close()
    }
}