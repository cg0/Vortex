package uk.cg0.vortex.test

import uk.cg0.vortex.controller.Controller
import uk.cg0.vortex.webserver.objects.Request
import uk.cg0.vortex.webserver.objects.Response

class TestController: Controller {
    fun test(request: Request, response: Response) {
        val test = TestTable().select("*").where("id", "1").get().first()
        response.renderView(HomeTemplate(test["name"]!!))
    }
}