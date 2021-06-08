package uk.cg0.vortex.controller

import uk.cg0.vortex.webserver.objects.Request
import uk.cg0.vortex.webserver.objects.Response

interface Respondable {
    fun handleResponse(request: Request, response: Response)
}