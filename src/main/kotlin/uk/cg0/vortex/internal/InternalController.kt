package uk.cg0.vortex.internal

import uk.cg0.vortex.controller.Controller
import uk.cg0.vortex.template.FileNotFoundTemplate
import uk.cg0.vortex.template.GenericErrorTemplate
import uk.cg0.vortex.template.InternalServerErrorTemplate
import uk.cg0.vortex.webserver.enum.HttpStatus
import uk.cg0.vortex.webserver.objects.Response

class InternalController: Controller {
    fun fileNotFound(response: Response) {
        response.renderView(FileNotFoundTemplate())
    }

    fun internalServerError(response: Response) {
        response.renderView(InternalServerErrorTemplate())
    }

    fun genericError(response: Response, httpStatus: HttpStatus) {
        response.renderView(GenericErrorTemplate(httpStatus))
    }
}