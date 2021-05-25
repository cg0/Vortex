package uk.cg0.vortex

import uk.cg0.vortex.migrations.CreateMigrationsTable
import uk.cg0.vortex.test.Test
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
    Vortex.routingEngine["/"] = TestHome()
    Test.test()

    CreateMigrationsTable().up()

    HttpServerThread().run()
}