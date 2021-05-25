package uk.cg0.vortex.database.migration

fun interface Blueprint {
    fun invoke()

    fun id() {

    }

    fun string(key: String) {

    }

    fun integer(key: String) {

    }

    fun double(key: String) {

    }

    fun boolean(key: String) {

    }

    fun timestamp(key: String) {

    }

    fun timestamps() {
        this.timestamp("created_at")
        this.timestamp("updated_at")
    }

    fun softDeletes() {
        this.timestamp("deleted_at")
    }

    fun dropColumns(vararg column: String) {

    }

    fun dropSoftDeletes() {
        this.dropColumns("deleted_at")
    }

    fun dropTimestamps() {
        this.dropColumns("created_at", "updated_at")
    }
}