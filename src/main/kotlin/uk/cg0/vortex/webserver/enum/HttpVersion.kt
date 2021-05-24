package uk.cg0.vortex.webserver.enum

enum class HttpVersion {
    HTTP_1_0,
    HTTP_1_1,
    HTTP_2_0,
    UNKNOWN;

    companion object {
        fun getByValue(key: String): HttpVersion {
            return when (key) {
                "HTTP/1.0" -> HTTP_1_0
                "HTTP/1.1" -> HTTP_1_1
                "HTTP/2.0" -> HTTP_2_0
                else -> UNKNOWN
            }
        }
    }
}
