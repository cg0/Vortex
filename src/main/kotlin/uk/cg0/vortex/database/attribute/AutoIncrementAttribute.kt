package uk.cg0.vortex.database.attribute

class AutoIncrementAttribute: DatabaseAttribute {
    override fun getAttribute(): String {
        return "AUTO_INCREMENT"
    }
}