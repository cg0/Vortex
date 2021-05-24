package uk.cg0.vortex.webserver.route

import uk.cg0.vortex.webserver.objects.Request
import uk.cg0.vortex.webserver.objects.Response

class TestHome: Route {
    override fun handleRoute(request: Request, response: Response) {
        response.writeString("This is a test homepage")
    }
}