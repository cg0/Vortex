package uk.cg0.vortex.template

import kotlinx.html.*
import kotlinx.html.stream.createHTML
import uk.cg0.vortex.Vortex
import uk.cg0.vortex.webserver.enum.HttpStatus

class InternalServerErrorTemplate(): Template {
    override fun render(): String {
        return createHTML().html {
            head {
                title(HttpStatus.INTERNAL_SERVER_ERROR.toString())
            }
            body {
                h1 { +HttpStatus.INTERNAL_SERVER_ERROR.toString() }
                p {
                    +"The requested resource threw an error and could not continue"
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