package uk.cg0.vortex.webserver.thread

import java.net.ServerSocket
import java.util.concurrent.Executors

/**
 * Basic thread listing to HTTP port and redirecting to a separate thread to be parsed
 */
class HttpServerThread: Runnable {
    private val threadPool = Executors.newFixedThreadPool(10)
    override fun run() {
        val server = ServerSocket(8080)

        while (true) {
            val socket = server.accept()
            threadPool.submit(HttpParserThread(socket))
        }
    }

}