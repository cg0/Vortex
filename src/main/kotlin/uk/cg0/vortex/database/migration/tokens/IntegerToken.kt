package uk.cg0.vortex.database.migration.tokens

class IntegerToken(override var name: String): SchemaToken() {
    fun autoIncrement(): IntegerToken {
        this.attributes.add("AUTO_INCREMENT")
        return this
    }

    override val dataType: String
        get() = "INTEGER"
}