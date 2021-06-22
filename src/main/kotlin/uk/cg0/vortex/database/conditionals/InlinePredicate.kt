package uk.cg0.vortex.database.conditionals

import uk.cg0.vortex.database.DatabaseColumn

data class InlinePredicate(val column: DatabaseColumn<*>, val value: Any, val condition: String): Predicate