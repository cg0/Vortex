package uk.cg0.vortex.test

import uk.cg0.vortex.template.Template
import kotlinx.html.*
import kotlinx.html.stream.createHTML

class HomeTemplate(val name: String): Template {
    override fun render(): String {
        return createHTML().html {
            head {
                title("Home page test")
            }
            body {
                h1 {
                    +"This is a test for an end to end test of Vortex MVC"
                }
                p {
                    +"The first value of the test table has been passed and the name is $name"
                }
            }
        }
    }
}