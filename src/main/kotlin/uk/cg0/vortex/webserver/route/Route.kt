package uk.cg0.vortex.webserver.route

import uk.cg0.vortex.webserver.objects.Request
import uk.cg0.vortex.webserver.objects.Response

interface Route {
    fun handleRoute(request: Request, response: Response)
}