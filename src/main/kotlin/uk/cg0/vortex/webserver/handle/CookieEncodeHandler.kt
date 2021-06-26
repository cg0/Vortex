package uk.cg0.vortex.webserver.handle

class CookieEncodeHandler {
    fun handleRead(postString: String): HashMap<String, String> {
        val post = HashMap<String, String>()
        var key = ""
        var value = ""

        var parsingState = CookieParsingState.KEY

        for (char in postString) {
            when (parsingState) {
                CookieParsingState.KEY -> {
                    if (char == '=') {
                        parsingState = CookieParsingState.VALUE
                    } else {
                        key += char
                    }
                }
                CookieParsingState.VALUE -> {
                    if (char == ';') {
                        parsingState = CookieParsingState.DELIMITER
                        post[key] = value
                        key = ""
                        value = ""
                    } else {
                        value += char
                    }
                }
                CookieParsingState.DELIMITER -> {
                    if (char == ' ') {
                        parsingState = CookieParsingState.KEY
                    }
                }
            }
        }

        if (key.isNotEmpty()) {
            post[key] = value
        }

        return post
    }

    enum class CookieParsingState {
        KEY,
        VALUE,
        DELIMITER,
    }
}