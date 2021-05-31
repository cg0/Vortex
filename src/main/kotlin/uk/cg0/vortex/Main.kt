package uk.cg0.vortex

import uk.cg0.vortex.controller.ControllerFunction
import uk.cg0.vortex.migrations.CreateMigrationsTable
import uk.cg0.vortex.test.Test
import uk.cg0.vortex.test.TestController
import uk.cg0.vortex.webserver.enum.HttpVerb
import uk.cg0.vortex.webserver.route.TestHome
import uk.cg0.vortex.webserver.thread.HttpServerThread

fun main() {
    val logo = arrayListOf(
        "____   ____            __                 ",
        "\\   \\ /   /___________/  |_  ____ ___  ___",
        " \\   Y   /  _ \\_  __ \\   __\\/ __ \\\\  \\/  /",
        "  \\     (  <_> )  | \\/|  | \\  ___/ >    < ",
        "   \\___/ \\____/|__|   |__|  \\___  >__/\\_ \\",
        "                                \\/      \\/"
    )

    for (line in logo) {
        println(line)
    }

    // Move later
    Vortex.routingEngine.addRoute(HttpVerb.GET, "test", ControllerFunction(TestController::class, TestController::test))

    Vortex.migrationHandler.migrate(arrayListOf(CreateMigrationsTable()))

    HttpServerThread().run()
}