package uk.cg0.vortex.template

import kotlinx.html.*
import kotlinx.html.stream.createHTML
import uk.cg0.vortex.webserver.enum.HttpStatus

class GenericErrorTemplate(private val httpStatus: HttpStatus): Template {
    override fun render(): String {
        return createHTML().html {
            head {
                title(httpStatus.toString())
            }
            body {
                h1 { +httpStatus.toString() }
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