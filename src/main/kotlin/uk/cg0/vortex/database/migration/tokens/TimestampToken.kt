package uk.cg0.vortex.database.migration.tokens

class TimestampToken(override var name: String): SchemaToken() {
    override val dataType: String
        get() = "TIMESTAMP"

}