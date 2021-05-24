package uk.cg0.vortex.webserver.route

import uk.cg0.vortex.webserver.enum.HttpStatus
import uk.cg0.vortex.webserver.objects.Request
import uk.cg0.vortex.webserver.objects.Response

class NotFoundRoute: Route {
    override fun handleRoute(request: Request, response: Response) {
        response.statusCode = HttpStatus.NOT_FOUND
        response.writeString("404 File Not Found")
    }
}