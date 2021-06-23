package uk.cg0.vortex.database.conditionals

import uk.cg0.vortex.database.DatabaseColumn

data class ColumnInlinePredicate(val column: DatabaseColumn<*>,
                                 val value: DatabaseColumn<*>,
                                 val condition: String): Predicate