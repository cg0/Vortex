package uk.cg0.vortex.database

class DatabaseRelation<T>(val localKey: DatabaseColumn<*>, val foreignKey: DatabaseColumn<*>,
                       val relationType: RelationType) {
    fun get(localValue: Any): Any {
        val data = foreignKey.table.where(foreignKey, localValue).get()

        return when (relationType) {
            RelationType.ONE_TO_ONE -> {
                data.first()
            }
            RelationType.ONE_TO_MANY -> {
                data
            }
        }
    }

    enum class RelationType {
        ONE_TO_ONE,
        ONE_TO_MANY
    }
}