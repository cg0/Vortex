package uk.cg0.vortex.middleware

import uk.cg0.vortex.webserver.objects.Request
import uk.cg0.vortex.webserver.objects.Response

interface Middleware {
    fun handleMiddleware(request: Request, response: Response, variables: HashMap<String, Any>)
}