package uk.cg0.vortex

import uk.cg0.vortex.config.Config
import uk.cg0.vortex.controller.ControllerFunction
import uk.cg0.vortex.database.Database
import uk.cg0.vortex.database.DatabaseTable
import uk.cg0.vortex.internal.InternalController
import uk.cg0.vortex.middleware.Middleware
import uk.cg0.vortex.webserver.enum.HttpStatus
import uk.cg0.vortex.webserver.enum.HttpVerb
import uk.cg0.vortex.webserver.routing.RoutingEngine
import uk.cg0.vortex.webserver.thread.HttpServerThread
import kotlin.reflect.KFunction

object Vortex {
    val routingEngine = RoutingEngine()
    val config = Config()
    val database: Database
    val version: String by lazy {
        Vortex::class.java.`package`.implementationVersion ?: "UNKNOWN"
    }

    init {
        config.load(".env")
        database = Database(config["DB_HOST"],
            config["DB_DATABASE"],
            config["DB_USERNAME"],
            config["DB_PASSWORD"])

        routingEngine.registerError(HttpStatus.NOT_FOUND, InternalController::fileNotFound)
        routingEngine.registerError(HttpStatus.INTERNAL_SERVER_ERROR, InternalController::internalServerError)
        routingEngine.registerGenericError(InternalController::genericError)
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

        HttpServerThread().run()
    }

    /**
     * Registers a GET route with the specified path
     *
     * @param path The full path to register, domains are optional
     * @param function A reference to the controller function
     * @param middleware A list of middleware to run before the controller
     */
    fun get(path: String, function: KFunction<Any>, middleware: ArrayList<Middleware> = ArrayList()) {
        routingEngine[HttpVerb.GET, path] = ControllerFunction(function, middleware)
    }

    /**
     * Registers a POST route with the specified path
     *
     * @param path The full path to register, domains are optional
     * @param function A reference to the controller function
     * @param middleware A list of middleware to run before the controller
     */
    fun post(path: String, function: KFunction<Unit>, middleware: ArrayList<Middleware> = ArrayList()) {
        routingEngine[HttpVerb.POST, path] = ControllerFunction(function, middleware)
    }

    /**
     * Registers a PUT route with the specified path
     *
     * @param path The full path to register, domains are optional
     * @param function A reference to the controller function
     * @param middleware A list of middleware to run before the controller
     */
    fun put(path: String, function: KFunction<Unit>, middleware: ArrayList<Middleware> = ArrayList()) {
        routingEngine[HttpVerb.PUT, path] = ControllerFunction(function, middleware)
    }

    /**
     * Registers a PATCH route with the specified path
     *
     * @param path The full path to register, domains are optional
     * @param function A reference to the controller function
     * @param middleware A list of middleware to run before the controller
     */
    fun patch(path: String, function: KFunction<Unit>, middleware: ArrayList<Middleware> = ArrayList()) {
        routingEngine[HttpVerb.PATCH, path] = ControllerFunction(function, middleware)
    }

    /**
     * Registers a DELETE route with the specified path
     *
     * @param path The full path to register, domains are optional
     * @param function A reference to the controller function
     * @param middleware A list of middleware to run before the controller
     */
    fun delete(path: String, function: KFunction<Unit>, middleware: ArrayList<Middleware> = ArrayList()) {
        routingEngine[HttpVerb.DELETE, path] = ControllerFunction(function, middleware)
    }

    /**
     * Migrates a set of databases to new database schema if needed
     *
     * @param tables An list of database table objects
     */
    fun migrateTables(tables: ArrayList<DatabaseTable>) {
        for (table in tables) {
            database.handleMigration(table)
        }
    }
}