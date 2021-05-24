package uk.cg0.vortex.webserver.enum

enum class HttpVerb {
    GET,
    POST,
    HEAD,
    PUT,
    PATCH,
    DELETE,
    UNKNOWN;

    companion object {
        fun getByValue(key: String): HttpVerb {
            for (verb in values()) {
                if (verb.name == key) {
                    return verb
                }
            }

            return UNKNOWN
        }
    }
}