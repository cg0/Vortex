package uk.cg0.vortex

import uk.cg0.vortex.config.Config
import uk.cg0.vortex.controller.Controller
import uk.cg0.vortex.controller.ControllerFunction
import uk.cg0.vortex.database.Database
import uk.cg0.vortex.database.migration.MigrationHandler
import uk.cg0.vortex.internal.InternalController
import uk.cg0.vortex.migrations.CreateMigrationsTable
import uk.cg0.vortex.webserver.enum.HttpStatus
import uk.cg0.vortex.webserver.enum.HttpVerb
import uk.cg0.vortex.webserver.routing.RouteDirectory
import uk.cg0.vortex.webserver.routing.RouteNode
import uk.cg0.vortex.webserver.routing.RoutingEngine
import uk.cg0.vortex.webserver.thread.HttpServerThread
import kotlin.reflect.KClass
import kotlin.reflect.KFunction

object Vortex {
    val routingEngine = RoutingEngine()
    val config = Config()
    val database: Database
    val migrationHandler = MigrationHandler()

    init {
        config.load(".env")
        database = Database(config["DB_HOST"],
            config["DB_DATABASE"],
            config["DB_USERNAME"],
            config["DB_PASSWORD"])

        routingEngine.registerError(HttpStatus.NOT_FOUND, InternalController::class, InternalController::fileNotFound)
        routingEngine.registerError(HttpStatus.INTERNAL_SERVER_ERROR, InternalController::class,
            InternalController::internalServerError)
    }

    /**
     * Starts the Vortex web framework
     *
     * This will start the HTTP server and connect up the command processor
     *
     * @param args Command line arguments to be passed to the command processor
     */
    fun start(args: Array<String>) {
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

        migrationHandler.migrate(arrayListOf(CreateMigrationsTable()))
        HttpServerThread().run()
    }

    fun getVersion(): String {
        return "0.0.1";
    }

    /**
     * Registers a GET route with the specified path
     *
     * @param path The full path to register, domains are optional
     * @param controller A reference to the controller class
     * @param function A reference to the controller function
     */
    fun get(path: String, controller: KClass<*>, function: KFunction<Unit>) {
        routingEngine.addRoute(HttpVerb.GET, path, ControllerFunction(controller, function))
    }

    /**
     * Registers a POST route with the specified path
     *
     * @param path The full path to register, domains are optional
     * @param controller A reference to the controller class
     * @param function A reference to the controller function
     */
    fun post(path: String, controller: KClass<*>, function: KFunction<Unit>) {
        routingEngine.addRoute(HttpVerb.POST, path, ControllerFunction(controller, function))
    }

    /**
     * Registers a PUT route with the specified path
     *
     * @param path The full path to register, domains are optional
     * @param controller A reference to the controller class
     * @param function A reference to the controller function
     */
    fun put(path: String, controller: KClass<*>, function: KFunction<Unit>) {
        routingEngine.addRoute(HttpVerb.PUT, path, ControllerFunction(controller, function))
    }

    /**
     * Registers a PATCH route with the specified path
     *
     * @param path The full path to register, domains are optional
     * @param controller A reference to the controller class
     * @param function A reference to the controller function
     */
    fun patch(path: String, controller: KClass<*>, function: KFunction<Unit>) {
        routingEngine.addRoute(HttpVerb.PATCH, path, ControllerFunction(controller, function))
    }

    /**
     * Registers a DELETE route with the specified path
     *
     * @param path The full path to register, domains are optional
     * @param controller A reference to the controller class
     * @param function A reference to the controller function
     */
    fun delete(path: String, controller: KClass<*>, function: KFunction<Unit>) {
        routingEngine.addRoute(HttpVerb.DELETE, path, ControllerFunction(controller, function))
    }
}