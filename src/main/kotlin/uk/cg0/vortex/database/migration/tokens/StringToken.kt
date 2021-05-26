package uk.cg0.vortex.database.migration.tokens

class StringToken(override var name: String): SchemaToken() {
    override val dataType: String
        get() = "VARCHAR(255)"

}