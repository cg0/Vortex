package uk.cg0.vortex.database.migration.tokens

class BitToken(override var name: String): SchemaToken() {
    override val dataType: String
        get() = "BIT"

}