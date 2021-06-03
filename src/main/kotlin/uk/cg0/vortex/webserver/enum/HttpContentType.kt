package uk.cg0.vortex.webserver.enum

enum class HttpContentType(private val value: String) {
    APPLICATION_OCTET_STREAM("application/octet-stream"),
    APPLICATION_X_WWW_FORM_URLENCODED("application/x-www-form-urlencoded"),
    TEXT_PLAIN("text/plain"),
    TEXT_HTML("text/html"),
    IMAGE_PNG("image/png"),
    IMAGE_JPG("image/jpg"),
    IMAGE_JPEG("image/jpeg");

    override fun toString(): String {
        return this.value
    }

    companion object {
        fun getByValue(value: String): HttpContentType {
            for (contentType in HttpContentType.values()) {
                if (contentType.value == value) {
                    return contentType
                }
            }

            return APPLICATION_OCTET_STREAM
        }
    }
}