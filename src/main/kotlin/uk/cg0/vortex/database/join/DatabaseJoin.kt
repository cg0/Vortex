package uk.cg0.vortex.database.join

import uk.cg0.vortex.database.conditionals.Predicate

data class DatabaseJoin(val joinType: JoinType, val predicate: Predicate) {
    enum class JoinType {
        INNER_JOIN,
        LEFT_JOIN,
        RIGHT_JOIN,
        CROSS_JOIN;

        override fun toString(): String {
            val words = this.name.split("_")
            return words.joinToString(" ")
        }
    }
}