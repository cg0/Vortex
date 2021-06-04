package uk.cg0.vortex.webserver

import junit.framework.TestCase.*
import org.junit.Before
import org.junit.Test
import uk.cg0.vortex.Vortex
import uk.cg0.vortex.controller.Controller
import uk.cg0.vortex.webserver.enum.HttpVerb
import uk.cg0.vortex.webserver.objects.Request
import uk.cg0.vortex.webserver.objects.Response

class WebserverRoutingTests {
    @Before
    fun cleanup() {
        Vortex.routingEngine.clear()
    }

    @Test
    fun `Do we correctly get back the page index`() {
        class TestController: Controller {
            fun index(request: Request, response: Response) {}
        }
        Vortex.get("/", TestController::index)

        val controllerFunction = Vortex.routingEngine[HttpVerb.GET, "/"]
        assertNotNull(controllerFunction)
    }

    @Test
    fun `Do we get back null when requesting a resource that doesn't exist`() {
        val controllerFunction = Vortex.routingEngine[HttpVerb.GET, "/"]

        assertNull(controllerFunction)
    }

    @Test
    fun `Can we register a route on one domain without affecting another`() {
        class TestController: Controller {
            fun indexOne(request: Request, response: Response) {}
            fun indexTwo(request: Request, response: Response) {}
        }

        Vortex.get("/", TestController::indexOne)
        Vortex.get("foo.bar/", TestController::indexTwo)

        val indexOne = Vortex.routingEngine[HttpVerb.GET, "/"]
        val indexTwo = Vortex.routingEngine["foo.bar", HttpVerb.GET, "/"]

        assertNotNull(indexOne)
        assertNotNull(indexTwo)
        assertNotSame(indexOne, indexTwo)
    }

    @Test
    fun `Can we register routes on other domains`() {
        class TestController: Controller {
            fun index(request: Request, response: Response) {}
        }
        Vortex.get("foo.bar/", TestController::index)

        val controllerFunction = Vortex.routingEngine["foo.bar", HttpVerb.GET, "/"]
        assertNotNull(controllerFunction)
    }

    @Test
    fun `Can we register a route variable`() {
        class TestController: Controller {
            fun index(request: Request, response: Response) {}
        }
        Vortex.get("/test/{name}/somethingelse/{name2}", TestController::index)

        val controllerFunction = Vortex.routingEngine[HttpVerb.GET, "/test/Connor/somethingelse/Amy"]
        assertNotNull(controllerFunction)
        assertTrue(controllerFunction?.variables?.size == 2)
        assertEquals("Connor", controllerFunction?.variables?.get("name"))
        assertEquals("Amy", controllerFunction?.variables?.get("name2"))
    }

    @Test
    fun `Can we register static responses to variable routes`() {
        class TestController: Controller {
            fun index(request: Request, response: Response) {}
        }
        Vortex.get("/test/{foo}", TestController::index)
        Vortex.get("/test/foo", TestController::index)

        val controllerFunctionStatic = Vortex.routingEngine[HttpVerb.GET, "/test/foo"]
        assertNotNull(controllerFunctionStatic)
        assertEquals(0, controllerFunctionStatic?.variables?.size)

        val controllerFunctionVariable = Vortex.routingEngine[HttpVerb.GET, "/test/other"]
        assertNotNull(controllerFunctionVariable)
        assertEquals(1, controllerFunctionVariable?.variables?.size)
    }
}