package uk.cg0.vortex.database.conditionals

import uk.cg0.vortex.database.DatabaseTable

data class ColumnLinkingPredicate(val table: DatabaseTable): Predicate
