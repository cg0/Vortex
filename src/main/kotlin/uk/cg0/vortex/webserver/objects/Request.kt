package uk.cg0.vortex.webserver.objects

import uk.cg0.vortex.webserver.enum.HttpVerb
import uk.cg0.vortex.webserver.enum.HttpVersion

data class Request(val httpVerb: HttpVerb,
                   val route: String,
                   val httpVersion: HttpVersion,
                   val headers: HashMap<String, String>,
                   val get: HashMap<String, String>,
                   val post: HashMap<String, String>,
                   val body: String)
