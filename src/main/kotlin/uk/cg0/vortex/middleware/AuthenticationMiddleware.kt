package uk.cg0.vortex.middleware

import uk.cg0.vortex.Vortex
import uk.cg0.vortex.controller.respondable.Respondable
import uk.cg0.vortex.controller.respondable.ServerError
import uk.cg0.vortex.webserver.enum.HttpStatus
import uk.cg0.vortex.webserver.objects.Request
import uk.cg0.vortex.webserver.objects.Response

class AuthenticationMiddleware: Middleware {
    override fun handleMiddleware(request: Request, response: Response, variables: HashMap<String, Any>): Respondable? {
        if ("session" !in variables) {
            return ServerError(HttpStatus.NOT_FOUND)
        }

        val session = variables["session"]
        if (session !is HashMap<*, *>) {
            return ServerError(HttpStatus.NOT_FOUND)
        }

        if ("user" !in session) {
            return ServerError(HttpStatus.NOT_FOUND)
        }

        variables["user"] = session["user"] as Any
        return null
    }
}