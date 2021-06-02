package uk.cg0.vortex.webserver.handle

class UrlEncodeHandler {
    fun handleRead(postString: String): HashMap<String, String> {
        val post = HashMap<String, String>()
        var key = ""
        var value = ""

        var parsingState = PostParsingState.KEY

        for (char in postString) {
            when (parsingState) {
                PostParsingState.KEY -> {
                    if (char == '=') {
                        parsingState = PostParsingState.VALUE
                    } else {
                        key += char
                    }
                }
                PostParsingState.VALUE -> {
                    if (char == '&') {
                        parsingState = PostParsingState.KEY
                        post[key] = value
                        key = ""
                        value = ""
                    } else {
                        value += char
                    }
                }
            }
        }

        post[key] = value

        return post
    }

    enum class PostParsingState {
        KEY,
        VALUE,
    }
}