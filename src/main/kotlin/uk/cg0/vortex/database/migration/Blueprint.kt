package uk.cg0.vortex.database.migration

import uk.cg0.vortex.database.Schema
import uk.cg0.vortex.database.migration.tokens.*

fun interface Blueprint {
    fun invoke(blueprintTokens: BlueprintTokeniser)

    class BlueprintTokeniser() {
        val tokens = ArrayList<SchemaToken>()

        fun id(): IntegerToken {
            return integer("id").autoIncrement().primaryKey() as IntegerToken
        }

        fun string(key: String): StringToken {
            val token = StringToken(key)
            tokens.add(token)
            return token
        }

        fun integer(key: String): IntegerToken {
            val token = IntegerToken(key)
            tokens.add(token)
            return token
        }

        fun double(key: String): DoubleToken {
            val token = DoubleToken(key)
            tokens.add(token)
            return token
        }

        fun bit(key: String): BitToken {
            val token = BitToken(key)
            tokens.add(token)
            return token
        }

        fun boolean(key: String): BitToken {
            return bit(key)
        }

        fun timestamp(key: String): TimestampToken {
            val token = TimestampToken(key)
            tokens.add(token)
            return token
        }

        fun timestamps() {
            this.timestamp("created_at")
            this.timestamp("updated_at")
        }

        fun softDeletes() {
            this.timestamp("deleted_at")
        }

        fun dropColumns(vararg columns: String) {
            for (column in columns) {
                tokens.add(DropColumnToken(column))
            }
        }

        fun dropSoftDeletes() {
            this.dropColumns("deleted_at")
        }

        fun dropTimestamps() {
            this.dropColumns("created_at", "updated_at")
        }

        fun dropTable(): DropTableToken {
            return DropTableToken()
        }
    }
}