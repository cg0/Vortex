package uk.cg0.vortex.database.migration.tokens

class DoubleToken(override var name: String): SchemaToken() {
    override val dataType: String
        get() = "DOUBLE"

}