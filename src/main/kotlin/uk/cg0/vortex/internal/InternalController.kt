package uk.cg0.vortex.internal

import uk.cg0.vortex.controller.Controller
import uk.cg0.vortex.template.FileNotFoundTemplate
import uk.cg0.vortex.template.InternalServerErrorTemplate
import uk.cg0.vortex.webserver.objects.Request
import uk.cg0.vortex.webserver.objects.Response

class InternalController: Controller {
    fun fileNotFound(request: Request, response: Response) {
        response.renderView(FileNotFoundTemplate())
    }

    fun internalServerError(request: Request, response: Response) {
        response.renderView(InternalServerErrorTemplate())
    }
}