package uk.cg0.vortex.database.attribute

class DefaultAttribute(private val default: Any): DatabaseAttribute {
    override fun getAttribute(): String {
        return "DEFAULT '$default'"
    }
}