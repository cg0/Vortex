package uk.cg0.vortex.database

class DatabaseRelation(val localKey: DatabaseColumn<*>, val foreignKey: DatabaseColumn<*>,
                       val relationType: RelationType) {

    enum class RelationType {
        ONE_TO_ONE,
        ONE_TO_MANY
    }
}