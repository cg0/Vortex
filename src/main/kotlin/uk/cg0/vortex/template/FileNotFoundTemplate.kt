package uk.cg0.vortex.template

import kotlinx.html.*
import kotlinx.html.stream.createHTML
import uk.cg0.vortex.webserver.enum.HttpStatus

class FileNotFoundTemplate: Template {
    override fun render(): String {
        return createHTML().html {
            head {
                title(HttpStatus.NOT_FOUND.toString())
            }
            body {
                h1 { +HttpStatus.NOT_FOUND.toString() }
                p {
                    +"The requested resource could not be found on this server"
                }
                hr()
                p {
                    +"Vortex | "
                    a("https://github.com/cg0/Vortex") {
                        +"Github"
                    }
                }
            }
        }
    }
}