package uk.cg0.vortex.webserver.enum

enum class HttpContentType(private val value: String) {
    APPLICATION_OCTET_STREAM("application/octet-stream"),
    APPLICATION_X_WWW_FORM_URLENCODED("application/x-www-form-urlencoded"),
    TEXT_PLAN("text/plain"),
    TEXT_HTML("text/html"),
    IMAGE_PNG("image/png");

    override fun toString(): String {
        return this.value
    }
}