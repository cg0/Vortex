package uk.cg0.vortex.webserver.handle

import uk.cg0.vortex.webserver.enum.HttpVerb
import uk.cg0.vortex.webserver.enum.HttpVersion
import uk.cg0.vortex.webserver.objects.Request
import uk.cg0.vortex.webserver.objects.Response
import java.io.ByteArrayOutputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter

class HttpHandler() {

    fun handleInput(inputStreamReader: InputStreamReader): Request {
        var parsingState = HttpParsingState.HTTP_VERB
        var temp = ""
        var tempHeaderName = ""

        var httpVerb = HttpVerb.UNKNOWN
        var httpVersion = HttpVersion.UNKNOWN
        var route = ""
        var getParamsString = ""
        val headers = HashMap<String, String>()
        var getParams = HashMap<String, String>()
        val outputStream = ByteArrayOutputStream()

        while (inputStreamReader.ready()) {
            val newChar = inputStreamReader.read().toChar()
            when (parsingState) {
                HttpParsingState.HTTP_VERB -> {
                    if (newChar == ' ') {
                        httpVerb = HttpVerb.getByValue(temp.toUpperCase())
                        temp = ""
                        parsingState = HttpParsingState.HTTP_ROUTE
                    } else {
                        temp += newChar
                    }
                }
                HttpParsingState.HTTP_ROUTE -> {
                    when (newChar) {
                        ' ' -> {
                            route = temp
                            temp = ""
                            parsingState = HttpParsingState.HTTP_VERSION
                        }
                        '?' -> {
                            parsingState = HttpParsingState.HTTP_GET
                        }
                        else -> {
                            temp += newChar
                        }
                    }
                }
                HttpParsingState.HTTP_GET -> {
                    if (newChar == ' ') {
                        parsingState = HttpParsingState.HTTP_VERSION
                        getParams = UrlEncodeHandler().handleRead(getParamsString)
                    } else {
                        getParamsString += newChar
                    }
                }
                HttpParsingState.HTTP_VERSION -> {
                    if (newChar == '\n') {
                        httpVersion = HttpVersion.getByValue(temp.toUpperCase().trim())
                        temp = ""
                        parsingState = HttpParsingState.HEADER_KEY
                    } else {
                        temp += newChar
                    }
                }
                HttpParsingState.HEADER_KEY -> {
                    when (newChar) {
                        '\n' -> {
                            // End of headers
                            temp = ""
                            parsingState = HttpParsingState.BODY
                        }
                        ':' -> {
                            tempHeaderName = temp
                            temp = ""
                            parsingState = HttpParsingState.HEADER_VALUE
                        }
                        else -> {
                            temp += newChar
                        }
                    }
                }
                HttpParsingState.HEADER_VALUE -> {
                    if (newChar == '\n') {
                        headers[tempHeaderName] = temp.trim()
                        temp = ""
                        parsingState = HttpParsingState.HEADER_KEY
                    } else {
                        temp += newChar
                    }
                }
                HttpParsingState.BODY -> {
                    outputStream.write(newChar.toInt())
                }
            }
        }

        return Request(httpVerb, route, httpVersion, headers, getParams, outputStream.toByteArray(), HashMap())
    }

    fun handleOutput(writer: OutputStreamWriter, response: Response) {
        writer.write("HTTP/1.1 ${response.statusCode}\r\n")
        for (header in response.headers.keys) {
            writer.write("$header: ${response.headers[header]}\r\n")
        }

        writer.write("\r\n")
        writer.write(response.outputStream.toString())
    }

    enum class HttpParsingState {
        HTTP_VERB, // GET, POST, HEAD
        HTTP_ROUTE, // /index for example
        HTTP_GET, // If route has GET params
        HTTP_VERSION, // HTTP/1.1
        HEADER_KEY,
        HEADER_VALUE,
        BODY,
    }
}