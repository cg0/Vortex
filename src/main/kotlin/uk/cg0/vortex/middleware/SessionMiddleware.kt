package uk.cg0.vortex.middleware

import uk.cg0.vortex.Vortex
import uk.cg0.vortex.controller.respondable.Respondable
import uk.cg0.vortex.controller.respondable.ServerError
import uk.cg0.vortex.webserver.enum.HttpStatus
import uk.cg0.vortex.webserver.objects.Request
import uk.cg0.vortex.webserver.objects.Response

class SessionMiddleware: Middleware {
    override fun handleMiddleware(request: Request, response: Response, variables: HashMap<String, Any>): Respondable? {
        if ("session" in request.cookies) {
            if (request.cookies["session"] in Vortex.sessions) {
                variables["session"] = Vortex.sessions[request.cookies["session"]]
                    ?: return ServerError(HttpStatus.INTERNAL_SERVER_ERROR)
            } else {
                // Session gone from server
                variables["session"] = response.createSession()
            }
        } else {
            variables["session"] = response.createSession()
        }

        return null
    }
}